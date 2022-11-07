package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.*;
import main.java.org.htwg.konstanz.metaproject.persistance.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author MaWeissh, StChiari, FaAmbros, FaHocur, AlVeliu,
 * JoFesenm,SiKelle,PaDrautz
 * @version 1.5
 */
@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/public")
public class PublicEndpoint {
    private static final Logger log = LoggerFactory.getLogger(PublicEndpoint.class);

    private final MetaprojectDAO metaprojectDao;

    private final ProjectDAO projectDao;

    private final TeamDAO teamDao;

    private final RelationTeamUserDAO relationTeamUserDao;

    private final ProjectFieldAccessDAO projectFieldAccessDAO;

    public PublicEndpoint(MetaprojectDAO metaprojectDao, ProjectDAO projectDao, TeamDAO teamDao, RelationTeamUserDAO relationTeamUserDao, ProjectFieldAccessDAO projectFieldAccessDAO) {
        this.metaprojectDao = metaprojectDao;
        this.projectDao = projectDao;
        this.teamDao = teamDao;
        this.relationTeamUserDao = relationTeamUserDao;
        this.projectFieldAccessDAO = projectFieldAccessDAO;
    }

    @GetMapping(value = "metaproject")
    public ResponseEntity<Object> getPublicMetaprojectList() {
        log.info("Request <-- GET /public/metaproject");
        return ResponseEntity.ok(metaprojectDao.findAllVisible());
    }

    @GetMapping(value = "metaproject/{id}")
    public ResponseEntity<Object> getPublicMetaproject(@PathVariable long id) {
        Metaproject metaproject = metaprojectDao.findById(id);
        if (metaproject == null)
            return ResponseEntity.ok("{}");
        else if (metaproject.isVisible())
            return ResponseEntity.ok(metaproject);
        else
            return ResponseEntity.badRequest().build();
    }

    /**
     * Returns a list of all projects of a given metaproject.
     */
    @GetMapping(value = "metaproject/{id}/project")
    public ResponseEntity<Object> getPublicProjectsOfMetaproject(@PathVariable long id) {
        Collection<Project> projects = projectDao.findByMetaproject(id);

        if (projects == null)
            return ResponseEntity.ok("[]");
        /*
        Collection<Project> linkedList = new LinkedList<Project>();
        for (Project project : projects) {
            if (project.getMetaproject().isVisible())
                linkedList.add(this.replaceFieldValues(this.copy(project)));
             else
                linkedList.add(this.replaceAllFieldValues(this.copy(project)));
        }
        return Response.ok(linkedList).build();
        */
        return ResponseEntity.ok(projects);
    }

