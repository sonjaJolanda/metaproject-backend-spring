package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.Team;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.repositories.RelationUserRoleAbtractRepository;
import main.java.org.htwg.konstanz.metaproject.repositories.RoleAbstractRepository;
import main.java.org.htwg.konstanz.metaproject.rights.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Data access object for roles.
 *
 * @author SiKelle
 */
@Service
public class RoleDAOImpl implements RoleDAO {

    private static final Logger log = LoggerFactory.getLogger(RoleDAOImpl.class);

    private final UserDAO userDAO;

    private final RoleAbstractRepository roleAbstractRepo;

    private final RelationUserRoleAbtractRepository relationUserRoleAbtractRepo;

    @Autowired
    private EntityManager em;

    public RoleDAOImpl(UserDAO userDAO, RoleAbstractRepository roleAbstractRepo, RelationUserRoleAbtractRepository relationUserRoleAbtractRepo) {
        this.userDAO = userDAO;
        this.roleAbstractRepo = roleAbstractRepo;
        this.relationUserRoleAbtractRepo = relationUserRoleAbtractRepo;
    }

    @Override
    public <T extends RoleAbstract> T save(T role, Class<T> type) throws IllegalArgumentException {
        if (role == null || !role.validateRights()) {
            throw new IllegalArgumentException("This is not a valid role!");
        }
        return roleAbstractRepo.save(role);
    }

    @Override
    public <T extends RoleAbstract> T update(T transientRole, Long id, Class<T> type) {
        if (transientRole == null || !transientRole.validateRights()) {
            throw new IllegalArgumentException("This is not a valid role!");
        }
        T role = findById(id, type);
        if (role == null)
            return null;

        transientRole.setRoleId(id);
        roleAbstractRepo.save(transientRole);
        return transientRole;
    }

