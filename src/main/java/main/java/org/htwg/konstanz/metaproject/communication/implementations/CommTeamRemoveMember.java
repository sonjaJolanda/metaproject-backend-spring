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
 * A subclass implementation for add a new user to a metaproject. This is only
 * an information to the added user.
 * 
 * @author PaDrautz
 *
 */
@Entity
@DiscriminatorValue("TRM")
public class CommTeamRemoveMember extends CommInfoAbstract {

	private static final long serialVersionUID = 1L;
	/**
	 * The user, who is appointed for the member who is deleted from a
	 * team.
	 */
	@NotNull(message = "deletedUser is null")
	@ManyToOne
	private User deletedUser;

	/**
	 * The affected {@link Team}.
	 */
	@XmlJavaTypeAdapter(TeamSerializationAdapter.class)
	@NotNull(message = "team is null")
	@ManyToOne
	private Team team;

	public User getDeletedUser() {
		return deletedUser;
	}

	public void setDeletedUser(User deletedUser) {
		this.deletedUser = deletedUser;
	}

	@Override
	public CommType getType() {
		return CommType.TEAM_REMOVE_MEMBER;
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

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

}
