package main.java.org.htwg.konstanz.metaproject.rights;

/**
 * These roles could be assigned to a user and element in code. All of these
 * roles are default roles and have to exist in database. You can find a role by
 * this enum value. This is a unique attribute on a role (like a second key).
 * 
 * @author SiKelle
 *
 */
public enum DefaultRoles {

	ADMIN,

	USER,

	USER_EDITOR,

	METAPROJECT_MEMBER_TEAMLESS,

	METAPROJECT_MEMBER,

	METAPROJECT_TEAM_MEMBER,

	METAPROJECT_TEAM_LEADER,

	METAPROJECT_ADMIN,

	METAPROJECT_LEADER,

	METAPROJECT_PROJECT_LEADER,

	METAPROJECT_PROJECT_CREATOR,
	
	METAPROJECT_OWNER,
	
	METAPROJECT_PROJECT_OWNER

}
