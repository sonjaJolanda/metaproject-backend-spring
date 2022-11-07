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
 * A subclass implementation for add a new user to a metaproject. This is only an
 * information to the added user.
 * 
 * @author PaDrautz
 *
 */
@Entity
@DiscriminatorValue("TAM")
public class CommMetaprojectleaderAddTeamMember extends CommInfoAbstract {

	private static final long serialVersionUID = 1L;


	/**
	 * The affected {@link Team}.
	 */
	@XmlJavaTypeAdapter(TeamSerializationAdapter.class)
	@NotNull(message = "team is null")
	@ManyToOne
	private Team team;
	
	public Team getTeam() {
		return team;
	}
	public void setTeam(Team team) {
		this.team = team;
	}

	/**
	 * The user, who is appointed for the member who is added to the Team.
	 */
	@NotNull(message = "newMember is null")
	@ManyToOne
	private User newMember;
	
	public User getNewMember() {
		return newMember;
	}
	public void setNewMember(User newMember) {
		this.newMember = newMember;
	}

	@Override
	public CommType getType() {
		return CommType.METAPROJECT_LEADER_ADD_TEAM_MEMBER;
	}
	
	public String paramMetaprojectId() {
		return team.getMetaProjectId().toString();
	}

	public String paramMetaprojectTitle() {
		return team.getMetaProjectId().getMetaprojectTitle();
	}

	public String paramMetaprojectLeader(){
		return team.getMetaProjectId().getMetaprojectLeader().getFullName();
	}
	
	public String paramTeamName(){
		return team.getTeamName();
	}
}
