package main.java.org.htwg.konstanz.metaproject.services;

import com.google.common.collect.Lists;
import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.RelationMetaprojectUser;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.entities.UserGroup;
import main.java.org.htwg.konstanz.metaproject.persistance.*;
import main.java.org.htwg.konstanz.metaproject.rights.DefaultRoles;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class GroupServiceImpl implements GroupService {

    private final UserDAO userDAO;

    private final GroupDAO groupDAO;

    private final MetaprojectDAO metaprojectDAO;

    private final RoleDAO roleDAO;

    private final RelationMetaprojectUserDAO relationMetaprojectUserDao;

    public GroupServiceImpl(UserDAO userDAO, GroupDAO groupDAO, MetaprojectDAO metaprojectDAO, RoleDAO roleDAO, RelationMetaprojectUserDAO relationMetaprojectUserDao) {
        this.userDAO = userDAO;
        this.groupDAO = groupDAO;
        this.metaprojectDAO = metaprojectDAO;
        this.roleDAO = roleDAO;
        this.relationMetaprojectUserDao = relationMetaprojectUserDao;
    }

    public UserGroup create(String name, Collection<Long> userIds, Collection<Long> subGroupIds) {
        return groupDAO.save(this.generateGroupObject(name, userIds, subGroupIds));
    }

    public UserGroup edit(Long id, String name, Collection<Long> userIds, Collection<Long> subGroupIds) {
        UserGroup newGroup = this.generateGroupObject(name, userIds, subGroupIds);
        if (hasCyclicReferences(id, newGroup)) {
            throw new IllegalArgumentException("Edited group has a cyclic reference");
        }

        UserGroup oldGroup = groupDAO.findById(id); //TODO handle null

        giveAddedUsersProjectCreatorRights(newGroup, oldGroup);
        revokeProjectCreatorRightsFromRemovedUsers(newGroup, oldGroup);


        return groupDAO.update(newGroup, id);
    }

    private void revokeProjectCreatorRightsFromRemovedUsers(UserGroup newGroup, UserGroup oldGroup) {
        Collection<User> removedUsers = new HashSet<>(oldGroup.getUsers());
        removedUsers.removeAll(newGroup.getUsers());
        if (removedUsers.size() > 0) {
            List<UserGroup> parentsAndSelf = getAllParents(oldGroup);
            parentsAndSelf.add(oldGroup);

            Collection<Metaproject> allMetaprojects = metaprojectDAO.findAll();
            for (Metaproject mp : allMetaprojects) {
                if (!Collections.disjoint(mp.getProjectCreatorGroups(), parentsAndSelf)) {
                    for (User user : removedUsers) {
                        roleDAO.removeMetaprojectProjectErsteller(mp.getMetaprojectId(), user.getUserId());
                    }
                }
            }
        }
    }

    private void giveAddedUsersProjectCreatorRights(UserGroup newGroup, UserGroup oldGroup) {
        Collection<User> addedUsers = new HashSet<>(newGroup.getUsers());
        addedUsers.removeAll(oldGroup.getUsers());
        if (addedUsers.size() > 0) {
            List<UserGroup> parentsAndSelf = getAllParents(oldGroup);
            parentsAndSelf.add(oldGroup);

            Collection<Metaproject> allMetaprojects = metaprojectDAO.findAll();
            for (Metaproject mp : allMetaprojects) {
                if (!Collections.disjoint(mp.getProjectCreatorGroups(), parentsAndSelf)) {
                    for (User user : addedUsers) {
                        System.out.println("Add user to mp: " + user.getUserName());
                        RelationMetaprojectUser existingRelation = relationMetaprojectUserDao.findByUserAndMetaproject(user, mp);
                        if (existingRelation == null) {
                            RelationMetaprojectUser rmu = new RelationMetaprojectUser();
                            rmu.setMetaprojectId(mp);
                            rmu.setUserId(user);
                            relationMetaprojectUserDao.save(rmu);
                            roleDAO.addRoleMetaprojectToUser(user, mp, DefaultRoles.METAPROJECT_MEMBER_TEAMLESS);
                            roleDAO.addRoleMetaprojectToUser(user, mp, DefaultRoles.METAPROJECT_MEMBER);
                        }
                        roleDAO.addRoleMetaprojectToUser(user, mp, DefaultRoles.METAPROJECT_PROJECT_CREATOR);
                    }
                }
            }
        }
    }

    List<UserGroup> getAllParents(UserGroup group) {
        List<UserGroup> directParents = group.getParentGroups();
        if (directParents.size() == 0) {
            return directParents;
        }

        List<UserGroup> allParents = new ArrayList<>(directParents);
        for (UserGroup parent : directParents) {
            allParents.addAll(getAllParents(parent));
        }
        return new ArrayList<>(new HashSet<>(allParents));  //this will return only distinct values
    }

    public Collection<UserGroup> findAll() {
        return groupDAO.findAll();
    }

    public UserGroup findById(Long id) {
        return groupDAO.findById(id);
    }

    public boolean delete(Long id) {
        Collection<UserGroup> allGroups = groupDAO.findAll();
        UserGroup toDelete = groupDAO.findById(id);
        if (toDelete == null) {
            return false;
        }

        Collection<Metaproject> metaprojects = metaprojectDAO.findAll();
        for (Metaproject mp : metaprojects) {
            if (mp.getProjectCreatorGroups().contains(toDelete)) {
                for (User user : toDelete.getUsers()) {
                    if (!mp.getProjectCreatorUsers().contains(user)) {
                        roleDAO.removeMetaprojectProjectErsteller(mp.getMetaprojectId(), user.getUserId());
                    }
                }
                mp.getProjectCreatorGroups().remove(toDelete);
            }
        }

        for (UserGroup group : allGroups) {
            group.getSubgroups().remove(toDelete);
        }

        groupDAO.remove(id);
        return true;
    }


    boolean hasCyclicReferences(long id, UserGroup group) {
        for (UserGroup subgroup : group.getSubgroups()) {
            if (id == subgroup.getId()) {
                return true;
            }
            return hasCyclicReferences(id, subgroup);
        }
        return false;
    }


    private UserGroup generateGroupObject(String name, Collection<Long> userIds, Collection<Long> subGroupIds) {
        Collection<User> users = new ArrayList<>();
        for (Long id : userIds) {
            users.add(userDAO.findById(id));
        }

        List<UserGroup> subGroups = new ArrayList<>();
        for (Long id : subGroupIds) {
            subGroups.add(groupDAO.findById(id));
        }

        UserGroup userGroup = new UserGroup();
        userGroup.setName(name);
        userGroup.setUsers(Lists.newArrayList(users));
        userGroup.setSubgroups(subGroups);
        return userGroup;
    }


}
