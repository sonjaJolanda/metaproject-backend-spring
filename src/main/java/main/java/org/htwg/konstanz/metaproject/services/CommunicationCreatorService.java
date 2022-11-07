package main.java.org.htwg.konstanz.metaproject.services;

import main.java.org.htwg.konstanz.metaproject.communication.implementations.*;
import main.java.org.htwg.konstanz.metaproject.entities.*;

import java.util.Collection;

/**
 * This service should be used to create communication objects. These service
 * method implements email handling.
 *
 * @author SiKelle
 */
public interface CommunicationCreatorService {

    /**
     * Send all communications for the team invite. This method handles every
     * communication which has to send in this case.
     */
    CommTeamInvite sendCommTeamInvite(User invitedUser, Team team);

    /**
     * Send all communications for the team request. This method handles every
     * communication which has to send in this case.
     */
    CommTeamRequest sendCommTeamRequest(User currentUser, Team team);

    /**
     * Send all communications for the metaproject leader appointment. This
     * method handles every communication which has to send in this case.
     */
    CommMetaprojectLeaderAppointment sendCommMetaprojectLeaderAppointment(User currentUser, User newLeader, Metaproject metaproject);

    /**
     * Send all communications for the metaproject leader change. This method
     * handles every communication which has to send in this case.
     */
    CommMetaprojectLeaderChange sendCommMetaprojectLeaderChange(User currentUser, User newLeader, User oldLeader, Metaproject metaproject);

    CommAssignProjectToTeam sendCommAssingProjectToTeamMaster(User currentUser, Metaproject metaproject, Project project);

    CommAssignProjectToTeam sendCommAssingProjectToTeam(User currentUser, Metaproject metaproject);

    CommMetaprojectAddMember sendCommMetaprojectAddMember(User currentUser, Metaproject metaproject, User newMember);

    CommMetaprojectDelete sendCommMetaprojectDelete(User currentUser, Metaproject metaproject);

    CommProjectDelete sendCommProjectDelete(User currentUser, Project project);

    CommProjectLeaderChange sendCommProjectLeaderChange(User currentUser, User newLeader, User oldLeader, Project project);

    CommTeamDelete sendCommTeamDelete(User currentUser, Project project, Team team, Collection<RelationTeamUser> relations);

    CommTeamApplicationReject sendCommTeamApplicationReject(User currentUser, Team team);

    CommTeamInvitationReject sendCommTeamInvitationReject(User currentUser, User receivingMember, Team team);

    CommTeamInvitationWithdraw sendCommTeamInvitationWithdraw(User currentUser, User invitedMember, Team team);

    CommTeamLeaderChange sendCommTeamLeaderChange(User currentUser, User newLeader, User oldLeader, Team team);

    CommTeamRemoveMember sendCommTeamRemoveMember(User currentUser, User deletedUser, Team team);

    CommTeamSendPrio sendCommTeamSendPrio(User currentUser, Team team);

    CommTeamInvitationAccept sendCommTeamInvitationAccept(User currentUser, Team team);

    CommTeamApplicationAccept sendCommTeamApplicationAccept(User currentUser, User receivingMember, Team team);

    CommTeamApplicationWithdraw sendCommTeamApplicationWithdraw(User sendingUser, Team team);

    CommProjectLeaderAppointment sendCommProjectLeaderAppointment(User currentUser, User newLeader, Project project);

    CommTeamLeaderAppointment sendCommTeamLeaderAppointment(User currentUser, User newLeader, Team team);

    CommMetaprojectleaderAddTeamMember sendCommMetaprojectleaderAddTeamMember(User currentUser, User newMember, Team team);
}
