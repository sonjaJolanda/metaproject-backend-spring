package main.java.org.htwg.konstanz.metaproject.entities;

import main.java.org.htwg.konstanz.metaproject.enums.ProjectAssignmentStatus;
import main.java.org.htwg.konstanz.metaproject.enums.UpdateStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author StChiari, FaAmbros, MaWeissh
 */
@Entity
@Table(name = "Team")
public class Team implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "teamId")
    @GeneratedValue
    private Long teamId;

    @NotNull(message = "teamName is null")
    @Size(min = 1, max = 25, message = "1-25 letters and spaces")
    @Column(name = "teamName")
    private String teamName;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "metaProjectId", insertable = true, updatable = true)
    private Metaproject metaProjectId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "projectId", insertable = true, updatable = true)
    private Project projectId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "projectAssignmentStatus", nullable = true)
    private ProjectAssignmentStatus projectAssignmentStatus;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "updateStatus", nullable = false)
    private UpdateStatus updateStatus;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teamLeader", insertable = true, updatable = true)
    private User teamLeader;

    //without id, metaproject, projectassignementstatus, project
    @Override
    public Object clone() {
        Team clone = new Team();
        clone.setTeamName(teamName);
        clone.setTeamLeader(teamLeader);
        clone.setUpdateStatus(UpdateStatus.TEMPORARY);
        return clone;
    }

    public void setUpdateStatus(UpdateStatus updateStatus) {
        this.updateStatus = updateStatus;
    }

    public UpdateStatus getUpdateStatus() {
        return updateStatus;
    }

    public Long getTeamId() {
        return teamId;
    }


    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
    }

    public Metaproject getMetaProjectId() {
        return metaProjectId;
    }

    public void setMetaProjectId(Metaproject metaProjectId) {
        this.metaProjectId = metaProjectId;
    }


    public User getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(User teamLeader) {
        this.teamLeader = teamLeader;
    }

    public ProjectAssignmentStatus getProjectAssignmentStatus() {
        return projectAssignmentStatus;
    }

    public void setProjectAssignmentStatus(ProjectAssignmentStatus projectAssignmentStatus) {
        this.projectAssignmentStatus = projectAssignmentStatus;
    }

    @Override
    public String toString() {
        return "Team{" +
                "teamId=" + teamId +
                ", teamName='" + teamName + '\'' +
                ", metaProjectId=" + metaProjectId +
                ", projectId=" + projectId +
                ", teamLeader=" + teamLeader +
                '}';
    }
}
