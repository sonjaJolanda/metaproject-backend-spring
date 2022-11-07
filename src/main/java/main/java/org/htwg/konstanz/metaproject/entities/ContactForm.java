package main.java.org.htwg.konstanz.metaproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * @author PaMoeser
 */
public class ContactForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private String sender;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String subject;

    @JsonIgnore
    private String message;


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
