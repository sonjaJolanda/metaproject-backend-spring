package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.RelationMetaprojectUser;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.repositories.RelationMetaprojectUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Data access object implementation for the relation metaproject user.
 */
@Service
public class RelationMetaprojectUserDAOImpl implements RelationMetaprojectUserDAO {

    private static final Logger log = LoggerFactory.getLogger(RelationMetaprojectUserDAOImpl.class);

    private final RelationMetaprojectUserRepository relationMetaprojectUserRepository;

    public RelationMetaprojectUserDAOImpl(RelationMetaprojectUserRepository relationMetaprojectUserRepository) {
        this.relationMetaprojectUserRepository = relationMetaprojectUserRepository;
    }

    @Override
    public RelationMetaprojectUser save(RelationMetaprojectUser transRel) {
        return relationMetaprojectUserRepository.save(transRel);
    }

    @Override
    public RelationMetaprojectUser findByUserAndMetaproject(User user, Metaproject metaproject) {
        List<RelationMetaprojectUser> result = relationMetaprojectUserRepository.findByMetaProjectIdAndUserId(metaproject, user);
        if (result.isEmpty())
            return null;
        else
            return result.get(0);
    }

    @Override
    public Collection<RelationMetaprojectUser> findByUser(User user) {
        return relationMetaprojectUserRepository.findByUserId(user);
    }

    @Override
    public Collection<RelationMetaprojectUser> findByMetaproject(Metaproject metaproject) {
        return relationMetaprojectUserRepository.findByMetaProjectId(metaproject);
    }

    @Override
    public Collection<RelationMetaprojectUser> deleteByMetaproject(Metaproject metaproject) {
        Collection<RelationMetaprojectUser> relations = findByMetaproject(metaproject);
        relationMetaprojectUserRepository.deleteAll(relations);
        return relations;
    }

    @Override
    public void deleteByMetaprojectAndUser(Metaproject metaproject, User user) {
        RelationMetaprojectUser relation = findByUserAndMetaproject(user, metaproject);
        relationMetaprojectUserRepository.delete(relation);
    }

    @Override
    public RelationMetaprojectUser delete(RelationMetaprojectUser relationMetaprojectUser) {
        relationMetaprojectUserRepository.delete(relationMetaprojectUser);
        return relationMetaprojectUser;
    }

}
