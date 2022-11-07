package main.java.org.htwg.konstanz.metaproject.communication.implementations;

import main.java.org.htwg.konstanz.metaproject.communication.CommAgreeRejectAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommType;
import main.java.org.htwg.konstanz.metaproject.entities.Team;
import main.java.org.htwg.konstanz.metaproject.serialization.TeamSerializationAdapter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A subclass implementation for a team request. This communication request
 * expects an answer.
 * 
 * @author SiKelle
 *
 */
@Entity
@DiscriminatorValue("TRE")
public class CommTeamRequest extends CommAgreeRejectAbstract {

	private static final long serialVersionUID = 1L;

	/**
	 * The team which invites the new user.
	 */
	@XmlJavaTypeAdapter(TeamSerializationAdapter.class)
	@NotNull(message = "team is null")
	@ManyToOne
	private Team team;

	@Override
	public CommType getType() {
		return CommType.TEAM_REQUEST;
	}

	public String paramTeamId() {
		return team.getTeamId().toString();
	}

	public String paramTeamName() {
		return team.getTeamName();
	}

	public String paramMetaprojectId() {
		return team.getMetaProjectId().getMetaprojectId().toString();
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

}
