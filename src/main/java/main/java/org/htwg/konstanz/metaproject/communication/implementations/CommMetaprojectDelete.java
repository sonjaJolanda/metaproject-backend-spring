package main.java.org.htwg.konstanz.metaproject.communication.implementations;

import main.java.org.htwg.konstanz.metaproject.communication.CommInfoAbstract;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * A subclass implementation for deleting a metaproject. This is an
 * information to all project leaders and all members of the metaproject.
 *
 * @author PaDrautz
 */
@Entity
@DiscriminatorValue("MPD")
public class CommMetaprojectDelete extends CommInfoAbstract {

    private static final long serialVersionUID = 1L;

    private String metaprojectTitle;

    public String paramMetaprojectTitle() {
        return metaprojectTitle;
    }

    @Override
    public CommType getType() {
        return CommType.METAPROJECT_DELETE;
    }

    public String getMetaprojectTitle() {
        return metaprojectTitle;
    }

    public void setMetaprojectTitle(String metaprojectTitle) {
        this.metaprojectTitle = metaprojectTitle;
    }

}



