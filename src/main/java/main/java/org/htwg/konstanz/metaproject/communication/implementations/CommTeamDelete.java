package main.java.org.htwg.konstanz.metaproject.communication.implementations;

import main.java.org.htwg.konstanz.metaproject.communication.CommInfoAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommType;
import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.serialization.MetaprojectSerializationAdapter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A subclass implementation for deleting a team. This is an information to
 * the team members and the metaproject leader. 
 * 
 * @author PaDrautz
 *
 */
@Entity
@DiscriminatorValue("TMD")
public class CommTeamDelete extends CommInfoAbstract {

	private static final long serialVersionUID = 1L;

	private String teamName;

	@XmlJavaTypeAdapter(MetaprojectSerializationAdapter.class)
	@NotNull(message = "metaproject is null")
	@ManyToOne
	private Metaproject metaproject;

	@Override
	public CommType getType() {
		return CommType.TEAM_DELETE;
	}

	public String paramTeamName() {
		return teamName;
	}

	public String paramMetaprojectId() {
		return metaproject.getMetaprojectId().toString();
	}

	public String paramMetaprojectTitle() {
		return metaproject.getMetaprojectTitle();
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public Metaproject getMetaproject() {
		return metaproject;
	}

	public void setMetaproject(Metaproject metaproject) {
		this.metaproject = metaproject;
	}

}

