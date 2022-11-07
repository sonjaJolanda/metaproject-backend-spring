package main.java.org.htwg.konstanz.metaproject.dtos;

public class CopyMetaprojectDTO {

    Long metaprojectId;

    String title;

    Long leaderId;

    boolean copyProjects;

    boolean copyUsers;

    boolean copyTeams;

    boolean copyPrioritizations;

    public Long getMetaprojectId() {
        return metaprojectId;
    }

    public void setMetaprojectId(Long metaprojectId) {
        this.metaprojectId = metaprojectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Long leaderId) {
        this.leaderId = leaderId;
    }

    public boolean isCopyProjects() {
        return copyProjects;
    }

    public void setCopyProjects(boolean copyProjects) {
        this.copyProjects = copyProjects;
    }

    public boolean isCopyUsers() {
        return copyUsers;
    }

    public void setCopyUsers(boolean copyUsers) {
        this.copyUsers = copyUsers;
    }

    public boolean isCopyTeams() {
        return copyTeams;
    }

    public void setCopyTeams(boolean copyTeams) {
        this.copyTeams = copyTeams;
    }

    public boolean isCopyPrioritizations() {
        return copyPrioritizations;
    }

    public void setCopyPrioritizations(boolean copyPrioritizations) {
        this.copyPrioritizations = copyPrioritizations;
    }
}
