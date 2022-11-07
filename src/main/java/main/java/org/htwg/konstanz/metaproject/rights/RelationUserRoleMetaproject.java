package main.java.org.htwg.konstanz.metaproject.rights;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;

/**
 * A {@link RoleMetaproject} to {@User} mapping with
 * {@link ElementType#METAPROJECT}. This is a right, that is connected to a
 * {@link Metaproject} by id.
 * 
 * @author SiKelle
 *
 */
@Entity
@DiscriminatorValue("METAPROJECT")
public class RelationUserRoleMetaproject extends RelationUserRoleAbstract {
	private static final long serialVersionUID = 9023070588737883504L;

	@NotNull(message = "connected is null")
	@ManyToOne(fetch = FetchType.LAZY)
	private Metaproject connected;

	@OneToOne
	@NotNull(message = "role is null")
	private RoleMetaproject role;

	public Metaproject getConnected() {
		return connected;
	}

	@Override
	public Long getConnectedId() {
		return connected.getMetaprojectId();
	}

	public void setConnected(Metaproject connected) {
		this.connected = connected;
	}

	@Override
	public RoleMetaproject getRole() {
		return role;
	}

	public void setRole(RoleMetaproject role) {
		this.role = role;
	}

}
