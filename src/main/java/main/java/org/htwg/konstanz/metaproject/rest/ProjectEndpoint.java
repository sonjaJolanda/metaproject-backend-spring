package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.dtos.CopyProjectDTO;
import main.java.org.htwg.konstanz.metaproject.entities.*;
import main.java.org.htwg.konstanz.metaproject.persistance.*;
import main.java.org.htwg.konstanz.metaproject.rights.DefaultRoles;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import main.java.org.htwg.konstanz.metaproject.rights.RoleProject;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import main.java.org.htwg.konstanz.metaproject.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author FaAmbros, StChiari, MaWeissh, FaHocur, AlVeliu, JoFesenm
 * @version 1.2
 */
@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/")
public class ProjectEndpoint {

    private final static Logger log = LoggerFactory.getLogger(ProjectEndpoint.class);

    private final RightService rightService;

    private final TokenService tokenService;

    private final CopyService copyService;

    private final UserDAO userDao;

    private final RoleDAO roleDao;

    private final TeamDAO teamDao;

    private final MetaprojectDAO metaDao;

    private final ProjectDAO projectDao;

    private final ProjectStatusChangeDAO projectStatusChangeDAO;

    private final FileUploadDAO fileUploadDao;

    private final PrioTeamProjectDAO prioTeamProjectDao;

    private final CommunicationService communicationService;

    private final CommunicationCreatorService communicationCreatorService;

    private final ProjectFieldAccessDAO projectFieldAccessDao;

    private final FailedINdigitTransferDAO failedINdigitTransferDAO;

    public ProjectEndpoint(RightService rightService, TokenService tokenService, CopyService copyService, UserDAO userDao, RoleDAO roleDao, TeamDAO teamDao, MetaprojectDAO metaDao, ProjectDAO projectDao, ProjectStatusChangeDAO projectStatusChangeDAO, FileUploadDAO fileUploadDao, PrioTeamProjectDAO prioTeamProjectDao, CommunicationService communicationService, CommunicationCreatorService communicationCreatorService, ProjectFieldAccessDAO projectFieldAccessDao, FailedINdigitTransferDAO failedINdigitTransferDAO) {
        this.rightService = rightService;
        this.tokenService = tokenService;
        this.copyService = copyService;
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.teamDao = teamDao;
        this.metaDao = metaDao;
        this.projectDao = projectDao;
        this.projectStatusChangeDAO = projectStatusChangeDAO;
        this.fileUploadDao = fileUploadDao;
        this.prioTeamProjectDao = prioTeamProjectDao;
        this.communicationService = communicationService;
        this.communicationCreatorService = communicationCreatorService;
        this.projectFieldAccessDao = projectFieldAccessDao;
        this.failedINdigitTransferDAO = failedINdigitTransferDAO;
    }

    @PostMapping(value = "metaproject/{metaid}/project")
    public ResponseEntity createProject(@PathVariable Long metaid, @RequestBody Project project, @RequestHeader String token)
            throws ParseException, IOException, INdigitApiService.HttpStatusCodeException {
        log.info("Request <-- POST /metaproject/{}/project", metaid);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_PROJECT_CREATE)
                .checkForMetaprojectRight(Rights.METAPROJECT_PROJECT_CREATE, metaid).validate();
        if (!hasRights)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Metaproject metaproject = metaDao.findById(metaid);
        project.setMetaprojectId(metaproject);

        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        parserSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date regStart = parserSDF.parse(metaproject.getProjectRegStart());
        Date now = new Date();
        // check whether project registration range is started in case user isn't superuser
        if (!rightService.newRightHandler(token).checkForSuperUser().validate() && !regStart.before(now)) {
            log.error("Project registration start date is not in past.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!validProjectLeader(project))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        if (!validProjectName(metaid, project.getProjectTitle())) {
            log.info("Project Title already existent");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("title_already_existent");
        }
        if (!validProject(metaid, project))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        if (!validSpecialization(metaid, project))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        if (!dateIsInFuture(metaid, project))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Project newProject = projectDao.save(project);
        User user = tokenService.getUserByToken(token);
        roleDao.addRoleProjectToUser(user, newProject, DefaultRoles.METAPROJECT_PROJECT_OWNER);

        // create and send a new communication object
        communicationCreatorService.sendCommProjectLeaderAppointment(user, newProject.getProjectLeader(), newProject);

        // Set roles for projectleader
        roleDao.addRoleProjectToUser(newProject.getProjectLeader(), newProject, DefaultRoles.METAPROJECT_PROJECT_LEADER);
        roleDao.addRoleMetaprojectToUser(newProject.getProjectLeader(), newProject.getMetaproject(), DefaultRoles.METAPROJECT_MEMBER);

        //A timestamp is set when a project is created and when a change of state takes place.
        SimpleDateFormat SDFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String curr_date = SDFormat.format(cal.getTime());

        newProject = projectDao.save(project);
        return sendProjectAndMembersToINdigit(newProject, curr_date);
    }

