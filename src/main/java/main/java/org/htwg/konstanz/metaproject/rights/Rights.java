package main.java.org.htwg.konstanz.metaproject.rights;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This enumeration lists all existing rights on this application. As parameter,
 * the possible links to element types is stored.
 *
 * @author SiKelle, PaDrautz
 */
/*
 * <b>IMPORTANT NOTE:</b>
 *
 * <p> Don't rename values without validating the database. The names of these
 * enums are used to reference a right in database.
 */
public enum Rights {

    /**
     * This right allows everything and is implemented as access key to
     * everything. This right is linked with {@link RoleTypes#APP}.
     */
    SUPER_USER(RoleTypes.APP),

    /**
     * This right allows, to list all existing metaprojects. This right is
     * linked with {@link RoleTypes#APP}.
     */
    METAPROJECT_LIST(RoleTypes.APP),

    /**
     * This right allows, to create a metaproject. This right is linked with
     * {@link RoleTypes#APP}.
     */
    METAPROJECT_CREATE(RoleTypes.APP),

    /**
     * This right allows you to edit a specific or all metaprojects. This right
     * is linked with {@link RoleTypes#APP} or {@link RoleTypes#METAPROJECT}.
     */
    METAPROJECT_EDIT(RoleTypes.APP, RoleTypes.METAPROJECT),

    /**
     * This right allows you to delete a specific or all metaprojects. This
     * right is linked with {@link RoleTypes#APP} or
     * {@link RoleTypes#METAPROJECT}.
     */
    METAPROJECT_DELETE(RoleTypes.APP, RoleTypes.METAPROJECT),

    /**
     * This right allows you to View the overall conditions of a metaproject.
     * This right is linked with {@link RoleTypes#APP}
     */
    //METAPROJECT_VIEW_INFO(RoleTypes.APP),
    METAPROJECT_VIEW_INFO(RoleTypes.APP),

    /**
     * This right allows you to View the all Information regarding a
     * metaproject. This right is linked with {@link RoleTypes#APP} or
     * {@link RoleTypes#METAPROJECT}.
     */
    METAPROJECT_VIEW_DETAILS(RoleTypes.APP, RoleTypes.METAPROJECT),

    /**
     * This right allows you to sign up for allmetaprojects. This right is
     * linked with {@link RoleTypes#APP}
     */
    METAPROJECT_REGISTER(RoleTypes.APP),

    /**
     * This right allows you to sign out from an specific metaproject. This
     * right is linked with {@link RoleTypes#METAPROJECT}.
     */
    METAPROJECT_UNREGISTER(RoleTypes.METAPROJECT),

    /**
     * This right allows you to create a project in a specific metaproject. This
     * right is linked with {@link RoleTypes#METAPROJECT}.
     */
    METAPROJECT_PROJECT_CREATE(RoleTypes.METAPROJECT),

    /**
     * This right allows you to edit a project in a specific or all
     * metaprojects. This right is linked with {@link RoleTypes#PROJECT}or
     * {@link RoleTypes#METAPROJECT}.
     */
    METAPROJECT_PROJECT_EDIT(RoleTypes.PROJECT, RoleTypes.METAPROJECT),

    /**
     * This right allows you to delete a project in a specific or all
     * metaprojects.This right is linked with {@link RoleTypes#PROJECT}or
     * {@link RoleTypes#METAPROJECT}.
     */
    METAPROJECT_PROJECT_DELETE(RoleTypes.PROJECT, RoleTypes.METAPROJECT),

    /**
     * This right allows you to create a team in a specific metaproject.This
     * right is linked with {@link RoleTypes#METAPROJECT}.
     */
    METAPROJECT_TEAM_CREATE(RoleTypes.METAPROJECT),

    /**
     * This right allows you to edit a specific team in a specific metaproject
     * or to edit all teams in a specific metaproject.This right is linked with
     * {@link RoleTypes#METAPROJECT}or {@link RoleTypes#TEAM}.
     */
    METAPROJECT_TEAM_EDIT(RoleTypes.METAPROJECT, RoleTypes.TEAM),

    /**
     * This right allows you to delete specific team in a specific metaproject
     * or all teams in a specific metaproject.This right is linked with
     * {@link RoleTypes#METAPROJECT}or {@link RoleTypes#TEAM}.
     */
    METAPROJECT_TEAM_DELETE(RoleTypes.METAPROJECT, RoleTypes.TEAM),

    /**
     * This right allows you to see all members in a team.This right is linked
     * with {@link RoleTypes#METAPROJECT}
     */
    METAPROJECT_TEAM_VIEW_INFO(RoleTypes.METAPROJECT),

    /**
     * This right allows you to see all teams in a metaproject.This right is
     * linked with {@link RoleTypes#METAPROJECT}
     */
    METAPROJECT_TEAM_LIST(RoleTypes.METAPROJECT),

    /**
     * This right allows you send a request to team when currently located in a
     * specific metaproject. This right is linked with
     * {@link RoleTypes#METAPROJECT}
     */
    METAPROJECT_TEAM_REQUEST(RoleTypes.METAPROJECT),

