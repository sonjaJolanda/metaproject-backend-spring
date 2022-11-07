package main.java.org.htwg.konstanz.metaproject.communication.implementations;

import main.java.org.htwg.konstanz.metaproject.communication.CommInfoAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommType;
import main.java.org.htwg.konstanz.metaproject.entities.Team;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.serialization.TeamSerializationAdapter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A subclass implementation for a metaproject leader appointment. This is only
 * an information to the new leader.
 * 
 * @author SiKelle
 *
 */
@Entity
@DiscriminatorValue("TLA")
public class CommTeamLeaderAppointment extends CommInfoAbstract {

	private static final long serialVersionUID = 1L;

	/**
	 * The user, who is appointed for the metaproject leader position.
	 */
	@NotNull(message = "newLeader is null")
	@ManyToOne
	private User newLeader;

	/**
	 * The affected {@link Team}.
	 */
	@XmlJavaTypeAdapter(TeamSerializationAdapter.class)
	@NotNull(message = "Team is null")
	@ManyToOne
	private Team team;

	@Override
	public CommType getType() {
		return CommType.TEAM_LEADER_APPOINTMENT;
	}

	public String paramNewLeader() {
		return newLeader.getFullName();
	}

	public String paramMetaprojectId() {
		return team.getMetaProjectId().toString();
	}

	public String paramMetaprojectTitle() {
		return team.getMetaProjectId().getMetaprojectTitle();
	}

	public String paramTeamName() {
		return team.getTeamName();
	}

	public String paramTeamId() {
		return team.getTeamId().toString();
	}

	public User getNewLeader() {
		return newLeader;
	}

	public void setNewLeader(User newLeader) {
		this.newLeader = newLeader;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

}