    /**
     * Returns an object of a given project.
     */
    @GetMapping(value = "metaproject/{id}/project/{pid}")
    public ResponseEntity<Object> getPublicProjectInformation(@PathVariable(value = "id") long mpId, @PathVariable long pid) {
        Project project = projectDao.findById(pid);
        Metaproject metaproject = metaprojectDao.findById(mpId);

        if (metaproject != null && project != null) {
            Project project1 = this.copy(project);
            if (!metaproject.isVisible())
                project1 = this.replaceAllFieldValues(project1);
            else
                project1 = this.replaceFieldValues(project1);
            return ResponseEntity.ok(project1);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Returns an object of a given project.
     * Attention:   Due to some problems while trying to access the Route above, we needed two factories with different routes.
     */
    @GetMapping(value = "project/{pid}")
    public ResponseEntity<Object> getPublicProjectInformation(@PathVariable long pid) {
        Project project = projectDao.findById(pid);
        if (project != null) {
            Project resultProject = this.copy(project);
            if (!project.getMetaproject().isVisible())
                resultProject = this.replaceAllFieldValues(resultProject);
            else
                resultProject = this.replaceFieldValues(resultProject);
            return ResponseEntity.ok(resultProject);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Returns a Project with anonymized properties
     */
    private Project replaceFieldValues(Project project) {
        Collection<ProjectFieldAccess> pfa = projectFieldAccessDAO.findByProjectId(project.getProjectId());
        if (pfa == null) {
            return project;
        }
        for (ProjectFieldAccess pfaElement : pfa) {
            if (pfaElement.getField().equals("projectLeader")) {
                // Creates an anonymous User
                User anon = new User();
                anon.setUserName("Anonym");
                anon.setUserFirstName("Anonym");
                anon.setUserLastName("Anonym");
                this.replaceField(project, pfaElement.getField(), anon);
            } else {
                this.replaceField(project, pfaElement.getField(), "Privat");
            }
        }

        return project;
    }/**/

    /**
     * Returns a Project with anonymized properties
     */
    private Project replaceAllFieldValues(Project project) {
        if (project == null)
            return null;
        for (String element : Constants.PRIVATE_FIELDS) {
            if (element.equals("projectLeader")) {
                // Creates an anonymous User
                User anon = new User();
                anon.setUserName("Anonym");
                anon.setUserFirstName("Anonym");
                anon.setUserLastName("Anonym");
                this.replaceField(project, element, anon);
            } else {
                this.replaceField(project, element, "Privat");
            }
        }
        return project;
    }/**/

    /**
     * Replaces the passed field with an anonymous value.
     */
    private void replaceField(Object object, String property, Object value) {
        try {
            PropertyUtils.setSimpleProperty(object, property, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.warn(e.getMessage());
        }
    }

    /**
     * creates a transient copy of passed Project.
     * Attention: If a persistent Object is used, the property will be persistent!
     */
    private Project copy(Project p) {
        Project project = new Project();
        Field[] fields = p.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                PropertyUtils.setSimpleProperty(project, field.getName(), PropertyUtils.getSimpleProperty(p, field.getName()));
            } catch (Exception e) {
                log.warn(e.getMessage());
            }/**/
        }
        return project;
    }

    /**
     * Get all Teams and number of member from Database
     */
    @GetMapping(value = "metaproject/{metaId}/members")
    public ResponseEntity<Object> getMembersPerMeta(@PathVariable Long metaId) {

        Collection<Project> projects = projectDao.findByMetaproject(metaId);
        Collection<Team> teams = teamDao.findByMetaprojectId(metaId);
        log.info("getAllTeams");

        HashMap<Long, Integer> projectTeamMapping = new HashMap<>();

        for (Project project : projects) {
            for (Team team : teams) {
                long teamid = team.getProjectId().getProjectId();
                if (teamid == project.getProjectId()) {
                    Collection<RelationTeamUser> countMembers = relationTeamUserDao.findTeammemberByTeam(team);
                    projectTeamMapping.put(project.getProjectId(), countMembers.size());
                }
            }
        }

        return ResponseEntity.ok(projectTeamMapping);
    }

    @GetMapping(value = "metaproject/{metaid}/team/{pid}/project")
    public ResponseEntity<Object> getNumberOfTeamMembersFromProjectID(@PathVariable Long metaid, @PathVariable Long pid) {
        Team team = teamDao.findByProjectId(pid);
        Collection<RelationTeamUser> result = relationTeamUserDao.findByTeam(team);
        log.info("Anzahl treffer (getNumberOfTeamsFromProjectID):" + result.size());
        return ResponseEntity.ok(result.size());
    }

    /**
     * Get all dashboard data for info panel depending on a certain metaproject
     *
     * @return hashmap
     */
    @GetMapping(value = "metaproject/{metaid}/dashboard")
    public ResponseEntity<Object> getDashboardData(@PathVariable Long metaid) {
        HashMap<String, Long> response = new HashMap<>();

        //1. number of projects in metaproject
        long numberOfProjects = this.getNumberOfProjects(metaid);
        response.put("numberOfProjects", numberOfProjects);

        //2. number of teams in metaproject
        long numberOfTeams = this.getNumberOfTeams(metaid);
        response.put("numberOfTeams", numberOfTeams);

        //3. number of free places
        long numberOfFreePlaces = this.getNumberOfFreePlaces(metaid);
        response.put("numberOfFreePlaces", numberOfFreePlaces);

        //4. current users
        long numberOfCurrentUsers = this.getNumberOfCurrentUsers();
        response.put("numberOfCurrentUsers", numberOfCurrentUsers);

        response.put("isSingleRegistration", this.checkIfSingleRegistration(metaid));

        return ResponseEntity.ok(response);
    }

    /**
     * Get all dashboard data (number of projects, number of teams, number of overall free places, amount of current users)
     *
     * @return HashMap<String, Long>
     */
    @GetMapping(value = "metaproject/dashboard")
    public ResponseEntity<Object> getDashboardData() {
        HashMap<String, Long> response = new HashMap<>();

        //1. number of projects in metaproject
        long numberOfProjects = this.getNumberOfProjects();
        response.put("numberOfProjects", numberOfProjects);

        //2. number of teams in metaproject
        long numberOfTeams = this.getNumberOfTeams();
        response.put("numberOfTeams", numberOfTeams);

        //3. number of free places in all projects
        long numberOfFreePlaces = this.getNumberOfFreePlaces();
        response.put("numberOfFreePlaces", numberOfFreePlaces);

        //4. current users
        long numberOfCurrentUsers = this.getNumberOfCurrentUsers();
        response.put("numberOfCurrentUsers", numberOfCurrentUsers);

        return ResponseEntity.ok(response);
    }

    private long checkIfSingleRegistration(long metaid) {

        Metaproject metaproject = metaprojectDao.findById(metaid);
        if (metaproject.getRegisterType().equals("Single")) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * get the number of projects in whole app
     */
    private long getNumberOfProjects() {
        return projectDao.findAll().size();
    }

    /**
     * get number of projects within a metaproject
     */
    private long getNumberOfProjects(long metaid) {
        return projectDao.findByMetaproject(metaid).size();
    }

    /**
     * get number of teams within metaproject
     */
    private long getNumberOfTeams(long metaid) {
        long numberOfTeams = 0;

        if (!(metaprojectDao.findById(metaid).getRegisterType().equals("Single")))
            numberOfTeams = teamDao.findByMetaprojectId(metaid).size();

        return numberOfTeams;
    }

    /**
     * get number of teams in whole app
     */
    private long getNumberOfTeams() {
        long numberOfTeams = teamDao.findAll().size();
        return numberOfTeams;
    }

    /**
     * Returns the number of current users depending on active connections at wildfly server
     * For retrieving the active connections the https management api of Wildfly is used
     */
    private long getNumberOfCurrentUsers() {
        long numberOfCurrentUsers = 0;
        String response = "";
        try {

            //query string which requests the active connections on wildfly server
            String urlStr = "http://metaproject2.in.fhkn.de:9990/management/subsystem/datasources/data-source/MySQLDS/statistics/pool?operation=attribute&name=ActiveCount";
            DefaultHttpClient httpclient = new DefaultHttpClient();
            //:flush-idle-connection-in-pool

            //"http://metaproject-tst.in.fhkn.de:9990/management/subsystem/datasources/data-source/MySQLDS/statistics/pool?operation=flush-idle-connection-in-pool"
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            //initialize httpclient
            httpclient.getCredentialsProvider().setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials("admin", "admin"));

            //describe type of request
            HttpGet httpget = new HttpGet(urlStr);

            //send request to wildfly and receive response
            response = httpclient.execute(httpget, responseHandler);
            //the string from response is handled as ""text"", the double apostrophe is not accepted as response,
            //therefore it has to be converted to a char array and then concatenated to a new string object

            char[] charArray = new char[response.length() - 1];
            String newString = "";
            int number = 0;
            for (int i = 1; i < response.length() - 1; i++) {
                //concat new
                newString = newString.concat(Character.toString(response.charAt(i)));
            }

            numberOfCurrentUsers = Long.parseLong(newString);

          /*  System.out.println("charArray"+charArray);
            String testParse = "9";
            long testParseLong = Long.parseLong(testParse);
            System.out.println("TestParseLong" + testParseLong);

            int numberparse = Integer.parseInt(response);
            System.out.println("Numberparse with int" + numberparse);

            numberOfCurrentUsers = (long) (number);*/

            return numberOfCurrentUsers;

        } catch (Exception e) {
            System.out.println(response);
            e.printStackTrace();
            return 999;
        }
    }

    /**
     * Returns the number of free places within the whole application
     */
    private long getNumberOfFreePlaces() {

        try {
            //get total number of teams *6 = total amount of places
            //total free places = total amount of places - amount of mapped users in a team
            long totalPlaces = 0;
            long totalOccupiedPlaces = 0;

            Collection<Metaproject> metaprojects = metaprojectDao.findAll();

            for (Metaproject metaproject : metaprojects) {
                Collection<Team> teams = teamDao.findByMetaprojectId(metaproject.getMetaprojectId());

                for (Team team : teams) {

                    //get the maximum team size
                    int maxTeamSize = team.getMetaProjectId().getTeamMaxSize();
                    totalPlaces += maxTeamSize;

                    //get all relations where team is involved , this is the number of members in a team
                    Collection<RelationTeamUser> numberFreePlaces = relationTeamUserDao.findByTeam(team);
                    totalOccupiedPlaces += numberFreePlaces.size();
                }
            }

            //add sizes of projects with single registration
            totalPlaces += this.getPlacesInSingleRegistration();

            return totalPlaces - totalOccupiedPlaces; // total free places

        } catch (Exception e) {
            return 999;
        }
    }

    /**
     * Returns the number of projects within a given metaproject
     */
    private long getNumberOfFreePlaces(long metaid) {
        long totalFreePlaces = 0;

        try {
            HashMap<String, Long> teamPlaces = this.getTotalPlacesTeamRegistration(metaid);
            long totalPlacesFromSingleRegistration = this.getTotalPlacesSingleRegistration(metaid);

            //total places from single and team registration
            long totalPlaces = totalPlacesFromSingleRegistration;
            totalPlaces += teamPlaces.get("totalPlaces");

            //total places which have been already occupied by a user
            long totalOccupiedPlaces = teamPlaces.get("totalOccupiedPlaces");

            if (totalPlaces > totalOccupiedPlaces)
                totalFreePlaces = totalPlaces - totalOccupiedPlaces;
            return totalFreePlaces;
        } catch (Exception e) {
            return 999;
        }
    }

    private long getTotalPlacesSingleRegistration(long metaid) {
        try {
            long totalPlaces = 0;
            Metaproject metaproject = metaprojectDao.findById(metaid);
            if (metaproject.getRegisterType().equals("Single")) {
                Collection<Project> projects = projectDao.findByMetaproject(metaid);
                for (Project project : projects) {
                    totalPlaces += project.getMaxAmountMember();
                }
            }
            return totalPlaces;
        } catch (Exception e) {
            return 999;
        }
    }

    /**
     * returns the total places and occupied places in a metaproject
     */
    private HashMap<String, Long> getTotalPlacesTeamRegistration(long metaid) {
        try {
            long totalPlaces = 0;
            long totalOccupiedPlaces = 0;
            HashMap<String, Long> response = new HashMap<>();

            if (!(metaprojectDao.findById(metaid).getRegisterType().equals("Single"))) {

                // get number of places in team registration projects
                Collection<Team> teams = teamDao.findByMetaprojectId(metaid);

                //get the maximum team size
                for (Team team : teams) {
                    int maxTeamSize = team.getMetaProjectId().getTeamMaxSize();
                    totalPlaces += maxTeamSize;

                    //get all relations where team is involved , this is the number of members in a team
                    Collection<RelationTeamUser> numberFreePlaces = relationTeamUserDao.findByTeam(team);
                    totalOccupiedPlaces += numberFreePlaces.size();
                }
            }
            response.put("totalPlaces", totalPlaces);
            response.put("totalOccupiedPlaces", totalOccupiedPlaces);
            return response;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * returns the sum of amount of places in all single registration projects
     */
    public long getPlacesInSingleRegistration() {
        try {
            long totalPlaces = 0;
            Collection<Metaproject> metaprojects = metaprojectDao.findByRegisterType("Single");
            for (Metaproject mp : metaprojects) {
                totalPlaces = getTotalPlacesSingleRegistration(mp.getMetaprojectId());
            }
            return totalPlaces;

        } catch (Exception e) {
            return 999;
        }
    }
}
