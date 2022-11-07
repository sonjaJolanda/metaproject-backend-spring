package main.java.org.htwg.konstanz.metaproject.rights;

import main.java.org.htwg.konstanz.metaproject.entities.Team;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;

/**
 * A {@link RoleTeam} to {@User} mapping with {@link ElementType#TEAM}. This is
 * a right, that is connected to a {@link Team} by id.
 * 
 * @author SiKelle
 *
 */
@Entity
@DiscriminatorValue("TEAM")
public class RelationUserRoleTeam extends RelationUserRoleAbstract {
	private static final long serialVersionUID = -6006998225107462772L;

	@NotNull(message = "connected is null")
	@ManyToOne(fetch = FetchType.LAZY)
	private Team connected;

	@OneToOne
	@NotNull(message = "role is null")
	private RoleTeam role;

	public Team getConnected() {
		return connected;
	}

	@Override
	public Long getConnectedId() {
		return connected.getTeamId();
	}

	public void setConnected(Team connected) {
		this.connected = connected;
	}

	@Override
	public RoleTeam getRole() {
		return role;
	}

	public void setRole(RoleTeam role) {
		this.role = role;
	}

}
