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
 * A subclass implementation for add a new user to a metaproject. This is only an
 * information to the added user.
 *
 * @author PaDrautz
 */
@Entity
@DiscriminatorValue("MAM")
public class CommMetaprojectAddMember extends CommInfoAbstract {

    private static final long serialVersionUID = 1L;

    /**
     * The affected {@link Metaproject}.
     */
    @XmlJavaTypeAdapter(MetaprojectSerializationAdapter.class)
    @NotNull(message = "metaproject is null")
    @ManyToOne
    private Metaproject metaproject;

    /**
     * The user, who is appointed for the member who is added to the Metaproject.
     */
    @NotNull(message = "newMember is null")
    @ManyToOne
    private User newMember;

    public User getNewMember() {
        return newMember;
    }


    public void setNewMember(User newMember) {
        this.newMember = newMember;
    }


    @Override
    public CommType getType() {
        return CommType.METAPROJECT_ADD_MEMBER;
    }


    public String paramMetaprojectId() {
        return metaproject.getMetaprojectId().toString();
    }


    public String paramMetaprojectTitle() {
        return metaproject.getMetaprojectTitle();
    }

    public String paramMetaprojectLeader() {
        return metaproject.getMetaprojectLeader().getFullName();
    }

    public Metaproject getMetaproject() {
        return metaproject;
    }


    public void setMetaproject(Metaproject metaproject) {
        this.metaproject = metaproject;
    }

}



