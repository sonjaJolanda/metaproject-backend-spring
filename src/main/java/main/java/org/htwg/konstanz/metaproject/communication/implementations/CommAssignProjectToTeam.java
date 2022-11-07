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
 * A subclass implementation for a project assignment to team. This is only an
 * information to the metaproject leader.
 * 
 * @author PaDrautz
 *
 */
@Entity
@DiscriminatorValue("APT")
public class CommAssignProjectToTeam extends CommInfoAbstract {

	private static final long serialVersionUID = 1L;

	/**
	 * The affected {@link Metaproject}.
	 */
	@XmlJavaTypeAdapter(MetaprojectSerializationAdapter.class)
	@NotNull(message = "metaproject is null")
	@ManyToOne
	private Metaproject metaproject;

	@Override
	public CommType getType() {
		return CommType.ASSIGN_PROJECT_TO_TEAM;
	}

	public String paramMetaprojectId() {
		return metaproject.getMetaprojectId().toString();
	}

	public String paramMetaprojectTitle() {
		return metaproject.getMetaprojectTitle();
	}

	public String paramMetaprojectLeader() {
		return metaproject.getMetaprojectLeader().getFullName();
	}

	public Metaproject getMetaproject() {
		return metaproject;
	}

	public void setMetaproject(Metaproject metaproject) {
		this.metaproject = metaproject;
	}

}
