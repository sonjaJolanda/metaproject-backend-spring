package main.java.org.htwg.konstanz.metaproject.communication.implementations;

import main.java.org.htwg.konstanz.metaproject.communication.CommInfoAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommType;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.serialization.ProjectSerializationAdapter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A subclass implementation for a Project leader change. This is only an
 * information to the new leader.
 * 
 * @author SiKelle
 *
 */
@Entity
@DiscriminatorValue("PLC")
public class CommProjectLeaderChange extends CommInfoAbstract {

	private static final long serialVersionUID = 1L;

	/**
	 * The user, who is appointed for the Project leader position.
	 */
	@NotNull(message = "newLeader is null")
	@ManyToOne
	private User newLeader;

	/**
	 * The user, who is the old leader.
	 */
	@NotNull(message = "oldLeader is null")
	@ManyToOne
	private User oldLeader;

	/**
	 * The affected {@link Project}.
	 */
	@XmlJavaTypeAdapter(ProjectSerializationAdapter.class)
	@NotNull(message = "Project is null")
	@ManyToOne
	private Project project;

	@Override
	public CommType getType() {
		return CommType.PROJECT_LEADER_CHANGE;
	}

	public String paramNewLeader() {
		return newLeader.getFullName();
	}

	public String paramOldLeader() {
		return oldLeader.getFullName();
	}

	public String paramProjectId() {
		return project.getProjectId().toString();
	}

	public String paramProjectTitle() {
		return project.getProjectTitle();
	}

	public String paramMetaprojectId() {
		return project.getMetaproject().getMetaprojectId().toString();
	}
	
	public String paramMetaprojectTitle(){
		return project.getMetaproject().getMetaprojectTitle();
	}
	
	public User getNewLeader() {
		return newLeader;
	}

	public void setNewLeader(User newLeader) {
		this.newLeader = newLeader;
	}

	public User getOldLeader() {
		return oldLeader;
	}

	public void setOldLeader(User oldLeader) {
		this.oldLeader = oldLeader;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
}
