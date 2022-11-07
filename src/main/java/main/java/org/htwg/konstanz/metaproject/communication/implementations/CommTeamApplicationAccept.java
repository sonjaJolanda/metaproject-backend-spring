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
@DiscriminatorValue("TAA")
public class CommTeamApplicationAccept extends CommInfoAbstract {

	private static final long serialVersionUID = 1L;

	/**
	 * The affected {@link Team}.
	 */
	@XmlJavaTypeAdapter(TeamSerializationAdapter.class)
	@NotNull(message = "Team is null")
	@ManyToOne
	private Team team;
	
	/**
	 * The user, who is appointed for the member who is added to the Metaproject.
	 */
	@NotNull(message = "receivingMember is null")
	@ManyToOne
	private User receivingMember;

	public User getReceivingMember() {
		return receivingMember;
	}

	public void setReceivingMember(User receivingMember) {
		this.receivingMember = receivingMember;
	}

	@Override
	public CommType getType() {
		return CommType.TEAM_APPLICATION_WITHDRAW;
	}
	
	public String paramMetaprojectId() {
		return team.getMetaProjectId().toString();
	}

	public String paramMetaprojectTitle() {
		return team.getMetaProjectId().getMetaprojectTitle();
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public String paramTeamName(){
		return team.getTeamName();
	}

	public String paramTeamId(){
		return team.getTeamId().toString();
	}

}






