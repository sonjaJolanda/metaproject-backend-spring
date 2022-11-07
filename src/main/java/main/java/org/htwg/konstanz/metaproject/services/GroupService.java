package main.java.org.htwg.konstanz.metaproject.services;

import main.java.org.htwg.konstanz.metaproject.entities.UserGroup;

import java.util.Collection;

public interface GroupService {

    UserGroup create(String name, Collection<Long> userIds, Collection<Long> subGroupIds);

    UserGroup edit(Long id, String name, Collection<Long> userIds, Collection<Long> subGroupIds);

    Collection<UserGroup> findAll();

    UserGroup findById(Long id);

    boolean delete(Long id);


}
