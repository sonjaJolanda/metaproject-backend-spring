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
 * A subclass implementation for deleting a project. This is an information to
 * the project leaders and all members of the project.
 * 
 * @author PaDrautz
 *
 */
@Entity
@DiscriminatorValue("PJD")
public class CommProjectDelete extends CommInfoAbstract {

	private static final long serialVersionUID = 1L;
	
	private String projectTitle;
	
	@XmlJavaTypeAdapter(MetaprojectSerializationAdapter.class)
	@NotNull(message = "Metaproject is null")
	@ManyToOne
	private Metaproject metaproject;

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public Metaproject getMetaproject() {
		return metaproject;
	}

	public void setMetaproject(Metaproject metaproject) {
		this.metaproject = metaproject;
	}

	@Override
	public CommType getType() {
		return CommType.PROJECT_DELETE;
	}

	public String paramProjectTitle() {
		return projectTitle;
	}

	public String paramMetaprojectId() {
		return metaproject.getMetaprojectId().toString();
	}

	public String paramMetaprojectTitle() {
		return metaproject.getMetaprojectTitle();
	}

}
