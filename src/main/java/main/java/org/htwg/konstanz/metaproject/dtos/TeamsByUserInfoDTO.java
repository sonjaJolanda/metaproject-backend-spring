package main.java.org.htwg.konstanz.metaproject.dtos;

import com.google.common.collect.Lists;
import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.User;

import java.util.List;

public class TeamsByUserInfoDTO {

    public boolean isInvited;
    public Metaproject metaProjectId;
    public String projectAssignmentStatus;
    public Project projectId;
    public long teamId;
    public User teamLeader;
    public List<User> teamMembers = Lists.newArrayList();
    public String teamName;
    public String updateStatus;

}
