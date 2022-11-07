package main.java.org.htwg.konstanz.metaproject.entities;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sonja Klein
 */

@Entity
@Table(name = "failedindigittransfer")
public class FailedINdigitTransfer {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @DateTimeFormat
    private Date firstFailedAt;

    @NonNull
    @DateTimeFormat
    private Date lastFailedAt;

    @OneToOne
    private Project project;

    @NonNull
    private String statusCode;

    private int numberOfFailedAttempts;

    private boolean isProjectTransferFailed;

    private boolean isMemberTransferFailed;

    public FailedINdigitTransfer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public boolean isProjectTransferFailed() {
        return isProjectTransferFailed;
    }

    public void setProjectTransferFailed(boolean projectTransferFailed) {
        isProjectTransferFailed = projectTransferFailed;
    }

    public boolean isMemberTransferFailed() {
        return isMemberTransferFailed;
    }

    public void setMemberTransferFailed(boolean memberTransferFailed) {
        isMemberTransferFailed = memberTransferFailed;
    }

    @NonNull
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(@NonNull String statusCode) {
        this.statusCode = statusCode;
    }

    @NonNull
    public Date getFirstFailedAt() {
        return firstFailedAt;
    }

    public void setFirstFailedAt(@NonNull Date firstFailedAt) {
        this.firstFailedAt = firstFailedAt;
    }

    @NonNull
    public Date getLastFailedAt() {
        return lastFailedAt;
    }

    public void setLastFailedAt(@NonNull Date lastFailedAt) {
        this.lastFailedAt = lastFailedAt;
    }

    public int getNumberOfFailedAttempts() {
        return numberOfFailedAttempts;
    }

    public void setNumberOfFailedAttempts(int numberOfFailedAttempts) {
        this.numberOfFailedAttempts = numberOfFailedAttempts;
    }
}
