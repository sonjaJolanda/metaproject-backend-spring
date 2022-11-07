package main.java.org.htwg.konstanz.metaproject.dtos;

import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.rights.RelationUserRoleAbstract;

/**
 * A {@link RelationUserRoleAbstract} data transfer object to send only a short
 * version of this {@link RelationUserRoleAbstract} to client and avoid to much
 * traffic. To transform a {@link RelationUserRoleAbstract} into this dto use a
 * xmlAdapter class or the constructor.
 * 
 * @author SiKelle
 *
 */
public class RelationUserRoleAbstractDTO {

	private Long id;

	private User user;

	private Long connectedId;

	private Long roleId;

	public RelationUserRoleAbstractDTO() {
	}

	/**
	 * This constructor creates this DTO out of an
	 * {@link RelationUserRoleAbstract} object.
	 * 
	 * @param rel
	 */
	public RelationUserRoleAbstractDTO(RelationUserRoleAbstract rel) {
		this.id = rel.getId();
		this.user = rel.getUser();
		this.connectedId = rel.getConnectedId();
		this.roleId = rel.getRole().getRoleId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getConnectedId() {
		return connectedId;
	}

	public void setConnectedId(Long connectedId) {
		this.connectedId = connectedId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

}
