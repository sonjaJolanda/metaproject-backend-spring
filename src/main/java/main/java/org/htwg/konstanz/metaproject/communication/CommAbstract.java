package main.java.org.htwg.konstanz.metaproject.communication;

import main.java.org.htwg.konstanz.metaproject.communication.values.CommReactionType;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommRead;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommStatus;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommType;
import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * This is the abstract super class for all communication objects. It is an
 * entity, persisted as single table with all sub classes in database.
 *
 * @author SiKelle
 */
@Entity
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "Communication")
public abstract class CommAbstract implements Serializable {

    private static final long serialVersionUID = -6872806145718617803L;

    /**
     * General identifier of a communication.
     */
    @Id
    @Column(name = "commId")
    @GeneratedValue
    private Long id;

    /**
     * User which is target of this communication(-request). This could be the
     * user, who is informed about something or who has to answer a question or
     * request.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull(message = "targetUser is null")
    private User targetUser;

    /**
     * The user, who triggers this communication request. That could be an user
     * who wants to inform other users, or who requests for something etc. This
     * field could be null, if this communication object is automatically
     * created by the application/system.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private User sendingUser;

    /**
     * Creation timestamp for this communication. Default value for this date is
     * the instantiation date of this object (now).
     */
    @NotNull(message = "created is null")
    @Column(name = "created")
    private Date created = new Date();

    /**
     * This status describes the whole state of a communication object. The
     * initial status should be {@link CommStatus#UNTOUCHED}. If the status is
     * {@link CommStatus#FINISHED}, the object is only stored for history.
     */
    @NotNull(message = "status is null")
    @Enumerated(EnumType.ORDINAL)
    private CommStatus status = CommStatus.UNTOUCHED;

    /**
     * Many emails, which are connected with that communication. The emails are
     * persisted with this communication but could have the state
     * {@link EmailStatus#WAITING} and {@link EmailStatus#SENT}. This variable
     * could be null, if no email should be send with this communication object.
     * The email is sent asynchronously, to avoid to many mails.
     */
    @XmlTransient
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "communication_email", joinColumns = {@JoinColumn(name = "Communication_commId")}, inverseJoinColumns = {@JoinColumn(name = "email_id")})
    private Collection<Email> email;

    /**
     * This field describes the template type of the message which should be
     * created for this communication. This template type could be used in
     * frontend to display a message or in an email to differentiate the email
     * body.
     */
    @NotNull(message = "templateType is null")
    @Column(name = "templateType")
    @Enumerated(EnumType.STRING)
    private EmailTemplateType templateType;


    @NotNull(message = "readStatus is null")
    @Enumerated(EnumType.ORDINAL)
    private CommRead readStatus = CommRead.NOT_READ;

    /**
     * Read timestamp for this communication. Default value is null.
     */
    private Date readAt = new Date();


    /**
     * This method returns the {@link CommType} of this communication object.
     * Every subclass has to implement this method and has to return the correct
     * type. It is used for serialization to show the correct type in frontend
     * and it is used to differentiate between all subclasses.
     */
    public abstract CommType getType();

    /**
     * This method returns the {@link CommReactionType} of this communication
     * object. Every subclass has to implement and choose one of these reactions
     * or answer possibilities.
     */
    public abstract CommReactionType getReactionType();

    public String paramId() {
        return id != null ? id.toString() : "";
    }

    public String paramTargetUser() {
        return targetUser.getFullName();
    }

    public String paramSendingUser() {
        return sendingUser != null ? sendingUser.getFullName() : null;
    }

    public String paramCreated() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
        return sdf.format(created);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public User getSendingUser() {
        return sendingUser;
    }

    public void setSendingUser(User sendingUser) {
        this.sendingUser = sendingUser;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public CommStatus getStatus() {
        return status;
    }

    public void setStatus(CommStatus status) {
        this.status = status;
    }

    @XmlTransient
    public Collection<Email> getEmail() {
        return email;
    }

    @XmlTransient
    public void setEmail(Collection<Email> email) {
        this.email = email;
    }

    public EmailTemplateType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(EmailTemplateType templateType) {
        this.templateType = templateType;
    }

    public Date getReadAt() {
        return readAt;
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }

    public void setReadStatus(CommRead readStatus) {
        this.readStatus = readStatus;
    }

    public CommRead getReadStatus() {
        return readStatus;
    }

}
