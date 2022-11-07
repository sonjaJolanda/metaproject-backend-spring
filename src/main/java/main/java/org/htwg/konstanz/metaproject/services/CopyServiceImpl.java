package main.java.org.htwg.konstanz.metaproject.services;

import main.java.org.htwg.konstanz.metaproject.dtos.CopyMetaprojectDTO;
import main.java.org.htwg.konstanz.metaproject.entities.*;
import main.java.org.htwg.konstanz.metaproject.enums.ProjectAssignmentStatus;
import main.java.org.htwg.konstanz.metaproject.enums.TeamMemberStatus;
import main.java.org.htwg.konstanz.metaproject.enums.UpdateStatus;
import main.java.org.htwg.konstanz.metaproject.persistance.*;
import main.java.org.htwg.konstanz.metaproject.rights.DefaultRoles;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CopyServiceImpl implements CopyService {

    private final RightService rightService;

    private final TeamDAO teamDao;

    private final MetaprojectDAO metaprojectDao;

    private final RoleDAO roleDao;

    private final PrioTeamProjectDAO prioTeamProjectDao;

    private final ProjectDAO projectDao;

    private final RelationTeamUserDAO relTeamUserDao;

    private final RelationMetaprojectUserDAO relMetaUserDao;

    private final CommunicationCreatorService communicationCreatorService;

    public CopyServiceImpl(RightService rightService, TeamDAO teamDao, MetaprojectDAO metaprojectDao, RoleDAO roleDao, PrioTeamProjectDAO prioTeamProjectDao, ProjectDAO projectDao, RelationTeamUserDAO relTeamUserDao, RelationMetaprojectUserDAO relMetaUserDao, CommunicationCreatorService communicationCreatorService) {
        this.rightService = rightService;
        this.teamDao = teamDao;
        this.metaprojectDao = metaprojectDao;
        this.roleDao = roleDao;
        this.prioTeamProjectDao = prioTeamProjectDao;
        this.projectDao = projectDao;
        this.relTeamUserDao = relTeamUserDao;
        this.relMetaUserDao = relMetaUserDao;
        this.communicationCreatorService = communicationCreatorService;
    }


    public Long copyMetaproject(Metaproject oldMp, CopyMetaprojectDTO copyMp, User leader, User creator, boolean isTeamReg) {
        Metaproject newMp = (Metaproject) oldMp.clone();
        newMp.setMetaprojectTitle(copyMp.getTitle());
        newMp.setMetaprojectLeader(leader);
        newMp = metaprojectDao.save(newMp);

        roleDao.addRoleMetaprojectToUser(creator, newMp, DefaultRoles.METAPROJECT_OWNER);
        setMetaprojectLeader(newMp, leader);
        copyProjectCreators(oldMp, newMp);

        if (!newMp.getPreRegistration()) {
            rightService.createRelationMetaToAllUserAddRoleToAllUser(newMp, leader);
        }
        if (copyMp.isCopyProjects()) {
            copyProjects(oldMp, newMp, leader, !isTeamReg);
        }
        if (copyMp.isCopyUsers() && newMp.getPreRegistration()) {
            copyUsers(oldMp, newMp);
        }
        if (copyMp.isCopyTeams() && isTeamReg) {
            copyTeams(oldMp, newMp, copyMp.isCopyPrioritizations());
        }

        return newMp.getMetaprojectId();
    }


    private void setMetaprojectLeader(Metaproject newMp, User metaprojectCreator) {
        roleDao.addRoleMetaprojectToUser(newMp.getMetaprojectLeader(), newMp,
                DefaultRoles.METAPROJECT_LEADER);
        rightService.createRelationMetaUserAddRoleToUser(newMp, metaprojectCreator, metaprojectCreator);

        communicationCreatorService.sendCommMetaprojectLeaderAppointment(metaprojectCreator,
                newMp.getMetaprojectLeader(), newMp);
    }

    private void copyProjectCreators(Metaproject oldMp, Metaproject newMp) {
        Collection<User> projectCreators = roleDao.findMetaprojectProjectErsteller(oldMp.getMetaprojectId());
        for (User projectCreator : projectCreators) {
            roleDao.addRoleMetaprojectToUser(projectCreator, newMp,
                    DefaultRoles.METAPROJECT_PROJECT_CREATOR);
            rightService.createRelationMetaUserAddRoleToUser(newMp, projectCreator, newMp.getMetaprojectLeader());
        }
    }

    private void copyUsers(Metaproject oldMp, Metaproject newMp) {
        Collection<RelationMetaprojectUser> relationsMpUsers = relMetaUserDao.findByMetaproject(oldMp);
        for (RelationMetaprojectUser rel : relationsMpUsers) {
            User user = rel.getUserId();
            rightService.createRelationMetaUserAddRoleToUser(newMp, user, newMp.getMetaprojectLeader());
        }
    }

    private void copyProjects(Metaproject oldMp, Metaproject newMp, User projectOwner, boolean isSingleRegistration) {
        Collection<Project> projects = projectDao.findByMetaproject(oldMp.getMetaprojectId());
        for (Project oldProject : projects) {
            Project newProject = (Project) oldProject.clone();
            newProject.setMetaprojectId(newMp);
            copySpecializationProjects(oldProject, newProject);
            newProject = projectDao.save(newProject);

            roleDao.addRoleProjectToUser(projectOwner, newProject, DefaultRoles.METAPROJECT_PROJECT_OWNER);
            communicationCreatorService.sendCommProjectLeaderAppointment(projectOwner, newProject.getProjectLeader(), newProject);

            roleDao.addRoleProjectToUser(newProject.getProjectLeader(), newProject,
                    DefaultRoles.METAPROJECT_PROJECT_LEADER);
            rightService.createRelationMetaUserAddRoleToUser(newMp, newProject.getProjectLeader(), newMp.getMetaprojectLeader());

            if (isSingleRegistration) {
                createDummyTeam(newMp, newProject);
            }
        }
    }

    private void createDummyTeam(Metaproject newMp, Project project) {
        Team dummy = new Team();
        dummy.setTeamName("Dummy");
        dummy.setMetaProjectId(newMp);
        dummy.setProjectId(project);
        dummy.setTeamLeader(project.getProjectLeader());
        dummy.setUpdateStatus(UpdateStatus.TEMPORARY);
        dummy.setProjectAssignmentStatus(ProjectAssignmentStatus.FINAL);

        dummy = teamDao.save(dummy);
        roleDao.addRoleTeamToUser(dummy.getTeamLeader(), dummy, DefaultRoles.METAPROJECT_TEAM_LEADER);
        roleDao.addRoleMetaprojectToUser(dummy.getTeamLeader(), newMp, DefaultRoles.METAPROJECT_TEAM_MEMBER);
    }

    private void copyTeams(Metaproject oldMp, Metaproject newMp, boolean copyPrios) {
        Collection<Team> oldTeams = teamDao.findByMetaprojectId(oldMp.getMetaprojectId());
        for (Team oldTeam : oldTeams) {
            Team newTeam = (Team) oldTeam.clone();
            newTeam.setMetaProjectId(newMp);
            if (copyPrios) {
                newTeam.setUpdateStatus(oldTeam.getUpdateStatus());
            }
            newTeam = teamDao.save(newTeam);

            roleDao.addRoleTeamToUser(newTeam.getTeamLeader(), newTeam, DefaultRoles.METAPROJECT_TEAM_LEADER);

            Collection<RelationTeamUser> oldTeamMembers = relTeamUserDao.findTeammemberByTeam(oldTeam);  //Nur Teammember, keine eingeladenen, angefragten
            for (RelationTeamUser oldTeamMember : oldTeamMembers) {
                addUserToTeam(oldTeamMember.getUserId(), newTeam, newMp);
            }

            if (copyPrios) {
                copyPriorizations(newMp, oldTeam, newTeam);
            }
        }
    }

    private void addUserToTeam(User user, Team team, Metaproject mp) {
        RelationTeamUser relation = new RelationTeamUser();
        relation.setTeamMemberStatus(TeamMemberStatus.TEAMMEMBER);
        relation.setUserId(user);
        relation.setTeamId(team);
        relTeamUserDao.save(relation);

        roleDao.removeRoleMetaprojectFromUser(user, mp, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);
        roleDao.addRoleMetaprojectToUser(user, mp, DefaultRoles.METAPROJECT_TEAM_MEMBER);
    }

    private void copyPriorizations(Metaproject newMp, Team oldTeam, Team newTeam) {
        Collection<PrioTeamProject> oldPrios = prioTeamProjectDao.findByTeam(oldTeam.getTeamId());
        for (PrioTeamProject oldPrio : oldPrios) {
            PrioTeamProject newPrio = (PrioTeamProject) oldPrio.clone();
            newPrio.setTeamId(newTeam);

            Project newProject = projectDao.findByMetaprojectAndTitle(newMp.getMetaprojectId(), oldPrio.getProjectId().getProjectTitle());
            newPrio.setProjectId(newProject);
            prioTeamProjectDao.save(newPrio);
        }
    }

    public Long copyProject(Project oldP, Metaproject mp, User projectLeader, User currentUser, String newProjectTitle) {

        boolean isTeamRegistration = mp.getRegisterType().equals("Team");
        Project newProject = (Project) oldP.clone();
        newProject.setMetaprojectId(mp);
        copySpecializationProjects(oldP, newProject);
        newProject.setProjectLeader(projectLeader);
        newProject.setProjectTitle(newProjectTitle);
        newProject = projectDao.save(newProject);
        roleDao.addRoleProjectToUser(newProject.getProjectLeader(), newProject, DefaultRoles.METAPROJECT_PROJECT_OWNER);
        communicationCreatorService.sendCommProjectLeaderAppointment(currentUser, newProject.getProjectLeader(), newProject);
        roleDao.addRoleProjectToUser(newProject.getProjectLeader(), newProject, DefaultRoles.METAPROJECT_PROJECT_LEADER);
        rightService.createRelationMetaUserAddRoleToUser(mp, newProject.getProjectLeader(), mp.getMetaprojectLeader());

        if (!isTeamRegistration) {
            createDummyTeam(mp, newProject);
        }

        return newProject.getProjectId();
    }

    public Long moveProject(Project oldProject, Metaproject sourceMp, Metaproject destinationMp, User currentUser) {

        boolean isTeamRegistration = destinationMp.getRegisterType().equals("Team");
        Project newProject = (Project) oldProject.clone();
        newProject.setMetaprojectId(destinationMp);
        addSpecialisationForMetaproject(destinationMp, oldProject);
        copySpecializationProjects(oldProject, newProject);
        newProject = projectDao.save(newProject);

        roleDao.addRoleProjectToUser(newProject.getProjectLeader(), newProject, DefaultRoles.METAPROJECT_PROJECT_OWNER);
        communicationCreatorService.sendCommProjectLeaderAppointment(currentUser, newProject.getProjectLeader(), newProject);
        roleDao.addRoleProjectToUser(newProject.getProjectLeader(), newProject,
                DefaultRoles.METAPROJECT_PROJECT_LEADER);
        rightService.createRelationMetaUserAddRoleToUser(destinationMp, newProject.getProjectLeader(), destinationMp.getMetaprojectLeader());

        if (!isTeamRegistration) {
            createDummyTeam(destinationMp, newProject);
        }

        return newProject.getProjectId();
    }

    private void addSpecialisationForMetaproject(Metaproject mp, Project p) {
        List<Specialisation> newSpecialisations = new ArrayList<>();
        List<String> specNames = new ArrayList<>();
        for (Specialisation destinationSpecialisation : mp.getSpecialisation()) {
            specNames.add(destinationSpecialisation.getSpecialisationName());
            newSpecialisations.add(destinationSpecialisation);
        }
        for (SpecialisationProject oldSpecProject : p.getSpecialisation()) {
            if (!specNames.contains(oldSpecProject.getSpecialisation().getSpecialisationName())) {
                Specialisation newSpec = new Specialisation();
                newSpec.setSpecialisationName(oldSpecProject.getSpecialisation().getSpecialisationName());
                newSpecialisations.add(newSpec);
            }
        }
        mp.setSpecialisation(newSpecialisations);
        metaprojectDao.update(mp.getMetaprojectId(), mp);
    }

    private void copySpecializationProjects(Project oldProject, Project newProject) {
        Set<SpecialisationProject> newSpecProjects = new HashSet<>();
        for (SpecialisationProject oldSpecProject : oldProject.getSpecialisation()) {
            String specName = oldSpecProject.getSpecialisation().getSpecialisationName();
            for (Specialisation spec : newProject.getMetaproject().getSpecialisation()) {
                if (spec.getSpecialisationName().equals(specName)) {
                    SpecialisationProject newSpecProject = new SpecialisationProject();
                    newSpecProject.setSpecialisation(spec);
                    newSpecProject.setSpecialisationProportion(oldSpecProject.getSpecialisationProportion());
                    newSpecProjects.add(newSpecProject);
                }
            }
        }
        newProject.setSpecialisation(newSpecProjects);
    }
}