    @Transactional
    ResponseEntity<Object> sendProjectAndMembersToINdigit(Project project, String timeStamp) throws INdigitApiService.HttpStatusCodeException, IOException {
        int statusCode = failedINdigitTransferDAO.tryTransfer(project, timeStamp);
        // No matter which status code occurred during the transmission to INdigit, the Endpoint should return the project with status 200
        return ResponseEntity.ok(project);
    }

    public boolean validProjectName(long metaid, String projectName) {
        Collection<Project> projects = projectDao.findByMetaproject(metaid);

        for (Project entry : projects) {
            if (entry.getProjectTitle().equals(projectName))
                return false;
        }
        return true;
    }

    // check if project input fields are valid
    public boolean validProjectLeader(Project project) {
        RoleProject role = roleDao.findDefaultByKey(DefaultRoles.METAPROJECT_PROJECT_LEADER, RoleProject.class);
        boolean isValidProjectLeader = roleDao.findRelationUserRole(project.getProjectLeader(), role) != null;
        log.info("is valid project leader? -> " + isValidProjectLeader + " (roleId:" + role.getRoleId() + ") (leaderId:" + project.getProjectLeader().getUserId() + ")");
        return isValidProjectLeader;
    }

    // check if project input fields are valid
    public boolean validProject(long metaid, Project project) {
        return project.getProjectLeader() != null && project.getKickOffDate() != null && project.getProjectTitle() != null;
    }

