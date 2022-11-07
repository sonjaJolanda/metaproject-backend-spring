package main.java.org.htwg.konstanz.metaproject.entities;

import main.java.org.htwg.konstanz.metaproject.communication.Email;
import main.java.org.htwg.konstanz.metaproject.dtos.ProjectInfoDTO;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * @author StChiari, Fahocur, AlVeliu, JoFesenm
 */

@Entity
@Table(name = "Project")
public class Project implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "projectId")
    private Long projectId;

    @NotNull(message = "kickOffDate is null")
    @Column(name = "kickOffDate", length = 50)
    private String kickOffDate;

    @NotNull(message = "projectStatus is null")
    @Column(name = "projectStatus", length = 20)
    private String projectStatus;

    @Size(max = 50000, message = "1-50000 letters and spaces")
    @Column(name = "projectDescription", columnDefinition = "longtext")
    private String projectDescription;

    @Size(max = 50000, message = "1-50000 letters and spaces")
    @Column(name = "shortProjectDescription", columnDefinition = "longtext")
    private String shortProjectDescription;

    @Size(max = 50000, message = "1-50000 letters and spaces")
    @Column(name = "kickOffLocation", columnDefinition = "longtext")
    private String kickOffLocation;

    @NotNull(message = "specialisation is null")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "project_specialisationproject", joinColumns = {@JoinColumn(name = "Project_projectId")}, inverseJoinColumns = {@JoinColumn(name = "specialisation_specialisationId")})
    private Set<SpecialisationProject> specialisation;

    @Column(name = "sollProject")
    private boolean sollProject;

    @NotNull(message = "isAssigned is null")
    @Column(name = "isAssigned")
    private boolean isAssigned;

    @NotNull(message = "projectTitle is null")
    @Size(min = 1, max = 100, message = "1-100 letters and spaces")
    @Column(name = "projectTitle", length = 100)
    private String projectTitle;

    @ManyToOne
    @JoinColumn(name = "metaProjectId")
    private Metaproject metaproject;

    @OneToOne
    @JoinColumn(name = "projectLeader")
    private User projectLeader;

    @NotNull(message = "minAmountMember is null")
    @Min(value = 1, message = "minAmountMember must consist of min 1 digit")
    @Column(name = "minAmountMember", columnDefinition = "INT(11)")
    private int minAmountMember;

    @NotNull(message = "maxAmountMember is null")
    @Min(value = 1, message = "maxAmountMember must consist of max 25 digits")
    @Column(name = "maxAmountMember", columnDefinition = "INT(11)")
    private int maxAmountMember;

    @Column(name = "endDate", length = 50)
    private String endDate;

    @NotNull(message = "lastUser is null")
    @Column(name = "lastUser", length = 100)
    private String lastUser;

    @NotNull(message = "timeStamp is null")
    @Column(name = "timeStamp", length = 100)
    private String timeStamp;

    @NotNull(message = "transferUser is null")
    @Column(name = "transferUser", length = 50)
    private String transferUser;

    @NotNull(message = "amountOfTeammembers is null")
    @Column(name = "amountOfTeammembers", columnDefinition = "INT(20)")
    private int amountOfTeammembers;

    @NotNull(message = "statusCode is null")
    @Column(name = "statusCode", columnDefinition = "BIGINT(20)")
    private int statusCode;

    @XmlTransient
    public ProjectInfoDTO getProjectInfoDto() {
        ProjectInfoDTO dto = new ProjectInfoDTO();
        dto.setProjectId(this.projectId);
        dto.setProjectTitle(this.projectTitle);
        dto.setProjectStatus(this.projectStatus);
        dto.setMetaprojectId(this.metaproject.getMetaprojectId());
        dto.setMetaprojectTitle(this.metaproject.getMetaprojectTitle());
        dto.setKickOffDate(this.kickOffDate);
        dto.setEndDate(this.endDate);
        return dto;
    }

    //without id, metaproject, assigned, specialisation
    @Override
    public Object clone() {
        Project clone = new Project();
        clone.setProjectTitle(projectTitle);
        clone.setKickOffDate(kickOffDate);
        clone.setProjectLeader(projectLeader);
        clone.setSollProject(sollProject);
        clone.setProjectDescription(projectDescription);
        clone.setShortProjectDescription(shortProjectDescription);
        clone.setKickOffLocation(kickOffLocation);
        clone.setProjectStatus(projectStatus);
        clone.setMinAmountMember(minAmountMember);
        clone.setMaxAmountMember(maxAmountMember);
        clone.setEndDate(endDate);
        clone.setAssigned(false);
        clone.setLastUser(lastUser);
        clone.setTimeStamp(timeStamp);
        clone.setTransferUser(transferUser);
        clone.setAmountOfTeammembers(amountOfTeammembers);

        return clone;
    }

    public int getAmountOfTeammembers() {
        return amountOfTeammembers;
    }

    public void setAmountOfTeammembers(int amountOfTeammembers) {
        this.amountOfTeammembers = amountOfTeammembers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getTransferUser() {
        return transferUser;
    }

    public void setTransferUser(String transferUser) {
        this.transferUser = transferUser;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLastUser() {
        return lastUser;
    }

    public void setLastUser(String lastUser) {
        this.lastUser = lastUser;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Metaproject getMetaproject() {
        return this.metaproject;
    }

    public void setMetaprojectId(Metaproject metaproject) {
        this.metaproject = metaproject;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getKickOffDate() {
        return kickOffDate;
    }

    public void setKickOffDate(String kickOffDate) {
        this.kickOffDate = kickOffDate;
    }

    public User getProjectLeader() {
        return projectLeader;
    }

    public void setProjectLeader(User projectLeader) {
        this.projectLeader = projectLeader;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getShortProjectDescription() {
        return shortProjectDescription;
    }

    public void setShortProjectDescription(String shortProjectDescription) {
        this.shortProjectDescription = shortProjectDescription;
    }

    public String getKickOffLocation() {
        return kickOffLocation;
    }

    public void setKickOffLocation(String kickOffLocation) {
        this.kickOffLocation = kickOffLocation;
    }

    public boolean isSollProject() {
        return sollProject;
    }

    public void setSollProject(boolean sollProject) {
        this.sollProject = sollProject;
    }

    public boolean getIsAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }

    public Set<SpecialisationProject> getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(Set<SpecialisationProject> specialisation) {
        this.specialisation = specialisation;
    }

    public int getMinAmountMember() {
        return minAmountMember;
    }

    public void setMinAmountMember(int minAmountMember) {
        this.minAmountMember = minAmountMember;
    }

    public int getMaxAmountMember() {
        return maxAmountMember;
    }

    public void setMaxAmountMember(int maxAmountMember) {
        this.maxAmountMember = maxAmountMember;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectId=" + projectId +
                ", kickOffDate='" + kickOffDate + '\'' +
                ", projectDescription='" + projectDescription + '\'' +
                ", shortProjectDescription='" + shortProjectDescription + '\'' +
                ", kickOffLocation='" + kickOffLocation + '\'' +
                ", specialisation=" + specialisation +
                ", sollProject=" + sollProject +
                ", isAssigned=" + isAssigned +
                ", projectTitle='" + projectTitle + '\'' +
                ", metaproject=" + metaproject +
                ", projectLeader=" + projectLeader +
                ", minAmountMember=" + minAmountMember +
                ", maxAmountMember=" + maxAmountMember +
                ", endDate=" + endDate +
                ", projectStatus=" + projectStatus +
                ", lastUser=" + lastUser +
                ", timeStamp=" + timeStamp +
                ", transferUser=" + transferUser +
                ", amountOfTeammembers=" + amountOfTeammembers +
                '}';
    }

}
