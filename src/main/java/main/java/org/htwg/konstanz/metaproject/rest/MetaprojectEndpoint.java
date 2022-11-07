package main.java.org.htwg.konstanz.metaproject.rest;

import com.google.common.collect.Lists;
import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.dtos.CopyMetaprojectDTO;
import main.java.org.htwg.konstanz.metaproject.dtos.MetaprojectInfoDTO;
import main.java.org.htwg.konstanz.metaproject.dtos.ProjectInfoDTO;
import main.java.org.htwg.konstanz.metaproject.entities.*;
import main.java.org.htwg.konstanz.metaproject.persistance.*;
import main.java.org.htwg.konstanz.metaproject.rights.DefaultRoles;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import main.java.org.htwg.konstanz.metaproject.services.CommunicationCreatorService;
import main.java.org.htwg.konstanz.metaproject.services.CommunicationService;
import main.java.org.htwg.konstanz.metaproject.services.CopyService;
import main.java.org.htwg.konstanz.metaproject.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author MaWeissh, StChiari, FaAmbros, FaHocur, AlVeliu,
 * JoFesenm,SiKelle,PaDrautz
 * @version 1.5
 */
@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/metaproject")
public class MetaprojectEndpoint<Foo, intpage, intsize> {

    private static final Logger log = LoggerFactory.getLogger(MetaprojectEndpoint.class);

    private RightService rightService;

    private final TokenService tokenService;

    private final TeamDAO teamDao;

    private final MetaprojectDAO metaprojectDao;

    private final RoleDAO roleDao;

    private final PrioTeamProjectDAO prioTeamProjectDao;

    private final ProjectDAO projectDao;

    private final UserDAO userDao;

    private final ProjectStatusChangeDAO projectStatusChangeDAO;

    private final RelationTeamUserDAO relTeamUserDao;

    private final RelationMetaprojectUserDAO relMetaUserDao;

    private final FileUploadDAO fileUploadDao;

    private final ProjectFieldAccessDAO projectFieldAccessDAO;

    private final CommunicationCreatorService communicationCreatorService;

    private final CommunicationService communicationService;

    private final CopyService copyService;
    private Page<Metaproject> result;

    public MetaprojectEndpoint(RightService rightService, TokenService tokenService, TeamDAO teamDao, MetaprojectDAO metaprojectDao, RoleDAO roleDao, PrioTeamProjectDAO prioTeamProjectDao, ProjectDAO projectDao, UserDAO userDao, ProjectStatusChangeDAO projectStatusChangeDAO, RelationTeamUserDAO relTeamUserDao, RelationMetaprojectUserDAO relMetaUserDao, FileUploadDAO fileUploadDao, ProjectFieldAccessDAO projectFieldAccessDAO, CommunicationCreatorService communicationCreatorService, CommunicationService communicationService, CopyService copyService) {
        this.rightService = rightService;
        this.tokenService = tokenService;
        this.teamDao = teamDao;
        this.metaprojectDao = metaprojectDao;
        this.roleDao = roleDao;
        this.prioTeamProjectDao = prioTeamProjectDao;
        this.projectDao = projectDao;
        this.userDao = userDao;
        this.projectStatusChangeDAO = projectStatusChangeDAO;
        this.relTeamUserDao = relTeamUserDao;
        this.relMetaUserDao = relMetaUserDao;
        this.fileUploadDao = fileUploadDao;
        this.projectFieldAccessDAO = projectFieldAccessDAO;
        this.communicationCreatorService = communicationCreatorService;
        this.communicationService = communicationService;
        this.copyService = copyService;
    }

    @PostMapping(value = "")
    public ResponseEntity<Object> createMetaproject(@RequestBody Metaproject mp, @RequestHeader String token) {
        log.info("Request <-- POST /metaproject");

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_CREATE).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Validation of Metaproject
        if (!validMetaproject(mp)) {
            log.info("Validierung Fehlgeschlagen");
            return ResponseEntity.badRequest().build();
        }

		/*if (!timesInFuture(mp)) {
			log.info("Zeiten nicht in Zukunft");
			return Response.status(Status.BAD_REQUEST).build();
		}'*/

