package main.java.org.htwg.konstanz.metaproject.rights;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * A role element model, used to persist a role in database.
 * 
 * @author SiKelle
 * @version 2.0
 */
@Entity
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "RoleAbstract")
public abstract class RoleAbstract implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "roleId")
	@GeneratedValue
	private Long roleId;

	@Lob
	@Size(min = 1, max = 500, message = "1-500 letters and spaces")
	@NotNull(message = "roleName is null")
	@Column(name = "roleName", unique = true)
	private String roleName;

	@Column(name = "created")
	private Date created = new Date();

	@Lob
	@Size(min = 1, max = 30000, message = "1-30000 letters and spaces")
	@Column(name = "roleDescription")
	private String roleDescription;

	@Column(name = "roleRights")
	@ElementCollection(targetClass = Rights.class, fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	private Collection<Rights> roleRights;

	/**
	 * This attribute describes, whether a role is a fix role, used in
	 * implementation and should not be deleted. This column is like a second
	 * key for a role. If this is null, the role is created by user interaction
	 * and on runtime. There is no setter method for this value, cause is could
	 * no be changed (only direct in database).
	 */
	@Column(name = "defaultRoleKey", unique = true, updatable = false)
	@Enumerated(EnumType.STRING)
	private DefaultRoles defaultRoleKey;
	public abstract RoleTypes getRoleType();

	/**
	 * This method validates, whether all given rights are correct assigned to
	 * this type of role. For example {@link Rights#SUPER_USER} could only be
	 * assigned to {@link RoleApp}.
	 */
	public abstract Boolean validateRights();

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public Collection<Rights> getRoleRights() {
		return roleRights;
	}

	public void setRoleRights(Collection<Rights> roleRights) {
		this.roleRights = roleRights;
	}

	public DefaultRoles getDefaultRoleKey() {
		return defaultRoleKey;
	}

}
