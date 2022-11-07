package main.java.org.htwg.konstanz.metaproject.services;

import main.java.org.htwg.konstanz.metaproject.communication.CommAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.EmailTemplateType;
import main.java.org.htwg.konstanz.metaproject.communication.implementations.*;
import main.java.org.htwg.konstanz.metaproject.entities.*;
import main.java.org.htwg.konstanz.metaproject.enums.ProjectAssignmentStatus;
import main.java.org.htwg.konstanz.metaproject.mail.EmailCreatorService;
import main.java.org.htwg.konstanz.metaproject.persistance.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;

/**
 * Implementation for the creator service for communications.
 *
 * @author SiKelle
 */
@Service
public class CommunicationCreatorServiceImpl implements CommunicationCreatorService {

    public static final Logger log = LoggerFactory.getLogger(CommunicationCreatorServiceImpl.class);

    private final CommDAO commDao;

    private final ProjectDAO projectDao;

    private final TeamDAO teamDao;

    private final RelationTeamUserDAO relationTeamUserDao;

    private final EmailCreatorService emailCreatorService;

    private final RelationMetaprojectUserDAO rMUDao;

    private final RelationTeamUserDAO rTUDao;

    public CommunicationCreatorServiceImpl(CommDAO commDao, ProjectDAO projectDao, TeamDAO teamDao, RelationTeamUserDAO relationTeamUserDao, EmailCreatorService emailCreatorService, RelationMetaprojectUserDAO rMUDao, RelationTeamUserDAO rTUDao) {
        this.commDao = commDao;
        this.projectDao = projectDao;
        this.teamDao = teamDao;
        this.relationTeamUserDao = relationTeamUserDao;
        this.emailCreatorService = emailCreatorService;
        this.rMUDao = rMUDao;
        this.rTUDao = rTUDao;
    }

    private <T extends CommAbstract> T createCommunication(Class<T> type, T newComm) {
        return commDao.save(type, newComm);
    }