        if (!uniqueNewMetaprojectName(mp.getMetaprojectTitle())) {
            log.info("Name bereits vorhanden");
            //return Response.status(Status.CONFLICT).entity(").build("title_already_existent");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("title_already_existent");
        }
        Metaproject savedMetaproject = metaprojectDao.save(mp);
        User user = tokenService.getUserByToken(token);

        roleDao.addRoleMetaprojectToUser(user, savedMetaproject,
                DefaultRoles.METAPROJECT_OWNER);
        // add roles to new metaproject leader
        roleDao.addRoleMetaprojectToUser(savedMetaproject.getMetaprojectLeader(), savedMetaproject,
                DefaultRoles.METAPROJECT_LEADER);
        roleDao.addRoleMetaprojectToUser(savedMetaproject.getMetaprojectLeader(), savedMetaproject,
                DefaultRoles.METAPROJECT_MEMBER);

        User actionUser = tokenService.getUserByToken(token);
        // create and send a new communication object
        communicationCreatorService.sendCommMetaprojectLeaderAppointment(actionUser,
                savedMetaproject.getMetaprojectLeader(), savedMetaproject);

        //Check for preRegistrationStatus
        if (!mp.getPreRegistration()) {
            rightService.createRelationMetaToAllUserAddRoleToAllUser(savedMetaproject, actionUser);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("{\"id\":" + savedMetaproject.getMetaprojectId() + "}");
    }