    /**
     * This right allows you to invite users in a specific team. This right is
     * linked with {@link RoleTypes#TEAM}
     */
    METAPROJECT_TEAM_INVITE(RoleTypes.TEAM),

    /**
     * This right allows you to set the priorisation for your current team. This
     * right is linked with {@link RoleTypes#TEAM}
     */
    METAPROJECT_TEAM_SET(RoleTypes.TEAM),

    /**
     * This right allows you to send the final priorisation. This right is
     * linked with {@link RoleTypes#TEAM}
     */
    METAPROJECT_PRIO_SETFIN(RoleTypes.TEAM),

    /**
     * This right allows you to set the priorisation. This right is linked with
     * {@link RoleTypes#TEAM}
     */
    METAPROJECT_PRIO_SET(RoleTypes.TEAM),

    /**
     * This right allows you to see the taken priorisations. This right is
     * linked with {@link RoleTypes#METAPROJECT}
     */
    METAPROJECT_VIEW_PRIO(RoleTypes.METAPROJECT),

    /**
     * This right allows you to match teams to projects. This right is linked
     * with {@link RoleTypes#METAPROJECT}
     */
    METAPROJECT_PROJECT_SET_TEAM(RoleTypes.METAPROJECT),

    /**
     * This right allows you to create teams always. This right is linked
     * with {@link RoleTypes#METAPROJECT}
     */
    METAPROJECT_TEAM_CREATE_ALWAYS(RoleTypes.METAPROJECT),

    /**
     * This right allows you to set the prioritization for a team. This right is linked
     * with {@link RoleTypes#METAPROJECT}
     */
    METAPROJECT_TEAM_PRIO_SET(RoleTypes.METAPROJECT),

    /**
     * This right allows you to send the final prioritization for a team. This right is linked
     * with {@link RoleTypes#METAPROJECT}
     */
    METAPROJECT_PRIO_SET_FIN(RoleTypes.METAPROJECT),

    /**
     * This right allows you to unlock a team and set the prioritization to temporary. This right is linked
     * with {@link RoleTypes#METAPROJECT}
     */
    METAPROJECT_TEAM_UNLOCK(RoleTypes.METAPROJECT),

    /**
     * This right allows you to add members for a metaproject always. This right is linked
     * with {@link RoleTypes#METAPROJECT}
     */
    METAPROJECT_INVITE_ALWAYS(RoleTypes.METAPROJECT),

    /**
     * This right allows you to invite users in a team always. This right is linked
     * with {@link RoleTypes#METAPROJECT}
     */
    METAPROJECT_TEAM_INVITE_ALWAYS(RoleTypes.METAPROJECT),

    /**
     * This right allows you to edit your own profil. This right is linked with
     * {@link RoleTypes#USER}
     */
    USER_EDIT(RoleTypes.USER),

    /**
     * This right allows you to see all user-profiles. This right is linked with
     * {@link RoleTypes#APP}
     */
    USER_VIEW(RoleTypes.APP),

    /**
     * This right allows you to list all roles. This right is linked with
     * {@link RoleTypes#APP}
     */
    ROLES_LIST(RoleTypes.APP),

    /**
     * This right allows you to list all roles regarding to users. This right is
     * linked with {@link RoleTypes#APP}
     */
    ROLES_USER_LIST(RoleTypes.APP),

    /**
     * This right allows you to create a new role. This right is linked with
     * {@link RoleTypes#APP}
     */
    ROLES_CREATE(RoleTypes.APP),

    /**
     * This right allows you to edit a role. This right is linked with
     * {@link RoleTypes#APP}
     */
    ROLES_EDIT(RoleTypes.APP),

    /**
     * This right allows you to delete a role. This right is linked with
     * {@link RoleTypes#APP}
     */
    ROLES_DELETE(RoleTypes.APP),

    /**
     * This right allows you to assing roles. This right is linked with
     * {@link RoleTypes#APP}
     */
    ROLES_ASSIGN(RoleTypes.APP),

    /**
     * This right allows you to create a group. This right is linked with
     * {@link RoleTypes#APP}
     */
    GROUP_CREATE(RoleTypes.APP),

    /**
     * This right allows you to edit a group. This right is linked with
     * {@link RoleTypes#APP}
     */
    GROUP_EDIT(RoleTypes.APP),

    /**
     * This right allows you to delte a group. This right is linked with
     * {@link RoleTypes#APP}
     */
    GROUP_DELETE(RoleTypes.APP),

    /**
     * This right allows you to see a List of group. This right is linked with
     * {@link RoleTypes#APP}
     */
    GROUP_LIST(RoleTypes.APP),

    /**
     * This right allows you to edit configurations related to INdigit. This right is linked with
     * {@link RoleTypes#APP}
     */
    INDIGIT_CONFIGURATION(RoleTypes.APP),

    /**
     * This right allows you to see everything related to INdigit. This right is linked with
     * {@link RoleTypes#APP}
     */
    INDIGIT_VIEW(RoleTypes.APP);

    /**
     * All possible links to this right are stored here.
     */
    private Set<RoleTypes> linked;

    private Rights(RoleTypes... elementTypes) {
        this.linked = new HashSet<>(Arrays.asList(elementTypes));
    }

    public Set<RoleTypes> getLinked() {
        return linked;
    }

}
