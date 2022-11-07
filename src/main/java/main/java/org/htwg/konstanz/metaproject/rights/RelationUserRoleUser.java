package main.java.org.htwg.konstanz.metaproject.rights;

import main.java.org.htwg.konstanz.metaproject.entities.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;

/**
 * A {@link RoleUser} to {@User} mapping with {@link ElementType#USER}. This is
 * a right, that is connected to a {@link User} by id.
 * 
 * @author SiKelle
 *
 */
@Entity
@DiscriminatorValue("USER")
public class RelationUserRoleUser extends RelationUserRoleAbstract {
	private static final long serialVersionUID = -4160910082305617300L;

	@NotNull(message = "connected is null")
	@ManyToOne(fetch = FetchType.LAZY)
	private User connected;

	@OneToOne
	@NotNull(message = "role is null")
	private RoleUser role;

	public User getConnected() {
		return connected;
	}

	@Override
	public Long getConnectedId() {
		return connected.getUserId();
	}

	public void setConnected(User connected) {
		this.connected = connected;
	}

	@Override
	public RoleUser getRole() {
		return role;
	}

	public void setRole(RoleUser role) {
		this.role = role;
	}

}
