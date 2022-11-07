package main.java.org.htwg.konstanz.metaproject.dtos;

public class CopyProjectDTO {

    String newProjectTitle;
    Long projectLeaderId;

    public String getNewProjectTitle() {
        return newProjectTitle;
    }

    public void setNewProjectTitle(String newProjectTitle) {
        this.newProjectTitle = newProjectTitle;
    }

    public Long getProjectLeaderId() {
        return projectLeaderId;
    }

    public void setProjectLeaderId(Long projectLeaderId) {
        this.projectLeaderId = projectLeaderId;
    }

}
