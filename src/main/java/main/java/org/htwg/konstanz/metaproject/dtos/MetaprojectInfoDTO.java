package main.java.org.htwg.konstanz.metaproject.dtos;

import main.java.org.htwg.konstanz.metaproject.entities.User;

import java.util.List;

/**
 * A metaproject data transfer object to send only an info version of this
 * metaproject to client and avoid too much traffic. To transform a team into
 * this dto use a xmlAdapter class.
 *
 * @author SiKelle
 *
 */
public class MetaprojectInfoDTO {

	private Long metaprojectId;

	private String metaprojectTitle;

	private User metaProjectLeader;

	private int semester;

	private String courseOfStudies;

	private String studentRegEnd;

    private String teamRegEnd;

	private String registerType;

	//is not added in the xmlAdapter class, but only in getAllMetaprojectsOverview in the metaprojectEndpoint it is added
	private List<ProjectInfoDTO> projects;

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

	public User getMetaprojectLeader() {
		return metaProjectLeader;
	}

	public void setMetaprojectLeader(User metaProjectLeader) {
		this.metaProjectLeader = metaProjectLeader;
	}

	public int getSemester() {
		return semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public String getCourseOfStudies() {
		return courseOfStudies;
	}

	public void setCourseOfStudies(String courseOfStudies) {
		this.courseOfStudies = courseOfStudies;
	}

	public String getStudentRegEnd() {
		return studentRegEnd;
	}

	public void setStudentRegEnd(String studentRegEnd) {
		this.studentRegEnd = studentRegEnd;
	}

    public String getTeamRegEnd() {
        return teamRegEnd;
    }

    public void setTeamRegEnd(String teamRegEnd) {
        this.teamRegEnd = teamRegEnd;
    }

	public String getRegisterType() { return registerType; }

	public void setRegisterType(String registerType) { this.registerType = registerType; }

	public List<ProjectInfoDTO> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectInfoDTO> projects) {
		this.projects = projects;
	}
}