    /**
     * Validate a Metaproject in backend This Main Method validates a
     * Metaproject in backend and returns true or false
     *
     * @return boolean
     */
    public boolean validMetaproject(Metaproject mp) {

        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        try {
            parserSDF.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date projectRegStart = parserSDF.parse(mp.getProjectRegStart());
            Date projectRegEnd = parserSDF.parse(mp.getProjectRegEnd());

            Date studentRegStart = parserSDF.parse(mp.getStudentRegStart());
            Date studentRegEnd = parserSDF.parse(mp.getStudentRegEnd());

            Date teamRegStart = parserSDF.parse(mp.getTeamRegStart());
            Date teamRegEnd = parserSDF.parse(mp.getTeamRegEnd());

            return (
                    // Prüfung ob Ende nach Start oder gleich
                    (projectRegEnd.after(projectRegStart) || projectRegEnd.equals(projectRegStart))
                            && (studentRegEnd.after(studentRegStart) || studentRegEnd.equals(studentRegStart))
                            && (teamRegEnd.after(teamRegStart) || teamRegEnd.equals(teamRegStart))

                            // Prüfung ob Start der Teamregistrieung nach Ende der
                            // Projectregistrierung oder gleich
                            //&& (teamRegStart.after(projectRegEnd) || teamRegStart.equals(projectRegEnd))

                            // Prüfung ob Start der Teamregistrierung nach Start der
                            // Studenregistrierung oder gleich
                            //&& (teamRegStart.after(studentRegStart) || teamRegStart.equals(studentRegStart))

                            // Prüfung ob Ende der Teamregistrierung nach Ende der
                            // Studentenregistrierung oder gleich
                            //&& (teamRegEnd.after(studentRegEnd) || teamRegEnd.equals(studentRegEnd))

                            && mp.getMetaprojectLeader() != null && mp.getCourseOfStudies() != null
                            && mp.getTeamMinSize() >= 1
                            && mp.getTeamMaxSize() <= 25 && mp.getTeamMinSize() <= mp.getTeamMaxSize()
            );
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Validate a Metaproject in backend This Method checks if the date input
     * for a Metaproject is in future and returns true or false
     *
     * @return boolean
     */
    public boolean timesInFuture(Metaproject mp) {

        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        try {
            Date now = new Date();
            parserSDF.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date projectRegStart = parserSDF.parse(mp.getProjectRegStart());
            Date studentRegStart = parserSDF.parse(mp.getStudentRegStart());
            Date teamRegStart = parserSDF.parse(mp.getTeamRegStart());

            return (
                    (projectRegStart.after(now) || projectRegStart.equals(now))
                            && (studentRegStart.after(now) || projectRegStart.equals(now))
                            && (teamRegStart.after(now) || teamRegStart.equals(now))
            );

        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * This Method checks befor creation of a new Metaproject if the given name
     * is already in database and returns true or false
     *
     * @return boolean
     */
    public boolean uniqueNewMetaprojectName(String title) {
        Collection<Metaproject> metaprojects = metaprojectDao.findAll();
        for (Metaproject entry : metaprojects) {
            if (entry.getMetaprojectTitle().equals(title)) {
                log.info("Name bereits Vorhanden");
                return false;
            }
        }
        return true;
    }

    /**
     * This Method checks befor editing of an existing Metaproject if the given
     * name is already in database and returns true or false
     *
     * @return boolean
     */
    public boolean uniqueExistingMetaprojectName(Metaproject mp) {
        Collection<Metaproject> metaprojects = metaprojectDao.findAll();
        for (Metaproject entry : metaprojects) {
            if (!entry.getMetaprojectId().equals(mp.getMetaprojectId())
                    && entry.getMetaprojectTitle().equals(mp.getMetaprojectTitle())) {
                log.info("Name bereits Vorhanden");
                return false;
            }
        }
        return true;
    }

    /**
     * @author Nora Selca
     */
    /*
    @GetMapping(params = { "page", "size" })
    public List<Foo> findPaginated(@RequestParam("page")intpage, @RequestParam("size")intsize,
    UriComponentsBuilder uriBuilder, HttpServletResponse response)
    { Page<Foo> resultPage = Metaproject.findPaginated(page, size);
    if (page > resultPage.getTotalPages()) { throw new MyResourceNotFoundException(); }
    eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<Foo>( Foo.class, uriBuilder, response, page, resultPage.getTotalPages(), size)); return resultPage.getContent(); }


    */
    @GetMapping(value = "")
    public ResponseEntity<Object> getAllMetaprojects(@RequestHeader String token,
                                                     @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        log.info("Request<--GET/metaproject");

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_LIST).validate();
        if (!hasRights) {
            log.info("Userhasnopermissionstodothatoperation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        page = (page == null) ? 0 : page;
        size = (size == null) ? Integer.MAX_VALUE : size;

        Page<Metaproject> result = metaprojectDao.findAll(page, size, "metaProjectId", false);
        //converteverymetaprojecttoitssmallerDTO
        Collection<MetaprojectInfoDTO> dtoResult = new LinkedList<>();
        for (Metaproject m : result) {
            dtoResult.add(m.getMetaprojectInfoDto());
        }
        return ResponseEntity.ok(dtoResult);
    }

    @GetMapping(value = "/number")
    public ResponseEntity<Integer> getNumberOfMetaprojects() {
        log.info("Request<--GET/metaproject/number");

        return ResponseEntity.ok(metaprojectDao.findAll().size());
    }

    @GetMapping(value = "/overview")
    public ResponseEntity<Object> getAllMetaprojectsOverview(@RequestHeader(required = false) String token,
                                                             @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size,
                                                             @RequestParam(required = false) String sortAttribute, @RequestParam(required = false) Boolean isDescending) {
        log.info("Request <-- GET /metaproject/overview with size: " + size + ", page: " + page + ", sortAttribute:" + sortAttribute);

        page = (page == null) ? 0 : page;
        size = (size == null) ? Integer.MAX_VALUE : size;

        sortAttribute = (sortAttribute == null || sortAttribute.equals("id")) ? "metaProjectId" : sortAttribute;
        isDescending = isDescending != null && isDescending;

        if (sortAttribute.equals("metaprojectTitle") || sortAttribute.equals("metaProjectTitle"))
            sortAttribute = "metaProjectTitle";
        else if (sortAttribute.equals("metaprojectLeader?.userFirstName"))
            sortAttribute = "metaProjectTitle"; // das funktioniert noch nicht und nach datum funktioniert auch noch nicht


        boolean hasRights = false;
        if (token != null)
            hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.METAPROJECT_LIST).validate();

        Collection<Metaproject> metaprojects;
        if (!hasRights)
            metaprojects = metaprojectDao.findAllVisible(page, size, sortAttribute, isDescending);
        else
            metaprojects = metaprojectDao.findAll(page, size, sortAttribute, isDescending).getContent();

        //convert into dto and add all projects of the metaproject
        List<MetaprojectInfoDTO> metaprojectDtos = Lists.newArrayList();
        for (Metaproject mp : metaprojects) {
            MetaprojectInfoDTO mpInfoDto = mp.getMetaprojectInfoDto();
            List<ProjectInfoDTO> projects = projectDao.findByMetaproject(mp.getMetaprojectId()).stream().map(Project::getProjectInfoDto).collect(Collectors.toList());
            mpInfoDto.setProjects(projects);
            metaprojectDtos.add(mpInfoDto);
        }

        return ResponseEntity.ok(metaprojectDtos);
    }

    /**
     * Get all Metaprojects from Database
     *
     * @return Collection<Metaproject>
     */
    @GetMapping(value = "/overview/user/{userId}")
    public ResponseEntity<Object> getAllMetaprojectsForOverviewOfUser(@PathVariable Long userId, @RequestHeader String token) {
        log.info("Request <-- GET /metaproject/overview/user/{userId}");

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_VIEW_INFO).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Collection<RelationMetaprojectUser> relationsMpUser = relMetaUserDao.findByUser(userDao.findById(userId));
        Set<Metaproject> metaprojects = relationsMpUser.stream().map(RelationMetaprojectUser::getMetaprojectId).collect(Collectors.toSet());

        //convert into dto
        List<MetaprojectInfoDTO> metaprojectDtos = metaprojects.stream().map(Metaproject::getMetaprojectInfoDto).collect(Collectors.toList());

        for (MetaprojectInfoDTO metaprojectDTO : metaprojectDtos) { // get all the projects into the metaprojects to send them with
            List<ProjectInfoDTO> projects = projectDao.findByMetaproject(metaprojectDTO.getMetaprojectId()).stream().map(Project::getProjectInfoDto).collect(Collectors.toList());
            metaprojectDTO.setProjects(projects);
        }
        metaprojectDtos.sort(Comparator.comparing(MetaprojectInfoDTO::getMetaprojectId));
        return ResponseEntity.ok(metaprojectDtos);
    }

    @PutMapping(value = "/{id}/edit")
    public ResponseEntity<Object> updateMetaproject(@PathVariable Long id, @RequestBody Metaproject mp, @RequestHeader String token) {
        log.info("Request <-- PUT /metaproject/{}/edit", id);

        boolean hasRightsApp = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_EDIT).validate();
        boolean hasRightsMeta = rightService.newRightHandler(token).checkForSuperUser()
                .checkForMetaprojectRight(Rights.METAPROJECT_EDIT, id).validate();

        if (!hasRightsApp && !hasRightsMeta) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Metaproject oldMetaProject = metaprojectDao.findById(id);
        User oldLeader = oldMetaProject.getMetaprojectLeader();
        boolean oldPreRegistration = oldMetaProject.getPreRegistration();
        log.info("Check time validation ");
        if (!validMetaproject(mp)) {
            return ResponseEntity.badRequest().build();
        }
        log.info("Check if existing Metaprojectname is unique ");
        if (!uniqueExistingMetaprojectName(mp)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("title_already_existent");
        }
        log.info("check if specialization is valid");
        if (!sameSpecialization(mp)) {
            return ResponseEntity.badRequest().build();
        }
        log.info("check of teamsize is valid");
        if (!validTeamSizeChange(mp)) {
            return ResponseEntity.badRequest().build();
        }
        log.info("Metaproject is valid!");
        Metaproject updatedMetaproject = metaprojectDao.update(id, mp);

		/*
		 * check if Metaproject is public of privat
		 * If the MP is private, all entries for projects in ProjectFieldAccess can be deleted, since all entries have to be private.
		 *
		if(!updatedMetaproject.isVisible()) {
			for(Project project : projectDao.findByMetaproject(updatedMetaproject.getMetaprojectId())) {
				this.clearProjectFieldAccess(project.getProjectId());
			}
		}/**/

        User newLeader = updatedMetaproject.getMetaprojectLeader();

        // if project leader changed
        if (oldLeader.getUserId() != newLeader.getUserId()) {
            roleDao.removeRoleMetaprojectFromUser(oldLeader, oldMetaProject, DefaultRoles.METAPROJECT_LEADER);
            //roleDao.removeRoleMetaprojectFromUser(oldLeader, oldMetaProject, DefaultRoles.METAPROJECT_MEMBER);

            roleDao.addRoleMetaprojectToUser(newLeader, updatedMetaproject, DefaultRoles.METAPROJECT_LEADER);
            roleDao.addRoleMetaprojectToUser(newLeader, updatedMetaproject, DefaultRoles.METAPROJECT_MEMBER);

            User actionUser = tokenService.getUserByToken(token);
            // create and send a new communication object
            communicationCreatorService.sendCommMetaprojectLeaderChange(actionUser, newLeader, oldLeader, updatedMetaproject);
        }

        // if preRegistration changed
        if (oldPreRegistration != mp.getPreRegistration()) {
            if (mp.getPreRegistration())
                rightService.deleteRelationMetaToAllUserRemoveRoleFromAllNormalUser(mp);
            else
                rightService.createRelationMetaToAllUserAddRoleToAllUser(mp, oldLeader);
        }

        return ResponseEntity.ok(updatedMetaproject);
    }

	/*
	 * Deletes a ProjectFieldAccess-Entry when it's not needed.
	 * @param projectId
	 *
	private void clearProjectFieldAccess(long projectId) {
		Collection<ProjectFieldAccess> projectFieldAccess = projectFieldAccessDAO.findByProjectId(projectId);
		for(ProjectFieldAccess pfa : projectFieldAccess) {
			projectFieldAccessDAO.delete(pfa);
		}
	}/**/

    public boolean sameSpecialization(Metaproject mp) {
        log.info("check if specialisation is unchangeched");
        Collection<Project> projects = projectDao.findByMetaproject(mp.getMetaprojectId());
        if (projects.isEmpty()) {
            return true;
        }
        Collection<Specialisation> oldSpecializations = metaprojectDao.findById(mp.getMetaprojectId())
                .getSpecialisation();
        Collection<Specialisation> newSpecializations = mp.getSpecialisation();

        boolean allMatch1 = true;
        for (Specialisation newItem : newSpecializations) {
            boolean match = false;

            for (Specialisation oldItem : oldSpecializations) {
                if (oldItem.getSpecialisationId().equals(newItem.getSpecialisationId())) {
                    match = true;
                }
            }
            if (!match) {
                allMatch1 = false;
                break;
            }
        }

        boolean allMatch2 = true;
        for (Specialisation newItem : newSpecializations) {
            boolean match = false;

            for (Specialisation oldItem : oldSpecializations) {
                if (oldItem.getSpecialisationId().equals(newItem.getSpecialisationId()))
                    match = true;
            }
            if (!match) {
                allMatch1 = false;
                break;
            }
        }
        return (allMatch1 && allMatch2);
    }

    public boolean validTeamSizeChange(Metaproject mp) {

        Collection<Team> teams = teamDao.findByMetaprojectId(mp.getMetaprojectId());
        if (teams.isEmpty()) {
            return true;
        }
        int oldMaxTeamsize = metaprojectDao.findById(mp.getMetaprojectId()).getTeamMaxSize();
        int newMaxTeamsize = mp.getTeamMaxSize();
        if (newMaxTeamsize < oldMaxTeamsize) {
            return false;
        }
        int oldMinTeamsize = metaprojectDao.findById(mp.getMetaprojectId()).getTeamMinSize();
        int newMinTeamsize = mp.getTeamMinSize();
        return newMinTeamsize <= oldMinTeamsize;
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id, @RequestHeader String token) {
        log.info("Request <-- DELETE /metaproject/{}", id);

        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_DELETE).checkForMetaprojectRight(Rights.METAPROJECT_DELETE, id)
                .validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Metaproject metaproject = metaprojectDao.findById(id);

        // send communication for this action
        communicationCreatorService.sendCommMetaprojectDelete(tokenService.getUserByToken(token), metaproject);

        // DELETE SQL-Statement, before deleting a metaproject first delete all connected tables/table columns
        if (metaproject == null) {
            return ResponseEntity.notFound().build();
        }
        // delete teams
        Collection<Team> teams = teamDao.findByMetaprojectId(id);
        for (Team team : teams) {
            prioTeamProjectDao.deleteByTeam(team.getTeamId());
            // remove all roles in this team
            roleDao.removeRolesTeam(team);

            relTeamUserDao.deleteByTeam(team);

            // delete all comms
            communicationService.revokeCommsForTeam(team.getTeamId());
        }
        teamDao.deleteByMetaproject(id);

        // delete all projects
        Collection<Project> projects = projectDao.findByMetaproject(id);
        for (Project project : projects) {
            //Delete all references of a project
            Collection<ProjectStatusChange> projectStatusChange = projectStatusChangeDAO.findByProject(project.getProjectId());
            for (ProjectStatusChange statusChange : projectStatusChange) {
                projectStatusChangeDAO.delete(statusChange.getStatusChangeId());
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
            projectDao.delete(project.getProjectId());
        }

        // remove all file uploads
        fileUploadDao.deleteAllByMetaproject(id);

        // Delete RelationUserRole-Entry
        roleDao.removeRolesMetaproject(metaproject);

        // Delete relationmetaprojectuser
        relMetaUserDao.deleteByMetaproject(metaproject);

        // delete all comms
        communicationService.revokeCommsForMetaproject(id);

        metaprojectDao.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getMetaprojectById(@PathVariable Long id, @RequestHeader String token) {
        log.info("Request <-- GET /metaproject/{}", id);

        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_LIST).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Metaproject mp = metaprojectDao.findById(id);
        return ResponseEntity.ok(mp);
    }

    /**
     * Find user with no team in a metaproject by id.
     */
    @GetMapping(value = "/{metaprojectId}/teamless")
    public ResponseEntity<Object> findUserByMetaprojectTeamless(@PathVariable long metaprojectId, @RequestHeader String token) {
        log.info("Request <-- GET /metaproject/{}/teamless", metaprojectId);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_VIEW_INFO).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(userDao.findByMetaprojectTeamLess(metaprojectId));
    }

    @PostMapping(value = "/copy")
    public ResponseEntity<Object> copyMetaproject(@RequestHeader String token, @RequestBody CopyMetaprojectDTO copyMp) {
        User metaprojectCreator = tokenService.getUserByToken(token);
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_CREATE).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!uniqueNewMetaprojectName(copyMp.getTitle())) {
            log.info("Title already existent");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("title_already_existent");
        }

