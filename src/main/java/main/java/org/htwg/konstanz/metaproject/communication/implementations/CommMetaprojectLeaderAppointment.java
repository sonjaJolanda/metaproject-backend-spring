package main.java.org.htwg.konstanz.metaproject.communication.implementations;

import main.java.org.htwg.konstanz.metaproject.communication.CommInfoAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommType;
import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.serialization.MetaprojectSerializationAdapter;

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
@DiscriminatorValue("MLA")
public class CommMetaprojectLeaderAppointment extends CommInfoAbstract {

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
    @XmlJavaTypeAdapter(MetaprojectSerializationAdapter.class)
    @NotNull(message = "metaproject is null")
    @ManyToOne
    private Metaproject metaproject;

    @Override
    public CommType getType() {
        return CommType.METAPROJECT_LEADER_APPOINTMENT;
    }

    public String paramNewLeader() {
        return newLeader.getFullName();
    }

    public String paramMetaprojectId() {
        return metaproject.getMetaprojectId().toString();
    }

    public String paramMetaprojectTitle() {
        return metaproject.getMetaprojectTitle();
    }

    public User getNewLeader() {
        return newLeader;
    }

    public void setNewLeader(User newLeader) {
        this.newLeader = newLeader;
    }

    public Metaproject getMetaproject() {
        return metaproject;
    }

    public void setMetaproject(Metaproject metaproject) {
        this.metaproject = metaproject;
    }

}
