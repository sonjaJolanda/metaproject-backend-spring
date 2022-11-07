package main.java.org.htwg.konstanz.metaproject.persistance;

import com.google.common.collect.Lists;
import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.dtos.ProjectMembersINdigitDTO;
import main.java.org.htwg.konstanz.metaproject.entities.FailedINdigitTransfer;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.RelationTeamUser;
import main.java.org.htwg.konstanz.metaproject.repositories.FailedINdigitTransferRepository;
import main.java.org.htwg.konstanz.metaproject.rest.INdigitApiMetaprojectClient;
import main.java.org.htwg.konstanz.metaproject.rest.ProjectEndpoint;
import main.java.org.htwg.konstanz.metaproject.services.INdigitApiMetaproject;
import main.java.org.htwg.konstanz.metaproject.services.INdigitApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FailedINdigitTransferDAOImpl implements FailedINdigitTransferDAO {

    private final static Logger log = LoggerFactory.getLogger(ProjectEndpoint.class);
    private final FailedINdigitTransferRepository failedINdigitTransfersRepo;

    private final RelationTeamUserDAO relationTeamUserDAO;

    private final ProjectDAO projectDAO;

    private final SystemVariableDAO systemVariableDAO;

    @Value("${indigit.api.auth.secret}")
    private String clientSecret;

    @Value("${indigit.api.auth.client}")
    private String clientId;

    @Value("${indigit.api.url}")
    private String indigitApiUrl;

    public FailedINdigitTransferDAOImpl(FailedINdigitTransferRepository failedINdigitTransfersRepo, RelationTeamUserDAO relationTeamUserDAO, ProjectDAO projectDAO, SystemVariableDAO systemVariableDAO) {
        this.failedINdigitTransfersRepo = failedINdigitTransfersRepo;
        this.relationTeamUserDAO = relationTeamUserDAO;
        this.projectDAO = projectDAO;
        this.systemVariableDAO = systemVariableDAO;
    }

    @Override
    public List<FailedINdigitTransfer> findAll() {
        return failedINdigitTransfersRepo.findAll();
    }

    @Override
    public FailedINdigitTransfer newFailedProjectTransfer(Project project, long statusCode) {
        FailedINdigitTransfer failedINdigitTransfer = newOrUpdatedFailedTransfer(project, statusCode);
        failedINdigitTransfer.setProjectTransferFailed(true);
        failedINdigitTransfer.setMemberTransferFailed(true);
        return failedINdigitTransfersRepo.save(failedINdigitTransfer);
    }

    @Override
    public FailedINdigitTransfer newFailedProjectMembersTransfer(Project project, long statusCode) {
        FailedINdigitTransfer failedINdigitTransfer = newOrUpdatedFailedTransfer(project, statusCode);
        failedINdigitTransfer.setProjectTransferFailed(false);
        failedINdigitTransfer.setMemberTransferFailed(true);
        return failedINdigitTransfersRepo.save(failedINdigitTransfer);
    }

    private FailedINdigitTransfer newOrUpdatedFailedTransfer(Project project, long statusCode) {
        FailedINdigitTransfer failedTransfer;

        Optional<FailedINdigitTransfer> optional = failedINdigitTransfersRepo.findByProject(project);
        if (optional.isPresent()){
            failedTransfer = optional.get();
            failedTransfer.setNumberOfFailedAttempts(failedTransfer.getNumberOfFailedAttempts() + 1);
        }
        else {
            failedTransfer = new FailedINdigitTransfer();
            failedTransfer.setProject(project);
            failedTransfer.setFirstFailedAt(new Date());
            failedTransfer.setNumberOfFailedAttempts(1);
        }

        failedTransfer.setLastFailedAt(new Date());
        failedTransfer.setStatusCode(statusCode + "");
        return failedINdigitTransfersRepo.save(failedTransfer);
    }

    @Override
    public int tryTransfer(Project project, String timeStamp) throws INdigitApiService.HttpStatusCodeException, IOException {
        return transfer(project, timeStamp, false);
    }

    private int transfer(Project project, String timeStamp, boolean isOnlyMembersTransferFailed) throws INdigitApiService.HttpStatusCodeException, IOException {

        // if the maximum number of attempts is reached this method doesn't do anything anymore
        Optional<FailedINdigitTransfer> optional = failedINdigitTransfersRepo.findByProject(project);
        int numberOfFailedAttempts = optional.map(FailedINdigitTransfer::getNumberOfFailedAttempts).orElse(0);
        int maximumOfFailedAttempts = Integer.parseInt(systemVariableDAO.findByKey(Constants.FAILED_INDIGIT_TRANSFER_ATTEMPTS).getValue());
        if (numberOfFailedAttempts >= maximumOfFailedAttempts){
            // in Zukunft soll hier auch noch eine E-Mail versendet werden an den Admin, dass die Maximalanzahl erreicht ist!
            return 200;
        }

        if (numberOfFailedAttempts >= 1)
            log.info("Retry transfer of project with id " + project.getProjectId() + " -> failedAttempts:" + numberOfFailedAttempts + ", maximum:" + maximumOfFailedAttempts);

        INdigitApiMetaproject instance = INdigitApiMetaprojectClient.getInstance(indigitApiUrl, clientId, clientSecret);

        if (!isOnlyMembersTransferFailed) {
            int projectStatuscode = instance.transferProjectStatusChange(new INdigitApiMetaproject.IndigitProject(
                    project.getTransferUser(),
                    project.getProjectId(),
                    project.getProjectTitle(),
                    project.getProjectLeader().getUserName(),
                    project.getAmountOfTeammembers(),
                    getProjectStatus(project, instance),
                    timeStamp,
                    getCourseOfStudies(project, instance)
            ));

            if (projectStatuscode != 200) {
                log.error("Project " + project.getProjectId() + " und Members konnten nicht an INdigit übertragen werden, StatusCode: " + projectStatuscode);
                newFailedProjectTransfer(project, projectStatuscode);
                return projectStatuscode;
            }
        }

        // transfer members of the project to INdigit
        List<ProjectMembersINdigitDTO> members = Lists.newArrayList();
        for (RelationTeamUser relation : relationTeamUserDAO.findByProject(project)) {
            ProjectMembersINdigitDTO dto = new ProjectMembersINdigitDTO();
            dto.projectid = project.getProjectId();
            dto.username = relation.getUserId().getUserName();
            dto.matnr = relation.getUserId().getMatrikelNumber();
            members.add(dto);
        }
        int memberStatusCode = 200;
        if (!members.isEmpty())
            memberStatusCode = instance.transferProjectMembers(members);

        if (memberStatusCode != 200) {
            log.error("Members of project " + project.getProjectId() + " could not be transferred to INdigit, StatusCode: " + memberStatusCode);
            newFailedProjectMembersTransfer(project, memberStatusCode);
            return memberStatusCode;
        }

        log.info("Project " + project.getProjectId() + " and its members were successfully transferred to INdigit");

        project.setStatusCode(memberStatusCode);
        Project finalUpdatedProject = projectDAO.update(project.getProjectId(), project);

        FailedINdigitTransfer failedTransferToThatProject = failedINdigitTransfersRepo.findByProject(project).orElse(null);
        if (failedTransferToThatProject != null) {
            if (failedTransferToThatProject.isProjectTransferFailed() && isOnlyMembersTransferFailed)
                retryTransfer(failedTransferToThatProject);
            else
                failedINdigitTransfersRepo.delete(failedTransferToThatProject);
        }
        return memberStatusCode;
    }

    @Override
    public int retryTransfer(FailedINdigitTransfer failedTransfer) throws INdigitApiService.HttpStatusCodeException, IOException {
        if (failedINdigitTransfersRepo.findById(failedTransfer.getId()).isEmpty()) {
            // If it has been removed, then it must have been successfully sent to INdigit
            log.info(FailedINdigitTransfer.class.getName() + " with " + Project.class.getName() + " -id = " + failedTransfer.getProject().getProjectId() + "has already been removed from the database!");
            return 200;
        }

        //A timestamp is set when a project is created and when a change of state takes place.
        SimpleDateFormat SDFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String curr_date = SDFormat.format(cal.getTime());

        return transfer(failedTransfer.getProject(), curr_date, !failedTransfer.isProjectTransferFailed());
    }

    /**
     * Check for equal status in Backend(Metaproject) and Backend(Indigit)
     */
    private int getProjectStatus(Project newProject, INdigitApiMetaproject instance) throws INdigitApiService.HttpStatusCodeException, IOException {
        int projectStatus = 41;
        for (INdigitApiMetaproject.Status status : instance.listStatus()) {
            if (newProject.getProjectStatus().toLowerCase(Locale.ROOT).equals(status.getName().replace("Projekt", "")))
                projectStatus = status.getStatusid();
        }
        return projectStatus;
    }

    /**
     * Course of studies of the created project is determined based on the abbreviation.
     */
    private int getCourseOfStudies(Project newProject, INdigitApiMetaproject instance) throws INdigitApiService.HttpStatusCodeException, IOException {
        int courseOfStudies = 0;
        for (INdigitApiMetaproject.StudyProgram studyProgram : instance.listStudyPrograms()) {
            if (newProject.getMetaproject().getCourseOfStudies() != null &&
                    newProject.getMetaproject().getCourseOfStudies().toUpperCase(Locale.ROOT).equals(studyProgram.getAbbreviation().toUpperCase(Locale.ROOT))) {
                courseOfStudies = studyProgram.getSpid();
            }
        }
        return courseOfStudies;
    }

}
