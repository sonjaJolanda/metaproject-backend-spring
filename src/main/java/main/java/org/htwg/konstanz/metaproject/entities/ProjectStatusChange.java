package main.java.org.htwg.konstanz.metaproject.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Sennur Kaya, Elisa-Lauren Bohnet
 */

@Entity
@Table(name = "projectstatuschange")
public class ProjectStatusChange implements Cloneable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "statusChangeId")
    private Long statusChangeId;

    @ManyToOne
    @JoinColumn(name = "project")
    private Project project;

    @NotNull(message = "user is null")
    @Column(name = "userName")
    private String userName;

    @NotNull(message = "lastStatus is null")
    @Column(name = "lastStatus")
    private String lastStatus;


    @NotNull(message = "timeStamp is null")
    @Column(name = "timeStamp")
    private String timeStamp;

    @Override
    public Object clone() {
        ProjectStatusChange clone = new ProjectStatusChange();
        clone.setTimeStamp(timeStamp);
        clone.setUserName(userName);
        clone.setProject(project);
        return clone;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    public Long getStatusChangeId() {
        if (statusChangeId != null)
            return statusChangeId;
        else
            return null;
    }

    public void setStatusChangeId(long statusChangeId) {
        this.statusChangeId = statusChangeId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
