package main.java.org.htwg.konstanz.metaproject.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Gemohr
 */

@Entity
@Table(name = "ProjectFieldAccess")
public class ProjectFieldAccess implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "projectId")
    private long projectId;

    @Column(name = "field")
    private String field;

    @Column(name = "visible")
    private boolean visible;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
