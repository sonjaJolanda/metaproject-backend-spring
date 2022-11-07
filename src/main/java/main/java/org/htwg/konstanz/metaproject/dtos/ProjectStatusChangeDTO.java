package main.java.org.htwg.konstanz.metaproject.dtos;

public class ProjectStatusChangeDTO {
    private Long projectId;

    private long statusChangeId;

    private String ProjectTitle;

    private Long metaprojectId;

    private String metaprojectTitle;

    public long getStatusChangeId() {
        return statusChangeId;
    }

    public void setStatusChangeId(long statusChangeId) {
        this.statusChangeId = statusChangeId;
    }

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
