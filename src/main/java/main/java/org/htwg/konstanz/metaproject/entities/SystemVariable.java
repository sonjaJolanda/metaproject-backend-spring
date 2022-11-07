package main.java.org.htwg.konstanz.metaproject.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author FaAmbros, StChiari, MaWeissh, SiKelle, SuMiele
 */

@Entity
@Table(name = "SystemVariable")
public class SystemVariable implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "var_key")
    private String key;

    @Column(name = "value")
    private String value;

    @Column(name = "name")
    private String name;

    @Column(name = "required")
    private boolean required;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }


}
