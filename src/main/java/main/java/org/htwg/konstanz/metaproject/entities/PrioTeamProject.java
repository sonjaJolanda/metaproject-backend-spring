package main.java.org.htwg.konstanz.metaproject.entities;

import main.java.org.htwg.konstanz.metaproject.enums.PrioStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author FaAmbros, StChiari
 */

@Entity
@Table(name = "PrioTeamProject")
public class PrioTeamProject implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "prioTeamProject")
    @GeneratedValue
    private Long prioTeamProject;

    @NotNull(message = "priorisation is null")
    @Size(min = 1, max = 25, message = "1-25 digits and spaces")
    @Pattern(regexp = "[1-9]*", message = "type mismatch: only digits from 1 to 9")
    @Column(name = "prioritisation")
    private String prioritisation;

    @OneToOne
    @JoinColumn(name = "projectId", insertable = true, updatable = true)
    private Project projectId;

    @OneToOne
    @JoinColumn(name = "teamId", insertable = true, updatable = true)
    private Team teamId;

    @NotNull(message = "priorisation type is null")
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private PrioStatus status;

    //without id, project, team
    @Override
    public Object clone() {
        PrioTeamProject clone = new PrioTeamProject();
        clone.setPrioritisation(prioritisation);
        clone.setStatus(status);
        return clone;
    }

    public Long getPrioTeamProject() {
        return prioTeamProject;
    }

    public void setPrioTeamProject(Long prioTeamProject) {
        this.prioTeamProject = prioTeamProject;
    }

    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
    }

    public Team getTeamId() {
        return teamId;
    }

    public void setTeamId(Team teamId) {
        this.teamId = teamId;
    }

    public String getPrioritisation() {
        return prioritisation;
    }

    public void setPrioritisation(String prioritisation) {
        this.prioritisation = prioritisation;
    }

    public PrioStatus getStatus() {
        return status;
    }

    public void setStatus(PrioStatus status) {
        this.status = status;
    }

}
