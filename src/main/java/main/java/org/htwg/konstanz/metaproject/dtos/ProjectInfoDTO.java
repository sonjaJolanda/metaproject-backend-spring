package main.java.org.htwg.konstanz.metaproject.dtos;

/**
 * A project info data transfer object to send only a short version of this project to
 * client and avoid too much traffic. To transform a project into this dto use a
 * xmlAdapter class.
 *
 * @author Sonja Klein
 */
public class ProjectInfoDTO {

    private Long projectId;

    private String projectTitle;

    private Long metaprojectId;

    private String metaprojectTitle;

    private String projectStatus;

    private String kickOffDate;

    private String endDate;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
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

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getKickOffDate() {
        return kickOffDate;
    }

    public void setKickOffDate(String kickOffDate) {
        this.kickOffDate = kickOffDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
