package main.java.org.htwg.konstanz.metaproject.rights;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;

/**
 * A {@link RoleApp} to {@User} mapping with {@link ElementType#APP}. This is a
 * global right.
 * 
 * @author SiKelle
 *
 */
@Entity
@DiscriminatorValue("APP")
public class RelationUserRoleApp extends RelationUserRoleAbstract {
	private static final long serialVersionUID = 4467126990128936655L;

	@OneToOne
	@NotNull(message = "role is null")
	private RoleApp role;

	@Override
	public RoleApp getRole() {
		return role;
	}

	@Override
	public Long getConnectedId() {
		return null;
	}

	public void setRole(RoleApp role) {
		this.role = role;
	}

}