    public boolean dateIsInFuture(long metaid, Project project) {
        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            parserSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date now = new Date();
            Date kickOffStartDate = parserSDF.parse(project.getKickOffDate());
            return (kickOffStartDate.after(now));
        } catch (ParseException e) {
            return false;
        }
    }

    // check if specialization sum is 100%
    public boolean validSpecialization(long metaid, Project project) {
        if (project.getSpecialisation().isEmpty())
            return true;

        int sum = 0;
        for (SpecialisationProject sp : project.getSpecialisation()) {
            sum += sp.getSpecialisationProportion();
        }
        return sum == 100;
    }


    @GetMapping(value = "metaproject/{metaid}/project")
    public ResponseEntity getAllProjects(@PathVariable("metaid") Long metaid, @RequestHeader String token) {
        log.info("Request <-- GET /metaproject/{}/project", metaid);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_VIEW_DETAILS, metaid)
                .checkForAppRight(Rights.METAPROJECT_VIEW_DETAILS).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(projectDao.findByMetaproject(metaid));
    }

    @PutMapping(value = "metaproject/{metaid}/project/{id}")
    public ResponseEntity updateProject(@PathVariable Long metaid, @PathVariable Long
            id, @RequestBody Project project, @RequestHeader String token) throws IOException, INdigitApiService.HttpStatusCodeException {
        log.info("Request <-- PUT /metaproject/{}/project/{}", metaid, id);

        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_PROJECT_EDIT, metaid)
                .checkForProjectRight(Rights.METAPROJECT_PROJECT_EDIT, id).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!uniqueExistingMetaprojectName(metaid, project)) {
            log.info("Project Title already existent");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("title_already_existent");
        }
        if (!validSpecialization(metaid, project))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        if (!validProject(metaid, project))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        if (!validProjectLeader(project))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        // project is valid continue
        Project oldProject = projectDao.findById(id);
        boolean oldProjectIsAssigned = oldProject.getIsAssigned();
        User oldLeader = oldProject.getProjectLeader();
        Metaproject metaproject = metaDao.findById(metaid);
        String oldTimeStamp = oldProject.getTimeStamp();

        Project updatedProject = projectDao.update(id, project);
        User newLeader = updatedProject.getProjectLeader();

        if (oldLeader.getUserId() != newLeader.getUserId()) {
            roleDao.removeRoleProjectFromUser(oldLeader, oldProject, DefaultRoles.METAPROJECT_PROJECT_LEADER);

            boolean isLeader = false;
            //check if the old leader is project leader of another project
            Collection<Project> existingProjects = projectDao.findByMetaproject(metaid);
            for (Project pro : existingProjects) {
                if (pro.getProjectLeader().getUserId().equals(oldLeader.getUserId()))
                    isLeader = true;
            }

            if (!isLeader)
                roleDao.removeRoleMetaprojectFromUser(oldLeader, oldProject.getMetaproject(), DefaultRoles.METAPROJECT_MEMBER);

            roleDao.addRoleProjectToUser(newLeader, updatedProject, DefaultRoles.METAPROJECT_PROJECT_LEADER);
            roleDao.addRoleMetaprojectToUser(newLeader, updatedProject.getMetaproject(), DefaultRoles.METAPROJECT_MEMBER);

            User actionUser = tokenService.getUserByToken(token);
            communicationCreatorService.sendCommProjectLeaderChange(actionUser, newLeader, oldLeader, updatedProject);
        }

        if (updatedProject.getIsAssigned() && !oldProjectIsAssigned)
            communicationCreatorService.sendCommAssingProjectToTeamMaster(metaproject.getMetaprojectLeader(), metaproject, project);

        //A timestamp is set when a project is created and when a change of state takes place.
        //Convert oldTimeStamp to a new Timestamp, for example 28.07.2021 14:34 => YYYY-MM-DD'T'HH24:MI:SS
        String timeStamp;
        SimpleDateFormat SDFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        if (oldTimeStamp == null) {
            Calendar cal = Calendar.getInstance();
            timeStamp = SDFormat.format(cal.getTime());
        } else {
            try {
                SDFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                timeStamp = SDFormat.format(SDFormat.parse(project.getKickOffDate()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        return sendProjectAndMembersToINdigit(updatedProject, timeStamp);
    }

    // validation for projectname for an updated project
    public boolean uniqueExistingMetaprojectName(long metaId, Project project) {
        Collection<Project> projects = projectDao.findByMetaproject(metaId);
        for (Project entry : projects) {
            if (!entry.getProjectId().equals(project.getProjectId())
                    && entry.getProjectTitle().equals(project.getProjectTitle())) {
                log.info("Name bereits Vorhanden");
                return false;
            }
        }
        return true;

    }

    @DeleteMapping("metaproject/{metaid}/project/{id}")
    public ResponseEntity deleteById(@PathVariable Long metaid, @PathVariable Long id, @RequestHeader String token) {
        log.info("Request <-- DELETE /metaproject/{}/project/{}", metaid, id);

        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_PROJECT_DELETE, metaid)
                .checkForProjectRight(Rights.METAPROJECT_PROJECT_DELETE, id).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // User has rights, continue
        Project project = projectDao.findById(id);

        if (project == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Collection<ProjectStatusChange> projectStatusChange = projectStatusChangeDAO.findByProject(id);
        for (ProjectStatusChange test : projectStatusChange) {
            projectStatusChangeDAO.delete(test.getStatusChangeId());
        }

        Project removedProject = deleteProjectById(project, token);

        return ResponseEntity.ok(removedProject);

    }

    @GetMapping(value = "metaproject/{metaid}/project/{id}")
    public ResponseEntity getProjectById(@PathVariable Long id, @PathVariable Long metaid, @RequestHeader String
            token) {
        log.info("Request <-- GET /metaproject/{}/project/{}", metaid, id);

        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_VIEW_DETAILS)
                .checkForMetaprojectRight(Rights.METAPROJECT_VIEW_DETAILS, metaid).validate();
        if (!hasRights) {
            // User has no rights
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Project project = projectDao.findById(id);
        if (project == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(project);
    }

    /**
     * Stores the given Fieldname in ProjectFieldAccess.
     */
    @PostMapping(value = "metaproject/{metaid}/project/{id}/private")
    public ResponseEntity updateProjectFieldAccess(@PathVariable Long metaid, @PathVariable Long id,
                                                   @RequestBody String[] privateFields, @RequestHeader String token) {
        log.info("Request <-- POST /metaproject/{}/project/{}/private", metaid, id);

        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_VIEW_DETAILS)
                .checkForMetaprojectRight(Rights.METAPROJECT_VIEW_DETAILS, metaid).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Project project = projectDao.findById(id);
        if (project != null) {
            if (privateFields.length > 0) {
                /*
                 *  Stores all given Fieldnames.
                 */
                for (String field : privateFields) {
                    Collection<ProjectFieldAccess> pfaCollection = projectFieldAccessDao.findByProjectId(id);
                    for (ProjectFieldAccess pfaTemp : pfaCollection) {
                        /*
                         *  If a persistent ProjectFieldAccess-Value is NOT passed, it will be removed from db.
                         */
                        if (!this.inStringArray(pfaTemp.getField(), privateFields)) {
                            projectFieldAccessDao.delete(pfaTemp);
                        }
                    }
                    ProjectFieldAccess pfaCheck = projectFieldAccessDao.findByIdAndField(id, field);
                    //If the given (key,value)-pair (projectID, fieldname) is not stored, it will be created.
                    if (pfaCheck == null) {
                        ProjectFieldAccess pfa = new ProjectFieldAccess();
                        pfa.setProjectId(id);
                        pfa.setField(field);
                        pfa.setVisible(false);
                        projectFieldAccessDao.save(pfa);
                    }
                }
            } else {
                // if no field is marked as private, all values for passed project can be deleted.
                Collection<ProjectFieldAccess> pfaCollection = projectFieldAccessDao.findByProjectId(id);
                for (ProjectFieldAccess pfaTemp : pfaCollection) {
                    projectFieldAccessDao.delete(pfaTemp);
                }
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Returns a list of all Fieldnames marked as private.
     */
    @GetMapping(value = "metaproject/{metaid}/project/{id}/private")
    public ResponseEntity getProjectFieldAccess(@PathVariable("metaid") Long metaId, @PathVariable("id") Long
            projectId, @RequestHeader(value = "token") String token) {

        log.info("Request <-- GET /metaproject/{}/project/{}/private", metaId, projectId);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_VIEW_DETAILS)
                .checkForMetaprojectRight(Rights.METAPROJECT_VIEW_DETAILS, metaId).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Project project = projectDao.findById(projectId);
        if (project != null) {
            return ResponseEntity.ok(projectFieldAccessDao.findByProjectId(projectId));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Checks if a field is inside a passed array.
     */
    private boolean inStringArray(String field, String[] array) {
        for (String value : array) {
            if (field.equals(value))
                return true;
        }
        return false;
    }

    @PostMapping(value = "metaproject/{metaId}/project/{projectId}/copy")
    public ResponseEntity copyProject(@PathVariable Long metaId, @PathVariable Long projectId,
                                      @RequestBody CopyProjectDTO copyProject, @RequestHeader String token) {
        log.info("Request <-- POST metaproject/{metaId}/project/{projectId}/copy");

        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_PROJECT_CREATE)
                .checkForMetaprojectRight(Rights.METAPROJECT_PROJECT_CREATE, metaId).validate();

        if (!hasRights) {
            // User has no rights
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!validProjectName(metaId, copyProject.getNewProjectTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("title_already_existent");
        }

        Metaproject Metaproject = metaDao.findById(metaId);
        if (Metaproject == null) {
            log.info("Can't find Metaproject with id " + metaId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Metaproject_not_found");
        }

        try {
            if (checkProjectRegistrationStarted(Metaproject)) {
                log.info("project_create_not_started");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("project_create_not_started");
            }
            if (checkProjectRegistrationNotEnded(Metaproject)) {
                log.info("project_create_ended");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("project_create_ended");
            }
        } catch (ParseException e) {
            log.info("parser_fail");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("parser_fail");
        }

        Project oldProject = projectDao.findById(projectId);
        if (oldProject == null) {
            log.info("Can't find Project to copy with id " + projectId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("project_not_found");
        }
        User projectLeader = userDao.findById(copyProject.getProjectLeaderId());
        if (projectLeader == null) {
            log.info("Can't find ProjectLeader with id " + copyProject.getProjectLeaderId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("projectLeader_not_found");
        }
        User currentUser = tokenService.getUserByToken(token);
        if (currentUser == null) {
            log.info("Can't find currentUser");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("currentUser_not_found");
        }

        Long newProjectId = copyService.copyProject(oldProject, Metaproject, projectLeader, currentUser, copyProject.getNewProjectTitle());

        return ResponseEntity.ok(newProjectId);
    }

    @PostMapping(value = "metaproject/{metaId}/project/{projectId}/move")
    public ResponseEntity moveProject(@PathVariable Long metaId, @PathVariable Long projectId,
                                      @RequestBody Long destinationMetaprojectId, @RequestHeader String token) {
        log.info("Request <-- POST metaproject/{metaId}/project/{projectId}/move");

        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_PROJECT_CREATE)
                .checkForMetaprojectRight(Rights.METAPROJECT_PROJECT_CREATE, metaId).validate();

        if (!hasRights) {
            // User has no rights
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Project oldProject = projectDao.findById(projectId);
        if (oldProject == null) {
            log.info("Can't find Project to copy with id " + projectId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("project_not_found");
        }

        if (!validProjectName(destinationMetaprojectId, oldProject.getProjectTitle())) {
            log.info("title_already_existent");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("title_already_existent");
        }

        Metaproject destinationMetaproject = metaDao.findById(destinationMetaprojectId);
        if (destinationMetaproject == null) {
            log.info("Can't find destinationMetaproject with id " + destinationMetaprojectId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("sourceMetaproject_not_found");
        }

        try {
            if (checkProjectRegistrationStarted(destinationMetaproject)) {
                log.info("project_create_not_started");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("project_create_not_started");
            }
            if (checkProjectRegistrationNotEnded(destinationMetaproject)) {
                log.info("project_create_ended");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("project_create_ended");
            }
        } catch (ParseException e) {
            log.info("parser_fail");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("parser_fail");
        }

        User currentUser = tokenService.getUserByToken(token);
        if (currentUser == null) {
            log.info("Can't find currentUser");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("currentUser_not_found");
        }
        Metaproject sourceMetaproject = metaDao.findById(metaId);
        if (sourceMetaproject == null) {
            log.info("Can't find sourceMetaproject with id " + metaId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("sourceMetaproject_not_found");
        }
        Long newProjectId = copyService.moveProject(oldProject, sourceMetaproject, destinationMetaproject, currentUser);

        deleteProjectById(oldProject, token);

        return ResponseEntity.ok(newProjectId);
    }

    public boolean checkProjectRegistrationStarted(Metaproject mp) throws ParseException {
        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        parserSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date now = new Date();
        //Careful: "ProjectReg" = Projekterstellungszeitraum!
        Date projectCreateStart = parserSDF.parse(mp.getProjectRegStart());
        return projectCreateStart.after(now);
    }

    public boolean checkProjectRegistrationNotEnded(Metaproject mp) throws ParseException {
        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        parserSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date now = new Date();
        //Careful: "ProjectReg" = Projekterstellungszeitraum!
        Date projectCreateEnd = parserSDF.parse(mp.getProjectRegEnd());
        return projectCreateEnd.before(now);
    }

    public Project deleteProjectById(Project project, String token) {
        Team team = teamDao.findByProjectId(project.getProjectId());
        if (team != null) {
            team.setProjectId(null);
            teamDao.update(team, team.getTeamId());
        }
        // Delete PrioTeamProject-Entry
        prioTeamProjectDao.deleteByProject(project.getProjectId());
        // Delete fileupload of project
        fileUploadDao.deleteAllByProject(project.getProjectId());
        // remove all roles in this project
        roleDao.removesRoleProject(project);
        // delete all comms
        communicationService.revokeCommsForProject(project.getProjectId());
        // delete project
        Project removedProject = projectDao.delete(project.getProjectId());

        User actionUser = tokenService.getUserByToken(token);
        // create and send a new communication object
        communicationCreatorService.sendCommProjectDelete(actionUser, removedProject);

        return removedProject;
    }

}
