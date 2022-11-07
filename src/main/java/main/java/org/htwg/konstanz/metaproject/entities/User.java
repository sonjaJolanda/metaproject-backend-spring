package main.java.org.htwg.konstanz.metaproject.entities;

import main.java.org.htwg.konstanz.metaproject.serialization.AvoidSerializationAdapter;
import com.sun.istack.NotNull;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Objects;

/**
 * @author FaAmbros, StChiari, MaWeissh, SiKelle, SuMiele
 */

@Entity
@Table(name = "User")
public class User {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long userId;

    @Lob
    @Size(min = 1, max = 1000, message = "1-1000 letters and spaces")
    @Column(name = "tokenJwt")
    @XmlJavaTypeAdapter(AvoidSerializationAdapter.class)
    private String tokenJwt;

    @NotNull
    @Size(min = 1, max = 50, message = "1-50 letters and spaces")
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    @Column(name = "userName", unique = true)
    private String userName;

    @Size(min = 1, max = 50, message = "1-50 letters and spaces")
    @Column(name = "userFirstName")
    private String userFirstName;

    @Size(min = 1, max = 50, message = "1-50 letters and spaces")
    @Column(name = "userLastName")
    private String userLastName;

    @Size(min = 1, max = 50, message = "1-50 letters and spaces")
    @Column(name = "userEmail")
    private String userEmail;

    @NotNull
    @Size(min = 1, max = 100, message = "1-100 letters and spaces")
    @Column(name = "userPassword")
    @XmlJavaTypeAdapter(AvoidSerializationAdapter.class)
    private String userPassword;

    @Column(name = "matrikelNumber")
    private Integer matrikelNumber;

    @Size(max = 100, message = "1-100 letters and spaces")
    @Column(name = "courseOfStudies")
    private String courseOfStudies;

    @Column(name = "userSemesters")
    private Integer userSemesters;

    @Size(max = 50, message = "1-50 letters and spaces")
    @Column(name = "userGraduation")
    private String userGraduation;

    @Column(name = "profilePicture", columnDefinition = "LONGBLOB")
    private String profilePicture;

    private int error;

    @XmlTransient
    public String getFullName() {
        return String.format("%s %s", this.userFirstName, this.userLastName);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getError() {
        return error;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getTokenJwt() {
        return tokenJwt;
    }

    public void setTokenJwt(String tokenJwt) {
        this.tokenJwt = tokenJwt;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getMatrikelNumber() {
        return matrikelNumber;
    }

    public void setMatrikelNumber(Integer matrikelNumber) {
        this.matrikelNumber = matrikelNumber;
    }

    public String getCourseOfStudies() {
        return courseOfStudies;
    }

    public void setCourseOfStudies(String courseOfStudies) {
        this.courseOfStudies = courseOfStudies;
    }

    public Integer getUserSemesters() {
        return userSemesters;
    }

    public void setUserSemesters(Integer userSemesters) {
        this.userSemesters = userSemesters;
    }

    public String getUserGraduation() {
        return userGraduation;
    }

    public void setUserGraduation(String userGraduation) {
        this.userGraduation = userGraduation;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