    @Override
    public <T extends RoleAbstract> T findById(Long id, Class<T> type) {
        try {
            return em.find(type, id);
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T extends RelationUserRoleAbstract> Collection<T> findByUser(User user, Class<T> type) {
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
            Root<T> rootRole = criteriaQuery.from(type);
            TypedQuery<T> allQuery = em.createQuery(
                    criteriaQuery.select(rootRole).where(criteriaBuilder.equal(rootRole.get("user"), user)));
            return allQuery.getResultList();
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T extends RoleAbstract> T findDefaultByKey(DefaultRoles defaultRole, Class<T> type) {
        Optional<RoleAbstract> roleAbstract = roleAbstractRepo.findRoleAbstractByDefaultRoleKey(defaultRole);
        return (T) roleAbstract.orElse(null);
    }

    @Override
    public <T extends RoleAbstract> T remove(Long id, Class<T> type) {
        T role = findById(id, type);
        if (role == null)
            return null;
        if (role.getDefaultRoleKey() != null) {
            // this role is a default role
            log.error("This role {} is a default role and could not be deleted.", role.getRoleName());
            return null;
        }
        roleAbstractRepo.delete(role);
        return role;
    }

    @Override
    public <T extends RoleAbstract> Collection<T> findAll(Class<T> type) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
        Root<T> rootRole = criteriaQuery.from(type);
        CriteriaQuery<T> all = criteriaQuery.select(rootRole);
        TypedQuery<T> allQuery = em.createQuery(all);
        return allQuery.getResultList();
    }

    @Override
    public Collection<RoleAbstract> findAll() {
        return findAll(RoleAbstract.class);
    }

    @Override
    public RelationUserRoleApp addRoleAppToUser(User user, DefaultRoles role) {
        RoleApp roleId = findDefaultByKey(role, RoleApp.class);
        return addRoleAppToUser(user, roleId.getRoleId());
    }

    @Override
    public RelationUserRoleMetaproject addRoleMetaprojectToUser(User user, Metaproject metaproject, DefaultRoles role) {
        RoleMetaproject roleId = findDefaultByKey(role, RoleMetaproject.class);
        return addRoleMetaprojectToUser(user, metaproject, roleId.getRoleId());
    }

    @Override
    public RelationUserRoleProject addRoleProjectToUser(User user, Project project, DefaultRoles role) {
        RoleProject roleId = findDefaultByKey(role, RoleProject.class);
        return addRoleProjectToUser(user, project, roleId.getRoleId());
    }

    @Override
    public RelationUserRoleTeam addRoleTeamToUser(User user, Team team, DefaultRoles role) {
        RoleTeam roleId = findDefaultByKey(role, RoleTeam.class);
        return addRoleTeamToUser(user, team, roleId.getRoleId());
    }

    @Override
    public RelationUserRoleUser addRoleUserToUser(User user, User elementUser, DefaultRoles role) {
        RoleUser roleId = findDefaultByKey(role, RoleUser.class);
        return addRoleUserToUser(user, elementUser, roleId.getRoleId());
    }

    @Override
    public RelationUserRoleApp addRoleAppToUser(User user, Long id) {
        RoleApp role = findById(id, RoleApp.class);
        if (role == null)
            return null;

        RelationUserRoleApp relation = findRelationUserRoleApp(user, role);
        if (relation != null)
            return null; // user already has role

        // create new relation
        RelationUserRoleApp newRelation = new RelationUserRoleApp();
        newRelation.setRole(role);
        newRelation.setUser(user);
        relationUserRoleAbtractRepo.save(newRelation);
        return newRelation;
    }

    @Override
    public RelationUserRoleMetaproject addRoleMetaprojectToUser(User user, Metaproject metaproject, Long id) {
        RoleMetaproject role = findById(id, RoleMetaproject.class);
        if (role == null)
            return null;

        RelationUserRoleMetaproject relation = findRelationUserRoleMetaproject(user, role, metaproject);
        if (relation != null)
            return relation;// user already has role

        // create new relation
        RelationUserRoleMetaproject newRelation = new RelationUserRoleMetaproject();
        newRelation.setRole(role);
        newRelation.setUser(user);
        newRelation.setConnected(metaproject);

        relationUserRoleAbtractRepo.save(newRelation);
        return newRelation;
    }

    @Override
    public RelationUserRoleProject addRoleProjectToUser(User user, Project project, Long id) {
        RoleProject role = findById(id, RoleProject.class);
        if (role == null)
            return null;

        RelationUserRoleProject relation = findRelationUserRoleProject(user, role, project);
        if (relation != null)
            return relation; // user already has role

        // create new relation
        RelationUserRoleProject newRelation = new RelationUserRoleProject();
        newRelation.setRole(role);
        newRelation.setUser(user);
        newRelation.setConnected(project);
        relationUserRoleAbtractRepo.save(newRelation);
        return newRelation;
    }

    @Override
    public RelationUserRoleTeam addRoleTeamToUser(User user, Team team, Long id) {
        RoleTeam role = findById(id, RoleTeam.class);
        if (role == null)
            return null;

        RelationUserRoleTeam relation = findRelationUserRoleTeam(user, role, team);
        if (relation != null)
            return relation; // user already has role

        // create new relation
        RelationUserRoleTeam newRelation = new RelationUserRoleTeam();
        newRelation.setRole(role);
        newRelation.setUser(user);
        newRelation.setConnected(team);
        relationUserRoleAbtractRepo.save(newRelation);
        return newRelation;
    }

    @Override
    public RelationUserRoleUser addRoleUserToUser(User user, User elementUser, Long id) {
        RoleUser role = findById(id, RoleUser.class);
        if (role == null)
            return null;

        RelationUserRoleUser relation = findRelationUserRoleUser(user, role, elementUser);
        if (relation != null)
            return relation; // user already has role

        // create new relation
        RelationUserRoleUser newRelation = new RelationUserRoleUser();
        newRelation.setRole(role);
        newRelation.setUser(user);
        newRelation.setConnected(elementUser);
        relationUserRoleAbtractRepo.save(newRelation);
        return newRelation;
    }

    @Override
    public RelationUserRoleApp findRelationUserRoleApp(User user, RoleApp role) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<RelationUserRoleApp> criteriaQuery = criteriaBuilder.createQuery(RelationUserRoleApp.class);
        Root<RelationUserRoleApp> rootRole = criteriaQuery.from(RelationUserRoleApp.class);
        TypedQuery<RelationUserRoleApp> allQuery = em.createQuery(criteriaQuery.select(rootRole)
                .where(criteriaBuilder.and(criteriaBuilder.equal(rootRole.get("user"), user),
                        criteriaBuilder.equal(rootRole.get("role"), role))));
        try {
            return allQuery.getSingleResult();
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RelationUserRoleMetaproject findRelationUserRoleMetaproject(User user, RoleMetaproject role,
                                                                       Metaproject metaproject) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<RelationUserRoleMetaproject> criteriaQuery = criteriaBuilder
                .createQuery(RelationUserRoleMetaproject.class);
        Root<RelationUserRoleMetaproject> rootRole = criteriaQuery.from(RelationUserRoleMetaproject.class);
        TypedQuery<RelationUserRoleMetaproject> allQuery = em.createQuery(criteriaQuery.select(rootRole)
                .where(criteriaBuilder.and(criteriaBuilder.equal(rootRole.get("user"), user),
                        criteriaBuilder.equal(rootRole.get("role"), role),
                        criteriaBuilder.equal(rootRole.get("connected"), metaproject))));
        try {
            return allQuery.getSingleResult();
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RelationUserRoleAbstract findRelationUserRole(User user, RoleAbstract role) {
        List<RelationUserRoleAbstract> relations = relationUserRoleAbtractRepo.findRelationUserRoleAbstractByUser(user);
        relations = relations.stream().filter(relation -> relation.getRole().equals(role)).collect(Collectors.toList());
        return relations.isEmpty() ? null : relations.get(0);
    }

    @Override
    public RelationUserRoleProject findRelationUserRoleProject(User user, RoleProject role, Project project) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<RelationUserRoleProject> criteriaQuery = criteriaBuilder
                .createQuery(RelationUserRoleProject.class);
        Root<RelationUserRoleProject> rootRole = criteriaQuery.from(RelationUserRoleProject.class);
        TypedQuery<RelationUserRoleProject> allQuery = em.createQuery(criteriaQuery.select(rootRole)
                .where(criteriaBuilder.and(criteriaBuilder.equal(rootRole.get("user"), user),
                        criteriaBuilder.equal(rootRole.get("role"), role),
                        criteriaBuilder.equal(rootRole.get("connected"), project))));
        try {
            return allQuery.getSingleResult();
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RelationUserRoleTeam findRelationUserRoleTeam(User user, RoleTeam role, Team team) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<RelationUserRoleTeam> criteriaQuery = criteriaBuilder.createQuery(RelationUserRoleTeam.class);
        Root<RelationUserRoleTeam> rootRole = criteriaQuery.from(RelationUserRoleTeam.class);
        TypedQuery<RelationUserRoleTeam> allQuery = em.createQuery(criteriaQuery.select(rootRole)
                .where(criteriaBuilder.and(criteriaBuilder.equal(rootRole.get("user"), user),
                        criteriaBuilder.equal(rootRole.get("role"), role),
                        criteriaBuilder.equal(rootRole.get("connected"), team))));
        try {
            return allQuery.getSingleResult();
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RelationUserRoleUser findRelationUserRoleUser(User user, RoleUser role, User elementUser) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<RelationUserRoleUser> criteriaQuery = criteriaBuilder.createQuery(RelationUserRoleUser.class);
        Root<RelationUserRoleUser> rootRole = criteriaQuery.from(RelationUserRoleUser.class);
        TypedQuery<RelationUserRoleUser> allQuery = em.createQuery(criteriaQuery.select(rootRole)
                .where(criteriaBuilder.and(criteriaBuilder.equal(rootRole.get("user"), user),
                        criteriaBuilder.equal(rootRole.get("role"), role),
                        criteriaBuilder.equal(rootRole.get("connected"), elementUser))));
        try {
            return allQuery.getSingleResult();
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RelationUserRoleApp removeRoleAppFromUser(User user, Long id) {
        RoleApp role = findById(id, RoleApp.class);
        if (role == null)
            return null;

        RelationUserRoleApp rel = findRelationUserRoleApp(user, role);
        if (rel == null)
            return null;

        relationUserRoleAbtractRepo.delete(rel);
        return rel;
    }

    @Override
    public RelationUserRoleMetaproject removeRoleMetaprojectFromUser(User user, Metaproject metaproject, Long id) {
        RoleMetaproject role = findById(id, RoleMetaproject.class);
        if (role == null)
            return null;

        RelationUserRoleMetaproject rel = findRelationUserRoleMetaproject(user, role, metaproject);
        if (rel == null)
            return null;

        relationUserRoleAbtractRepo.delete(rel);
        return rel;
    }

    @Override
    public RelationUserRoleProject removeRoleProjectFromUser(User user, Project project, Long id) {
        RoleProject role = findById(id, RoleProject.class);
        if (role == null)
            return null;

        RelationUserRoleProject rel = findRelationUserRoleProject(user, role, project);
        if (rel == null)
            return null;

        relationUserRoleAbtractRepo.delete(rel);
        return rel;
    }

    @Override
    public RelationUserRoleTeam removeRoleTeamFromUser(User user, Team team, Long id) {
        RoleTeam role = findById(id, RoleTeam.class);
        if (role == null)
            return null;

        RelationUserRoleTeam rel = findRelationUserRoleTeam(user, role, team);
        if (rel == null)
            return null;

        relationUserRoleAbtractRepo.delete(rel);
        return rel;
    }

    @Override
    public RelationUserRoleUser removeRoleUserFromUser(User user, User elementUser, Long id) {
        RoleUser role = findById(id, RoleUser.class);
        if (role == null)
            return null;

        RelationUserRoleUser rel = findRelationUserRoleUser(user, role, elementUser);
        relationUserRoleAbtractRepo.delete(rel);
        return rel;
    }

    @Override
    public Collection<RelationUserRoleMetaproject> removeRolesMetaproject(Metaproject metaproject) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<RelationUserRoleMetaproject> criteriaQuery = criteriaBuilder
                .createQuery(RelationUserRoleMetaproject.class);
        Root<RelationUserRoleMetaproject> rootRole = criteriaQuery.from(RelationUserRoleMetaproject.class);
        TypedQuery<RelationUserRoleMetaproject> allQuery = em.createQuery(
                criteriaQuery.select(rootRole).where(criteriaBuilder.equal(rootRole.get("connected"), metaproject)));
        try {
            Collection<RelationUserRoleMetaproject> rels = allQuery.getResultList();
            for (RelationUserRoleMetaproject rel : rels) {
                relationUserRoleAbtractRepo.delete(rel);
            }
            return rels;
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Collection<RelationUserRoleProject> removesRoleProject(Project project) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<RelationUserRoleProject> criteriaQuery = criteriaBuilder
                .createQuery(RelationUserRoleProject.class);
        Root<RelationUserRoleProject> rootRole = criteriaQuery.from(RelationUserRoleProject.class);
        TypedQuery<RelationUserRoleProject> allQuery = em.createQuery(
                criteriaQuery.select(rootRole).where(criteriaBuilder.equal(rootRole.get("connected"), project)));
        try {
            Collection<RelationUserRoleProject> rels = allQuery.getResultList();
            for (RelationUserRoleProject rel : rels) {
                relationUserRoleAbtractRepo.delete(rel);
            }
            return rels;
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Collection<RelationUserRoleTeam> removeRolesTeam(Team team) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<RelationUserRoleTeam> criteriaQuery = criteriaBuilder.createQuery(RelationUserRoleTeam.class);
        Root<RelationUserRoleTeam> rootRole = criteriaQuery.from(RelationUserRoleTeam.class);
        TypedQuery<RelationUserRoleTeam> allQuery = em.createQuery(
                criteriaQuery.select(rootRole).where(criteriaBuilder.equal(rootRole.get("connected"), team)));
        try {
            Collection<RelationUserRoleTeam> rels = allQuery.getResultList();
            for (RelationUserRoleTeam rel : rels) {
                relationUserRoleAbtractRepo.delete(rel);
            }
            return rels;
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RelationUserRoleApp removeRoleAppFromUser(User user, DefaultRoles role) {
        RoleApp roleId = findDefaultByKey(role, RoleApp.class);
        return removeRoleAppFromUser(user, roleId.getRoleId());
    }

    @Override
    public RelationUserRoleMetaproject removeRoleMetaprojectFromUser(User user, Metaproject metaproject, DefaultRoles role) {
        RoleMetaproject roleId = findDefaultByKey(role, RoleMetaproject.class);
        return removeRoleMetaprojectFromUser(user, metaproject, roleId.getRoleId());
    }

    @Override
    public RelationUserRoleProject removeRoleProjectFromUser(User user, Project project, DefaultRoles role) {
        RoleProject roleId = findDefaultByKey(role, RoleProject.class);
        return removeRoleProjectFromUser(user, project, roleId.getRoleId());
    }

    @Override
    public RelationUserRoleTeam removeRoleTeamFromUser(User user, Team team, DefaultRoles role) {
        RoleTeam roleId = findDefaultByKey(role, RoleTeam.class);
        return removeRoleTeamFromUser(user, team, roleId.getRoleId());
    }

    @Override
    public RelationUserRoleUser removeRoleUserFromUser(User user, User elementUser, DefaultRoles role) {
        RoleUser roleId = findDefaultByKey(role, RoleUser.class);
        return removeRoleUserFromUser(user, elementUser, roleId.getRoleId());
    }

    @Override
    public <T extends RelationUserRoleAbstract> Collection<T> findUsersByRoleId(Long roleId, Class<T> type) {
        RoleAbstract role = findById(roleId, RoleAbstract.class);
        if (role == null) {
            return null;
        }
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
            Root<T> rootRole = criteriaQuery.from(type);
            TypedQuery<T> allQuery = em.createQuery(
                    criteriaQuery.select(rootRole).where(criteriaBuilder.equal(rootRole.get("role"), role)));
            return allQuery.getResultList();
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T extends RelationUserRoleAbstract> Long findUsersByRoleIdCount(Long roleId, Class<T> type) {
        RoleAbstract role = findById(roleId, RoleAbstract.class);
        if (role == null) {
            return null;
        }
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<T> rootRole = criteriaQuery.from(type);
            TypedQuery<Long> allQuery = em.createQuery(criteriaQuery.select(criteriaBuilder.count(rootRole))
                    .where(criteriaBuilder.equal(rootRole.get("role"), role)));
            return allQuery.getSingleResult();
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Collection<User> findMetaprojectProjectErsteller(long metaid) {
        Query q = em.createNativeQuery(
                "select u.* from user u, relationuserroleabstract reluserrole  \n" +
                        "where u.userId = reluserrole.user_userId and reluserrole.connected_metaProjectId = " + metaid + " and reluserrole.role_roleId = 25");
        List<Object[]> objects = q.getResultList();
        List<User> userList = new ArrayList<>();
        for (Object[] a : objects) {
            userList.add(userDAO.findById(Long.valueOf(a[0].toString())));
        }
        return userList;
    }

    @Override
    public void removeMetaprojectProjectErsteller(long metaid, long userid) {
        Query q = em.createNativeQuery("DELETE FROM relationuserroleabstract " +
                "WHERE user_userid =" + userid + " and connected_metaProjectId =" + metaid + " and role_roleId = 25");
        q.executeUpdate();
    }
}