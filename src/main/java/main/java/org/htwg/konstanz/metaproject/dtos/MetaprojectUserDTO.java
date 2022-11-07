package main.java.org.htwg.konstanz.metaproject.dtos;

import com.google.common.collect.Lists;
import main.java.org.htwg.konstanz.metaproject.entities.User;

import java.util.List;

/**
 * A data transfer object to get relationship between metaproject and its users
 *
 * @author Maren Mutter
 */
public class MetaprojectUserDTO {

    public MetaprojectInfoDTO metaprojectId;

    public User userId;

    public List<String> roles = Lists.newArrayList();

    public long teamId;

    public String teamName;

    public long projectId;

    public String projectTitle;

    public long relationMetaprojectUserId;
}