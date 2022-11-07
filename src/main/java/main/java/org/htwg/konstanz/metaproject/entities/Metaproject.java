package main.java.org.htwg.konstanz.metaproject.entities;

import com.google.common.collect.Lists;
import main.java.org.htwg.konstanz.metaproject.dtos.MetaprojectInfoDTO;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author StChiari, MaWeiss, FaAmbros, Fahocur, AlVeliu, JoFesenm
 */

@Entity
@Table(name = "Metaproject")
public class Metaproject implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "metaProjectId")
    private Long metaProjectId;

    @NotNull(message = "metaProjectTitle is null")
    @Size(min = 1, max = 50, message = "1-50 letters and spaces")
    @Column(name = "metaProjectTitle")
    private String metaProjectTitle;

    @NotNull(message = "registerType is null")
    @Column(name = "registerType")
    private String registerType;

    @OneToOne
    @JoinColumn(name = "metaProjectLeader") //, insertable = true, updatable = true)
    private User metaProjectLeader;

    // extended pattern for time. FaHocur 24.05.2015
    @NotNull(message = "projectRegStart is null")
    @Column(name = "projectRegStart")
    private String projectRegStart;

    // add new column themeRegEnd for the database and generate their
    // getter/setter. FaHocur 24.05.2015
    @NotNull(message = "projectRegEnd is null")
    @Column(name = "projectRegEnd")
    private String projectRegEnd;

    // extended pattern for time. FaHocur 24.05.2015
    @NotNull(message = "studentRegStart is null")
    @Column(name = "studentRegStart")
    private String studentRegStart;

    // add new column studentRegEnd for the database and generate their
    // getter/setter. FaHocur 24.05.2015
    @NotNull(message = "studentRegEnd is null")
    @Column(name = "studentRegEnd")
    private String studentRegEnd;

    // extended pattern for time. FaHocur 24.05.2015
    @NotNull(message = "teamRegStart is null")
    @Column(name = "teamRegStart")
    private String teamRegStart;

    // add new column teamRegEnd for the database and generate their
    // getter/setter. FaHocur 24.05.2015
    @NotNull(message = "teamRegEnd is null")
    @Column(name = "teamRegEnd")
    private String teamRegEnd;

    @NotNull(message = "deadline is null")
    @Column(name = "deadline")
    private String deadline;

    @Size(min = 1, max = 25, message = "1-25 letters and spaces")
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    @Column(name = "courseOfStudies")
    private String courseOfStudies;

    @Column(name = "semester")
    private int semester;

    // add new column teamMinSize for the database and generated their
    // getter/setter. FaHocur 24.05.2015
    @NotNull(message = "teamMinSize is null")
    @Min(value = 1, message = "teamMinSize must consist of min 1 digit")
    @Column(name = "teamMinSize")
    private int teamMinSize;

    // change column from teamSize to teamMaxSize. FaHocur 24.05.2015
    @NotNull(message = "teamMaxSize is null")
    @Min(value = 1, message = "teamMaxSize must consist of max 25 digits")
    @Column(name = "teamMaxSize")
    private int teamMaxSize;

    @Column(name = "specialisation")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Specialisation> specialisation;

    @Lob
    @Size(max = 30000, message = "1-30000 letters and spaces")
    @Column(name = "description")
    private String description;

    @Column(name = "public", columnDefinition = "bit default 1")
    private boolean visible;

    @Column(name = "preRegistration", columnDefinition = "bit default 1")
    private boolean preRegistration;

    @ManyToMany()
    @JoinTable(name = "Metaproject_ProjectcreatorUsers")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<User> projectCreatorUsers;

    @ManyToMany()
    @JoinTable(name = "Metaproject_ProjectcreatorsGroups")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<UserGroup> projectCreatorGroups;

    @XmlTransient
    public MetaprojectInfoDTO getMetaprojectInfoDto() {
        MetaprojectInfoDTO dto = new MetaprojectInfoDTO();
        dto.setMetaprojectId(this.metaProjectId);
        dto.setCourseOfStudies(this.courseOfStudies);
        dto.setMetaprojectLeader(this.metaProjectLeader);
        dto.setMetaprojectTitle(this.metaProjectTitle);
        dto.setSemester(this.semester);
        dto.setStudentRegEnd(this.studentRegEnd);
        dto.setTeamRegEnd(this.teamRegEnd);
        dto.setRegisterType(this.registerType);
        return dto;
    }

    //doesnt copy id, leader and title
    @Override
    public Object clone() {
        Metaproject clone = new Metaproject();
        clone.setRegisterType(registerType);
        clone.setDescription(description);
        clone.setStudentRegStart(studentRegStart);
        clone.setProjectRegStart(projectRegStart);
        clone.setCourseOfStudies(courseOfStudies);
        clone.setSemester(semester);
        clone.setTeamMaxSize(teamMaxSize);
        clone.setTeamMinSize(teamMinSize);
        clone.setStudentRegEnd(studentRegEnd);
        clone.setProjectRegEnd(projectRegEnd);
        clone.setTeamRegStart(teamRegStart);
        clone.setTeamRegEnd(teamRegEnd);
        clone.setDeadline(deadline);
        clone.setVisible(visible);
        clone.setPreRegistration(preRegistration);
        clone.setProjectCreatorUsers(Lists.newArrayList(projectCreatorUsers));
        clone.setProjectCreatorGroups(Lists.newArrayList(projectCreatorGroups));

        List<Specialisation> specialisationClones = new ArrayList<>();
        for (Specialisation spec : specialisation) {
            specialisationClones.add((Specialisation) spec.clone());
        }
        clone.setSpecialisation(specialisationClones);
        return clone;
    }

    public Long getMetaprojectId() {
        return metaProjectId;
    }

    public void setMetaprojectId(Long metaProjectId) {
        this.metaProjectId = metaProjectId;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetaprojectTitle() {
        return metaProjectTitle;
    }

    public void setMetaprojectTitle(String metaProjectTitle) {
        this.metaProjectTitle = metaProjectTitle;
    }

    public User getMetaprojectLeader() {
        return metaProjectLeader;
    }

    public void setMetaprojectLeader(User metaprojectLeader) {
        this.metaProjectLeader = metaprojectLeader;
    }

    public String getStudentRegStart() {
        return studentRegStart;
    }

    public void setStudentRegStart(String studentRegStart) {
        this.studentRegStart = studentRegStart;
    }

    public String getProjectRegStart() {
        return projectRegStart;
    }

    public List<Specialisation> getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(List<Specialisation> specialisation) {
        this.specialisation = specialisation;
    }

    public void setProjectRegStart(String projectRegStart) {
        this.projectRegStart = projectRegStart;
    }

    public String getCourseOfStudies() {
        return courseOfStudies;
    }

    public void setCourseOfStudies(String courseOfStudies) {
        this.courseOfStudies = courseOfStudies;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getTeamMaxSize() {
        return teamMaxSize;
    }

    public void setTeamMaxSize(int teamSize) {
        this.teamMaxSize = teamSize;
    }

    public int getTeamMinSize() {
        return teamMinSize;
    }

    public void setTeamMinSize(int teamMinSize) {
        this.teamMinSize = teamMinSize;
    }

    public String getStudentRegEnd() {
        return studentRegEnd;
    }

    public void setStudentRegEnd(String studentRegEnd) {
        this.studentRegEnd = studentRegEnd;
    }

    public String getProjectRegEnd() {
        return projectRegEnd;
    }

    public void setProjectRegEnd(String projectRegEnd) {
        this.projectRegEnd = projectRegEnd;
    }

    public String getTeamRegStart() {
        return teamRegStart;
    }

    public void setTeamRegStart(String teamReg) {
        this.teamRegStart = teamReg;
    }

    public String getTeamRegEnd() {
        return teamRegEnd;
    }

    public void setTeamRegEnd(String teamRegEnd) {
        this.teamRegEnd = teamRegEnd;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean getPreRegistration() {
        return preRegistration;
    }

    public void setPreRegistration(boolean preRegistration) {
        this.preRegistration = preRegistration;
    }

    public List<User> getProjectCreatorUsers() {
        return projectCreatorUsers;
    }

    public void setProjectCreatorUsers(List<User> projectCreatorUsers) {
        this.projectCreatorUsers = projectCreatorUsers;
    }

    public List<UserGroup> getProjectCreatorGroups() {
        return projectCreatorGroups;
    }

    public void setProjectCreatorGroups(List<UserGroup> projectCreatorGroups) {
        this.projectCreatorGroups = projectCreatorGroups;
    }
}
