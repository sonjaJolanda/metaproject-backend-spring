package main.java.org.htwg.konstanz.metaproject.dtos;

/**
 * A team data transfer object to send only a short version of this project to
 * client and avoid too much traffic. To transform a project into this dto use a
 * xmlAdapter class.
 * 
 * @author PaDrautz
 *
 */
public class ProjectDTO {

	private Long projectId;

	private String ProjectTitle;

	private Long metaprojectId;

	private String metaprojectTitle;

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectTitle() {
		return ProjectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		ProjectTitle = projectTitle;
	}

	public Long getMetaprojectId() {
		return metaprojectId;
	}

	public void setMetaprojectId(Long metaprojectId) {
		this.metaprojectId = metaprojectId;
	}

	public String getMetaprojectTitle() {
		return metaprojectTitle;
	}

	public void setMetaprojectTitle(String metaprojectName) {
		this.metaprojectTitle = metaprojectName;
	}

}