    /**
     * Method for implement communication when a Metaproject leader is
     * appointed.
     *
     * @return null
     */
    @Override
    public CommMetaprojectLeaderAppointment sendCommMetaprojectLeaderAppointment(User currentUser, User newLeader,
                                                                                 Metaproject metaproject) {
        if (!currentUser.getUserId().equals(metaproject.getMetaprojectLeader().getUserId())) {
            CommMetaprojectLeaderAppointment comm = new CommMetaprojectLeaderAppointment();
            comm.setMetaproject(metaproject);
            comm.setSendingUser(currentUser);
            comm.setNewLeader(newLeader);
            comm.setTargetUser(newLeader);
            comm.setTemplateType(EmailTemplateType.METAPROJECT_LEADER_APPOINTMENT);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.METAPROJECT_LEADER_APPOINTMENT));
            createCommunication(CommMetaprojectLeaderAppointment.class, comm);
        }
        return null;
    }

    @Override
    public CommProjectLeaderAppointment sendCommProjectLeaderAppointment(User currentUser, User newLeader,
                                                                         Project project) {
        if (!currentUser.getUserId().equals(project.getProjectLeader().getUserId())) {
            CommProjectLeaderAppointment comm = new CommProjectLeaderAppointment();
            comm.setProject(project);
            comm.setSendingUser(currentUser);
            comm.setNewLeader(newLeader);
            comm.setTargetUser(newLeader);
            comm.setTemplateType(EmailTemplateType.PROJECT_LEADER_APPOINTMENT);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.PROJECT_LEADER_APPOINTMENT));
            createCommunication(CommProjectLeaderAppointment.class, comm);
        }
        return null;
    }

    @Override
    public CommTeamLeaderAppointment sendCommTeamLeaderAppointment(User currentUser, User newLeader, Team team) {
        if (!currentUser.getUserId().equals(team.getTeamLeader().getUserId())) {
            CommTeamLeaderAppointment comm = new CommTeamLeaderAppointment();
            comm.setTeam(team);
            comm.setSendingUser(currentUser);
            comm.setNewLeader(newLeader);
            comm.setTargetUser(newLeader);
            comm.setTemplateType(EmailTemplateType.TEAM_LEADER_APPOINTMENT);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_LEADER_APPOINTMENT));
            createCommunication(CommTeamLeaderAppointment.class, comm);
        }
        return null;
    }

    /**
     * Method for implement communication when metaproject leader changed
     *
     * @return null
     */
    @Override
    public CommMetaprojectLeaderChange sendCommMetaprojectLeaderChange(User currentUser, User newLeader, User oldLeader,
                                                                       Metaproject metaproject) {
        CommMetaprojectLeaderChange comm = new CommMetaprojectLeaderChange();
        // send to old leader if this was done by third user
        if (!currentUser.getUserId().equals(oldLeader.getUserId())) {
            comm.setMetaproject(metaproject);
            comm.setNewLeader(newLeader);
            comm.setOldLeader(oldLeader);
            comm.setTargetUser(oldLeader);
            comm.setSendingUser(currentUser);
            comm.setTemplateType(EmailTemplateType.METAPROJECT_LEADER_CHANGE_OLD);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.METAPROJECT_LEADER_CHANGE_OLD));
            log.info("Send to old meta leader: {}", comm.getTargetUser().getFullName());
            createCommunication(CommMetaprojectLeaderChange.class, comm);
        }
        // send to all project leaders
        for (Project project : projectDao.findByMetaproject(metaproject.getMetaprojectId())) {
            comm.setMetaproject(metaproject);
            comm.setNewLeader(newLeader);
            comm.setOldLeader(oldLeader);
            comm.setTargetUser(project.getProjectLeader());
            comm.setSendingUser(currentUser);
            comm.setTemplateType(EmailTemplateType.METAPROJECT_LEADER_CHANGE_COMMON);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.METAPROJECT_LEADER_CHANGE_COMMON));
            log.info("Send to project leaders: {}", comm.getTargetUser().getFullName());
            createCommunication(CommMetaprojectLeaderChange.class, comm);
        }
        // send to all team leaders
        for (Team team : teamDao.findByMetaprojectId(metaproject.getMetaprojectId())) {
            comm.setMetaproject(metaproject);
            comm.setNewLeader(newLeader);
            comm.setOldLeader(oldLeader);
            comm.setTargetUser(team.getTeamLeader());
            comm.setSendingUser(currentUser);
            comm.setTemplateType(EmailTemplateType.METAPROJECT_LEADER_CHANGE_COMMON);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.METAPROJECT_LEADER_CHANGE_COMMON));
            log.info("Send to team leaders: {}", comm.getTargetUser().getFullName());
            createCommunication(CommMetaprojectLeaderChange.class, comm);
        }
        // send to new leader
        comm.setMetaproject(metaproject);
        comm.setNewLeader(newLeader);
        comm.setOldLeader(oldLeader);
        comm.setTargetUser(newLeader);
        comm.setSendingUser(currentUser);
        comm.setTemplateType(EmailTemplateType.METAPROJECT_LEADER_CHANGE_NEW);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.METAPROJECT_LEADER_CHANGE_NEW));
        log.info("Send to new meta leader: {}", comm.getTargetUser().getFullName());
        return createCommunication(CommMetaprojectLeaderChange.class, comm);
    }

    /**
     * Method for implement communication when the team leader is changed.
     *
     * @return null
     */
    @Override
    public CommTeamLeaderChange sendCommTeamLeaderChange(User currentUser, User newLeader, User oldLeader, Team team) {
        CommTeamLeaderChange comm = new CommTeamLeaderChange();
        // send to old leader if this was done by third user
        if (!currentUser.getUserId().equals(oldLeader.getUserId())) {
            comm.setTeam(team);
            comm.setSendingUser(currentUser);
            comm.setNewLeader(newLeader);
            comm.setOldLeader(oldLeader);
            comm.setTargetUser(oldLeader);
            comm.setTemplateType(EmailTemplateType.TEAM_LEADER_CHANGE_OLD);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_LEADER_CHANGE_OLD));
            log.info("Send to old team leader: {}", comm.getTargetUser().getFullName());
            createCommunication(CommTeamLeaderChange.class, comm);
        }

        // Nachricht an alle Teammitglieder
        Collection<RelationTeamUser> teamUsers = rTUDao.findByTeam(team);
        for (RelationTeamUser rtu : teamUsers) {
            comm.setTeam(team);
            comm.setSendingUser(currentUser);
            comm.setNewLeader(newLeader);
            comm.setOldLeader(oldLeader);
            comm.setTargetUser(rtu.getUserId());
            comm.setTemplateType(EmailTemplateType.TEAM_LEADER_CHANGE_COMMON);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_LEADER_CHANGE_COMMON));
            createCommunication(CommTeamLeaderChange.class, comm);
        }

        // Projektleiter des Projekts, das diesem Team zugeordnet ist
        if (team.getProjectId() != null
                && team.getProjectAssignmentStatus().equals(ProjectAssignmentStatus.FINAL)) {
            comm.setTeam(team);
            comm.setSendingUser(currentUser);
            comm.setNewLeader(newLeader);
            comm.setOldLeader(oldLeader);
            comm.setTargetUser(team.getProjectId().getProjectLeader());
            comm.setTemplateType(EmailTemplateType.TEAM_LEADER_CHANGE_COMMON);
            comm.setEmail(emailCreatorService.getEmailForType(comm,
                    EmailTemplateType.TEAM_LEADER_CHANGE_COMMON));
            createCommunication(CommTeamLeaderChange.class, comm);
        }

        // send to new leader
        comm.setTeam(team);
        comm.setNewLeader(newLeader);
        comm.setOldLeader(oldLeader);
        comm.setSendingUser(currentUser);
        comm.setTargetUser(newLeader);
        comm.setTemplateType(EmailTemplateType.TEAM_LEADER_CHANGE_NEW);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_LEADER_CHANGE_NEW));
        log.info("Send to new team leader: {}", comm.getTargetUser().getFullName());
        createCommunication(CommTeamLeaderChange.class, comm);

        return null;
    }

    /**
     * Method for implement communication when the project leader changed.
     *
     * @return null
     */
    @Override
    public CommProjectLeaderChange sendCommProjectLeaderChange(User currentUser, User newLeader, User oldLeader,
                                                               Project project) {
        CommProjectLeaderChange comm = new CommProjectLeaderChange();
        // send to old leader if this was done by third user
        if (!currentUser.getUserId().equals(oldLeader.getUserId())) {
            comm.setProject(project);
            comm.setNewLeader(newLeader);
            comm.setOldLeader(oldLeader);
            comm.setTargetUser(oldLeader);
            comm.setSendingUser(currentUser);
            comm.setTemplateType(EmailTemplateType.PROJECT_LEADER_CHANGE_OLD);
            comm.setEmail(
                    emailCreatorService.getEmailForType(comm, EmailTemplateType.PROJECT_LEADER_CHANGE_OLD));
            log.info("Send to old project leader: {}", comm.getTargetUser().getFullName());
            createCommunication(CommProjectLeaderChange.class, comm);
        }
        // send to metaproject leader
        if (!currentUser.getUserId().equals(project.getMetaproject().getMetaprojectLeader().getUserId())) {
            comm.setProject(project);
            comm.setNewLeader(newLeader);
            comm.setOldLeader(oldLeader);
            comm.setTargetUser(project.getMetaproject().getMetaprojectLeader());
            comm.setSendingUser(currentUser);
            comm.setTemplateType(EmailTemplateType.PROJECT_LEADER_CHANGE_COMMON);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.PROJECT_LEADER_CHANGE_COMMON));
            log.info("Send to project leader: {}", comm.getTargetUser().getFullName());
            createCommunication(CommProjectLeaderChange.class, comm);
        }

        // send to all team leaders
        for (Team team : teamDao.findByMetaprojectId(project.getMetaproject().getMetaprojectId())) {
            comm.setProject(project);
            comm.setNewLeader(newLeader);
            comm.setOldLeader(oldLeader);
            comm.setTargetUser(team.getTeamLeader());
            comm.setSendingUser(currentUser);
            comm.setTemplateType(EmailTemplateType.PROJECT_LEADER_CHANGE_COMMON);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.PROJECT_LEADER_CHANGE_COMMON));
            log.info("Send to team leader: {}", comm.getTargetUser().getFullName());
            createCommunication(CommProjectLeaderChange.class, comm);
        }
        // send to new leader
        comm.setProject(project);
        comm.setNewLeader(newLeader);
        comm.setOldLeader(oldLeader);
        comm.setTargetUser(newLeader);
        comm.setSendingUser(currentUser);
        comm.setTemplateType(EmailTemplateType.PROJECT_LEADER_CHANGE_NEW);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.PROJECT_LEADER_CHANGE_NEW));
        log.info("Send to new leader: {}", comm.getTargetUser().getFullName());
        return createCommunication(CommProjectLeaderChange.class, comm);
    }

    /**
     * Method for implement communication when a team sends a final priorisation.
     *
     * @return null
     */
    @Override
    public CommTeamSendPrio sendCommTeamSendPrio(User currentUser, Team team) {
        CommTeamSendPrio comm = new CommTeamSendPrio();
        comm.setTeam(team);
        comm.setSendingUser(currentUser);
        comm.setTargetUser(team.getMetaProjectId().getMetaprojectLeader());
        comm.setTemplateType(EmailTemplateType.TEAM_SEND_PRIO);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_SEND_PRIO));
        createCommunication(CommTeamSendPrio.class, comm);

        return null;
    }

    /**
     * Method for implement communication when a project is assigned to a team
     *
     * @return null
     */

    @Override
    public CommAssignProjectToTeam sendCommAssingProjectToTeam(User currentUser, Metaproject metaproject) {
        CommAssignProjectToTeam comm = new CommAssignProjectToTeam();
        // send to all project leaders
        for (Project project : projectDao.findByMetaproject(metaproject.getMetaprojectId())) {
            comm.setMetaproject(metaproject);
            comm.setTargetUser(project.getProjectLeader());
            comm.setSendingUser(currentUser);
            comm.setTemplateType(EmailTemplateType.ASSIGN_PROJECT_TO_TEAM);
            comm.setEmail(
                    emailCreatorService.getEmailForType(comm, EmailTemplateType.ASSIGN_PROJECT_TO_TEAM));
            log.info("Send to project leader: {}", comm.getTargetUser().getFullName());
            createCommunication(CommAssignProjectToTeam.class, comm);
        }
        // send to all team leaders
        for (Team team : teamDao.findByMetaprojectId(metaproject.getMetaprojectId())) {
            comm.setMetaproject(metaproject);
            comm.setTargetUser(team.getTeamLeader());
            comm.setSendingUser(currentUser);
            comm.setTemplateType(EmailTemplateType.ASSIGN_PROJECT_TO_TEAM);
            comm.setEmail(
                    emailCreatorService.getEmailForType(comm, EmailTemplateType.ASSIGN_PROJECT_TO_TEAM));
            log.info("Send to team leader: {}", comm.getTargetUser().getFullName());
            createCommunication(CommAssignProjectToTeam.class, comm);

        }
        return null;
    }


    /**
     * Method for implement communication when a project is assigned to a master team
     *
     * @return null
     */

    @Override
    public CommAssignProjectToTeam sendCommAssingProjectToTeamMaster(User currentUser, Metaproject metaproject, Project project) {
        CommAssignProjectToTeam comm = new CommAssignProjectToTeam();


        // send to all team leaders
        Team team = teamDao.findByProjectId(project.getProjectId());
        for (RelationTeamUser relation : relationTeamUserDao.findByTeam(team)) {
            comm.setMetaproject(metaproject);
            comm.setTargetUser(relation.getUserId());
            comm.setSendingUser(currentUser);
            comm.setTemplateType(EmailTemplateType.ASSIGN_PROJECT_TO_TEAM);
            comm.setEmail(
                    emailCreatorService.getEmailForType(comm, EmailTemplateType.ASSIGN_PROJECT_TO_TEAM));
            log.info("Send to team leader: {}", comm.getTargetUser().getFullName());
            createCommunication(CommAssignProjectToTeam.class, comm);

        }
        return null;
    }


    /**
     * Method for implement communication when a team is deleted.
     */
    @Override
    public CommTeamDelete sendCommTeamDelete(User currentUser, Project project, Team team, Collection<RelationTeamUser> relations) {
        CommTeamDelete comm = new CommTeamDelete();
        // Nachricht an Metaprojektleiter
        if (!currentUser.getUserId().equals(team.getMetaProjectId().getMetaprojectLeader().getUserId())) {
            comm.setTeamName(team.getTeamName());
            comm.setMetaproject(team.getMetaProjectId());
            comm.setSendingUser(currentUser);
            comm.setTargetUser(team.getMetaProjectId().getMetaprojectLeader());
            comm.setTemplateType(EmailTemplateType.TEAM_DELETE_COMMON);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_DELETE_COMMON));
            createCommunication(CommTeamDelete.class, comm);
        }
        // Nachricht an alle Teammitglieder
        for (RelationTeamUser rtu : relations) {
            comm.setTeamName(team.getTeamName());
            comm.setMetaproject(team.getMetaProjectId());
            comm.setSendingUser(currentUser);
            comm.setTargetUser(rtu.getUserId());
            comm.setTemplateType(EmailTemplateType.TEAM_DELETE_COMMON);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_DELETE_COMMON));
            createCommunication(CommTeamDelete.class, comm);
        }
        // Nachricht an Projektleiter, dessen Projekt dem Team zugeordnet ist

        if (team.getProjectId() != null
                && team.getProjectAssignmentStatus().equals(ProjectAssignmentStatus.FINAL)) {
            comm.setTeamName(team.getTeamName());
            comm.setMetaproject(team.getMetaProjectId());
            comm.setSendingUser(currentUser);
            comm.setTargetUser(team.getProjectId().getProjectLeader());
            comm.setTemplateType(EmailTemplateType.TEAM_DELETE_SPECIFIC);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_DELETE_SPECIFIC));
            createCommunication(CommTeamDelete.class, comm);
        }

        return null;
    }

    /**
     * Method for implement communication when a project is deleted.
     *
     * @return null
     */
    @Override
    public CommProjectDelete sendCommProjectDelete(User currentUser, Project project) {

        CommProjectDelete comm = new CommProjectDelete();

        // Nachricht an Metaprojektleiter
        if (!currentUser.getUserId().equals(project.getMetaproject().getMetaprojectLeader().getUserId())) {

            comm.setProjectTitle(project.getProjectTitle());
            comm.setSendingUser(currentUser);
            comm.setMetaproject(project.getMetaproject());
            comm.setTargetUser(project.getMetaproject().getMetaprojectLeader());
            comm.setTemplateType(EmailTemplateType.PROJECT_DELETE_COMMON);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.PROJECT_DELETE_COMMON));
            createCommunication(CommProjectDelete.class, comm);

        }

        Team teamFound = teamDao.findByProjectId(project.getProjectId());
        Collection<Team> team = teamDao.findByMetaprojectId(project.getMetaproject().getMetaprojectId());
        Iterator<Team> itr = team.iterator();

        if (teamFound == null || teamFound.getProjectAssignmentStatus().equals(ProjectAssignmentStatus.TEMPORARY)) {
            while (itr.hasNext()) {
                Team str = itr.next();
                comm.setProjectTitle(project.getProjectTitle());
                comm.setSendingUser(currentUser);
                comm.setMetaproject(project.getMetaproject());
                comm.setTargetUser(str.getTeamLeader());
                comm.setTemplateType(EmailTemplateType.PROJECT_DELETE_COMMON);
                comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.PROJECT_DELETE_COMMON));
                createCommunication(CommProjectDelete.class, comm);
            }
        } else if (teamFound.getProjectAssignmentStatus().equals(ProjectAssignmentStatus.FINAL)) {
            comm.setProjectTitle(project.getProjectTitle());
            comm.setSendingUser(currentUser);
            comm.setMetaproject(project.getMetaproject());
            comm.setTargetUser(teamFound.getTeamLeader());
            comm.setTemplateType(EmailTemplateType.PROJECT_DELETE_SPECIFIC);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.PROJECT_DELETE_SPECIFIC));
            createCommunication(CommProjectDelete.class, comm);
        }
        return null;
    }

    /**
     * Method for implement communication when a metaproject is deleted.
     *
     * @return null
     */

    @Override
    public CommMetaprojectDelete sendCommMetaprojectDelete(User currentUser, Metaproject metaproject) {
        CommMetaprojectDelete comm = new CommMetaprojectDelete();

        Collection<RelationMetaprojectUser> metaUser = rMUDao.findByMetaproject(metaproject);

        for (RelationMetaprojectUser str : metaUser) {
            comm.setMetaprojectTitle(metaproject.getMetaprojectTitle());
            comm.setSendingUser(currentUser);
            comm.setTargetUser(str.getUserId());
            comm.setTemplateType(EmailTemplateType.METAPROJECT_DELETE);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.METAPROJECT_DELETE));
            createCommunication(CommMetaprojectDelete.class, comm);
        }

        for (Project project : projectDao.findByMetaproject(metaproject.getMetaprojectId())) {
            comm.setMetaprojectTitle(metaproject.getMetaprojectTitle());
            comm.setTargetUser(project.getProjectLeader());
            comm.setSendingUser(currentUser);
            comm.setTemplateType(EmailTemplateType.METAPROJECT_DELETE);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.METAPROJECT_DELETE));
            log.info("Send to project leader: {}", comm.getTargetUser().getFullName());
            createCommunication(CommMetaprojectDelete.class, comm);
        }
        return null;
    }


    /**
     * Method for implement communication when a user is invited
     *
     * @return null
     */
    @Override
    public CommTeamInvite sendCommTeamInvite(User invitedUser, Team team) {
        CommTeamInvite comm = new CommTeamInvite();
        comm.setTeam(team);
        comm.setTargetUser(invitedUser);
        comm.setSendingUser(team.getTeamLeader());
        comm.setTemplateType(EmailTemplateType.TEAM_INVITE);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_INVITE));
        createCommunication(CommTeamInvite.class, comm);

        return null;
    }

    /**
     * Method for implement communication when team get a user request
     *
     * @return null
     */
    @Override
    public CommTeamRequest sendCommTeamRequest(User currentUser, Team team) {
        CommTeamRequest comm = new CommTeamRequest();
        comm.setTeam(team);
        comm.setTargetUser(team.getTeamLeader());
        comm.setSendingUser(currentUser);
        comm.setTemplateType(EmailTemplateType.TEAM_REQUEST);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_REQUEST));
        createCommunication(CommTeamRequest.class, comm);

        return null;
    }


    /**
     * Method for implement communication when a metaproject leader add member
     * to metaproject
     *
     * @return null
     */

    @Override
    public CommMetaprojectAddMember sendCommMetaprojectAddMember(User currentUser, Metaproject metaproject,
                                                                 User newMember) {
        CommMetaprojectAddMember comm = new CommMetaprojectAddMember();

        comm.setMetaproject(metaproject);
        comm.setSendingUser(currentUser);
        comm.setTargetUser(newMember);
        comm.setNewMember(newMember);
        comm.setTemplateType(EmailTemplateType.METAPROJECT_ADD_MEMBER);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.METAPROJECT_ADD_MEMBER));
        return createCommunication(CommMetaprojectAddMember.class, comm);

    }

    @Override
    public CommMetaprojectleaderAddTeamMember sendCommMetaprojectleaderAddTeamMember(User currentUser, User newMember, Team team) {
        CommMetaprojectleaderAddTeamMember comm = new CommMetaprojectleaderAddTeamMember();

        comm.setTeam(team);
        comm.setSendingUser(currentUser);
        comm.setNewMember(newMember);
        comm.setTargetUser(newMember);
        comm.setTemplateType(EmailTemplateType.METAPROJECT_LEADER_ADD_TEAM_MEMBER);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.METAPROJECT_LEADER_ADD_TEAM_MEMBER));
        return createCommunication(CommMetaprojectleaderAddTeamMember.class, comm);

    }


    /**
     * Method for implement communication when an application is rejected.
     *
     * @return null
     */
    @Override
    public CommTeamApplicationReject sendCommTeamApplicationReject(User sendingUser, Team team) {
        CommTeamApplicationReject comm = new CommTeamApplicationReject();

        comm.setTeam(team);
        comm.setSendingUser(team.getTeamLeader());
        comm.setTargetUser(sendingUser);
        comm.setTemplateType(EmailTemplateType.TEAM_APPLICATION_REJECT);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_APPLICATION_REJECT));
        createCommunication(CommTeamApplicationReject.class, comm);

        return null;
    }

    /**
     * Method for implement communication when a team invitation is rejected.
     *
     * @return null
     */
    @Override
    public CommTeamInvitationReject sendCommTeamInvitationReject(User currentUser, User receivingMember, Team team) {
        CommTeamInvitationReject comm = new CommTeamInvitationReject();

        comm.setTeam(team);
        comm.setSendingUser(currentUser);
        comm.setReceivingMember(receivingMember);
        comm.setTargetUser(receivingMember);
        comm.setTemplateType(EmailTemplateType.TEAM_INVITATION_REJECT);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_INVITATION_REJECT));
        createCommunication(CommTeamInvitationReject.class, comm);

        return null;
    }

    /**
     * Method for implement communication when a team invitation is withdrawd.
     */
    @Override
    public CommTeamInvitationWithdraw sendCommTeamInvitationWithdraw(User currentUser, User invitedMember, Team team) {
        CommTeamInvitationWithdraw comm = new CommTeamInvitationWithdraw();

        comm.setTeam(team);
        comm.setSendingUser(currentUser);
        comm.setInvitedMember(invitedMember);
        comm.setTargetUser(invitedMember);
        comm.setTemplateType(EmailTemplateType.TEAM_INVITATION_WITHDRAW);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_INVITATION_WITHDRAW));
        createCommunication(CommTeamInvitationWithdraw.class, comm);

        return null;
    }


    /**
     * Method for implement communication when a member is removed from team.
     *
     * @return null
     */
    @Override
    public CommTeamRemoveMember sendCommTeamRemoveMember(User currentUser, User deletedUser, Team team) {
        CommTeamRemoveMember comm = new CommTeamRemoveMember();
        // sending to team leader
        if (!currentUser.getUserId().equals(team.getTeamLeader().getUserId())) {
            comm.setTeam(team);
            comm.setSendingUser(currentUser);
            comm.setDeletedUser(deletedUser);
            comm.setTargetUser(team.getTeamLeader());
            comm.setTemplateType(EmailTemplateType.TEAM_REMOVE_MEMBER_COMMON);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_REMOVE_MEMBER_COMMON));
            log.info("Send to team leader: {}", comm.getTargetUser().getFullName());
            createCommunication(CommTeamRemoveMember.class, comm);
        }

        // sending to team members
        Collection<RelationTeamUser> teamUsers = rTUDao.findByTeam(team);
        for (RelationTeamUser rtu : teamUsers) {
            comm.setTeam(team);
            comm.setSendingUser(currentUser);
            comm.setDeletedUser(deletedUser);
            comm.setTargetUser(rtu.getUserId());
            comm.setTemplateType(EmailTemplateType.TEAM_REMOVE_MEMBER_COMMON);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_REMOVE_MEMBER_COMMON));
            createCommunication(CommTeamRemoveMember.class, comm);
        }

        // sending to project leader if a project was assigned to this team
        if (team.getProjectId() != null
                && team.getProjectAssignmentStatus().equals(ProjectAssignmentStatus.FINAL)) {
            comm.setTeam(team);
            comm.setDeletedUser(deletedUser);
            comm.setSendingUser(currentUser);
            comm.setTargetUser(team.getProjectId().getProjectLeader());
            comm.setTemplateType(EmailTemplateType.TEAM_REMOVE_MEMBER_LEADER);
            comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_REMOVE_MEMBER_LEADER));
            createCommunication(CommTeamRemoveMember.class, comm);
        }

        // sending to deleted user
        comm.setTeam(team);
        comm.setDeletedUser(deletedUser);
        comm.setSendingUser(currentUser);
        comm.setTargetUser(deletedUser);
        comm.setTemplateType(EmailTemplateType.TEAM_REMOVE_MEMBER_SPECIFIC);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_REMOVE_MEMBER_SPECIFIC));
        createCommunication(CommTeamRemoveMember.class, comm);

        return null;
    }


    // team invitation accept
    @Override
    public CommTeamInvitationAccept sendCommTeamInvitationAccept(User currentUser, Team team) {
        CommTeamInvitationAccept comm = new CommTeamInvitationAccept();

        comm.setTeam(team);
        comm.setSendingUser(currentUser);
        comm.setTargetUser(team.getTeamLeader());
        comm.setTemplateType(EmailTemplateType.TEAM_INVITATION_ACCEPT);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_INVITATION_ACCEPT));
        createCommunication(CommTeamInvitationAccept.class, comm);

        return null;
    }

    // team application accept
    @Override
    public CommTeamApplicationAccept sendCommTeamApplicationAccept(User currentUser, User receivingMember, Team team) {
        CommTeamApplicationAccept comm = new CommTeamApplicationAccept();

        comm.setTeam(team);
        comm.setSendingUser(currentUser);
        comm.setReceivingMember(receivingMember);
        comm.setTargetUser(receivingMember);
        comm.setTemplateType(EmailTemplateType.TEAM_APPLICATION_ACCEPT);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_APPLICATION_ACCEPT));
        createCommunication(CommTeamApplicationAccept.class, comm);

        return null;
    }

    // team application withdraw
    @Override
    public CommTeamApplicationWithdraw sendCommTeamApplicationWithdraw(User sendingUser, Team team) {
        CommTeamApplicationWithdraw comm = new CommTeamApplicationWithdraw();

        comm.setTeam(team);
        comm.setSendingUser(sendingUser);
        comm.setTargetUser(team.getTeamLeader());
        comm.setTemplateType(EmailTemplateType.TEAM_APPLICATION_WITHDRAW);
        comm.setEmail(emailCreatorService.getEmailForType(comm, EmailTemplateType.TEAM_APPLICATION_WITHDRAW));
        createCommunication(CommTeamApplicationWithdraw.class, comm);

        return null;
    }

}