        Metaproject oldMp = metaprojectDao.findById(copyMp.getMetaprojectId());
        if (oldMp == null) {
            log.info("Can't find metaproject with id " + copyMp.getMetaprojectId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("metaproject_not_found");
        }
        boolean isTeamReg = oldMp.getRegisterType().equals("Team");

        if (!isTeamReg && (copyMp.isCopyTeams() || copyMp.isCopyPrioritizations())) {
            log.info("Can't copy teams or priorizations when the metaproject isn't from type 'Teamregistration'");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("teams_priorizations_singleregistration");
        }

        if (oldMp.getPreRegistration() && copyMp.isCopyTeams() && !copyMp.isCopyUsers()) {
            log.info("Can't copy teams without users if the project has preregistration");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("teams_without_users");
        }
        if (copyMp.isCopyPrioritizations() && (!copyMp.isCopyTeams() || !copyMp.isCopyProjects())) {
            log.info("Can't priorizations without teams and projects");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("priorizations_without_users_teams_projects");
        }

        User leader = userDao.findById(copyMp.getLeaderId());
        if (leader == null) {
            log.info("Can't find user with id " + copyMp.getLeaderId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("leader_not_found");
        }

        Long id = copyService.copyMetaproject(oldMp, copyMp, leader, metaprojectCreator, isTeamReg);

        return ResponseEntity.ok().body(id);
    }

    /**
     * Get all Metaproject for specific User from Database
     *
     * @return Collection<Metaproject>
     */
    @GetMapping(value = "/user/{userId}/metaprojects")
    public ResponseEntity<Object> getAllMetaprojectForUser(@PathVariable Long userId, @RequestHeader String token) {
        log.info("Request <-- GET metaproject/user/" + userId + "/metaprojects");

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_VIEW_INFO).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDao.findById(userId);
        Collection<MetaprojectInfoDTO> mpInfoDTOs = new LinkedList<>();
        for (RelationMetaprojectUser r : relMetaUserDao.findByUser(user)) {
            Metaproject mp = r.getMetaprojectId();
            mpInfoDTOs.add(mp.getMetaprojectInfoDto());
        }
        return ResponseEntity.ok(mpInfoDTOs);
    }
}
