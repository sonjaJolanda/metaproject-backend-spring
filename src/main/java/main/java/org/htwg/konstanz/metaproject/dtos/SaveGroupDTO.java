package main.java.org.htwg.konstanz.metaproject.dtos;

import java.util.Collection;

public class SaveGroupDTO {

    private Long id;

    private String name;

    private Collection<Long> selectedUsers;

    private Collection<Long> selectedSubgroups;

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

    public Collection<Long> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(Collection<Long> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }

    public Collection<Long> getSelectedSubgroups() {
        return selectedSubgroups;
    }

    public void setSelectedSubgroups(Collection<Long> selectedSubgroups) {
        this.selectedSubgroups = selectedSubgroups;
    }
}
