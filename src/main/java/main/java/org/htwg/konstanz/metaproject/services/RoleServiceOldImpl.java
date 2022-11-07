package main.java.org.htwg.konstanz.metaproject.services;

import main.java.org.htwg.konstanz.metaproject.persistance.RoleDAO;
import main.java.org.htwg.konstanz.metaproject.rights.*;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceOldImpl implements RoleServiceOld {

    private final RoleDAO roleDao;

    public RoleServiceOldImpl(RoleDAO roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public int getRelatedUserToRoleByCount(Long roleId) {
        RoleAbstract role = roleDao.findById(roleId, RoleAbstract.class);
        if (role == null)
            return 0;

        RoleTypes roleType = role.getRoleType();
        int count = 0;

        if (roleType.equals(RoleTypes.APP)) {
            count = roleDao.findUsersByRoleIdCount(roleId, RelationUserRoleApp.class).intValue();
        } else if (roleType.equals(RoleTypes.METAPROJECT)) {
            count = roleDao.findUsersByRoleIdCount(roleId, RelationUserRoleMetaproject.class).intValue();
        } else if (roleType.equals(RoleTypes.PROJECT)) {
            count = roleDao.findUsersByRoleIdCount(roleId, RelationUserRoleProject.class).intValue();
        } else if (roleType.equals(RoleTypes.TEAM)) {
            count = roleDao.findUsersByRoleIdCount(roleId, RelationUserRoleTeam.class).intValue();
        } else if (roleType.equals(RoleTypes.USER)) {
            count = roleDao.findUsersByRoleIdCount(roleId, RelationUserRoleUser.class).intValue();
        }

        return count;
    }
}
