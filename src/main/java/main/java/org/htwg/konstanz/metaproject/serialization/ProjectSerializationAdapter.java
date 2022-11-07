package main.java.org.htwg.konstanz.metaproject.serialization;

import main.java.org.htwg.konstanz.metaproject.dtos.ProjectDTO;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import org.springframework.stereotype.Service;

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * This adapter makes a custom serialization and deserialization. It can be used
 * for a team attribute in a class by annotation.
 * 
 * Example:
 * 
 * <pre>
 * &#64;XmlJavaTypeAdapter(TeamSerializationAdapter.class)
 * private Project projectId;
 * </pre>
 * 
 * This adapter only serializes a project in a projectDto and deserializes a projectDto
 * in a new <b>empty (!)</b> project with id.
 * 
 * @author Padrautz
 *
 */
@Service
public class ProjectSerializationAdapter extends XmlAdapter<ProjectDTO, Project> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 * 
	 * This method enables deserialization to a project object with only an id.
	 */
	@Override
	public Project unmarshal(ProjectDTO projectDto) throws Exception {
		Project t = new Project();
		t.setProjectId(projectDto.getProjectId());
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 * 
	 * This method serializes a project object to only its id.
	 */
	@Override
	public ProjectDTO marshal(Project project) throws Exception {
		ProjectDTO projectDto = new ProjectDTO();
		projectDto.setProjectId(project.getProjectId());
		projectDto.setProjectTitle(project.getProjectTitle());
		if (project.getMetaproject().getMetaprojectId() != null) {
			projectDto.setMetaprojectId(project.getMetaproject().getMetaprojectId());
			projectDto.setMetaprojectTitle(project.getMetaproject().getMetaprojectTitle());
		}
		return projectDto;
	}

}

