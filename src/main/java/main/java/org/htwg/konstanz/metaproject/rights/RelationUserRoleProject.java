package main.java.org.htwg.konstanz.metaproject.rights;

import main.java.org.htwg.konstanz.metaproject.entities.Project;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;

/**
 * A {@link RoleProject} to {@User} mapping with {@link ElementType#PROJECT}.
 * This is a right, that is connected to a {@link Project} by id.
 * 
 * @author SiKelle
 *
 */
@Entity
@DiscriminatorValue("PROJECT")
public class RelationUserRoleProject extends RelationUserRoleAbstract {
	private static final long serialVersionUID = -8971961670150931067L;

	@NotNull(message = "connected is null")
	@ManyToOne(fetch = FetchType.LAZY)
	private Project connected;

	@OneToOne
	@NotNull(message = "role is null")
	private RoleProject role;

	public Project getConnected() {
		return connected;
	}

	@Override
	public Long getConnectedId() {
		return connected.getProjectId();
	}

	public void setConnected(Project connected) {
		this.connected = connected;
	}

	@Override
	public RoleProject getRole() {
		return role;
	}

	public void setRole(RoleProject role) {
		this.role = role;
	}

}
