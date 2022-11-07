package main.java.org.htwg.konstanz.metaproject.communication.implementations;

import main.java.org.htwg.konstanz.metaproject.communication.CommInfoAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommType;
import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.serialization.ProjectSerializationAdapter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A subclass implementation for a metaproject leader appointment. This is only
 * an information to the new leader.
 *
 * @author SiKelle
 */
@Entity
@DiscriminatorValue("PLA")
public class CommProjectLeaderAppointment extends CommInfoAbstract {

    private static final long serialVersionUID = 1L;

    /**
     * The user, who is appointed for the metaproject leader position.
     */
    @NotNull(message = "newLeader is null")
    @ManyToOne
    private User newLeader;

    /**
     * The affected {@link Metaproject}.
     */
    @XmlJavaTypeAdapter(ProjectSerializationAdapter.class)
    @NotNull(message = "project is null")
    @ManyToOne
    private Project project;

    @Override
    public CommType getType() {
        return CommType.PROJECT_LEADER_APPOINTMENT;
    }

    public String paramNewLeader() {
        return newLeader.getFullName();
    }

    public String paramMetaprojectId() {
        return project.getMetaproject().getMetaprojectId().toString();
    }

    public String paramMetaprojectTitle() {
        return project.getMetaproject().getMetaprojectTitle();
    }

    public String paramProjectTitle() {
        return project.getProjectTitle();
    }

    public String paramProjectId() {
        return project.getProjectId().toString();
    }

    public User getNewLeader() {
        return newLeader;
    }

    public void setNewLeader(User newLeader) {
        this.newLeader = newLeader;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }


}

