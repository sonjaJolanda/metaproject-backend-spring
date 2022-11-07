package main.java.org.htwg.konstanz.metaproject.entities;

import com.sun.istack.NotNull;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author SiKelle
 */

@Entity
@Table(name = "UserGroup")
public class UserGroup {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "groupId")
    private Long id;

    @Column(name = "groupName")
    @NotNull
    private String name;

    @ManyToMany()
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
            name = "User_UserGroup",
            joinColumns = {@JoinColumn(name = "groupId")},
            inverseJoinColumns = {@JoinColumn(name = "userId")}
    )
    private List<User> users = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usergroup_usergroup", joinColumns = {@JoinColumn(name = "parentGroups_groupId")}, inverseJoinColumns = {@JoinColumn(name = "subgroups_groupId")})
    private List<UserGroup> subgroups = new ArrayList<>();

    @ManyToMany(mappedBy = "subgroups", fetch = FetchType.EAGER)
    //@JoinTable(name = "usergroup_usergroup", joinColumns = {@JoinColumn(name = "subgroups_groupId")}, inverseJoinColumns = {@JoinColumn(name = "parentGroups_groupId")})
    private List<UserGroup> parentGroups = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<UserGroup> getSubgroups() {
        return subgroups;
    }

    public void setSubgroups(List<UserGroup> subgroups) {
        this.subgroups = subgroups;
    }

    @XmlTransient
    public List<UserGroup> getParentGroups() {
        return parentGroups;
    }

    public void setParentGroups(List<UserGroup> parentGroups) {
        this.parentGroups = parentGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserGroup userGroup = (UserGroup) o;
        return id.equals(userGroup.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
