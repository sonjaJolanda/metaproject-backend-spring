package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.UserGroup;
import main.java.org.htwg.konstanz.metaproject.repositories.UserGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Data access object for groups.
 *
 * @author SiKelle
 */
@Service
public class GroupDAOImpl implements GroupDAO {

    private static final Logger log = LoggerFactory.getLogger(GroupDAOImpl.class);

    private final UserGroupRepository userGroupRepo;

    public GroupDAOImpl(UserGroupRepository userGroupRepo) {
        this.userGroupRepo = userGroupRepo;
    }

    @Override
    public UserGroup save(UserGroup group) {
        return userGroupRepo.save(group);
    }

    @Override
    public UserGroup update(UserGroup transientGroup, Long id) {
        UserGroup group = findById(id);
        if (group == null)
            return null;

        transientGroup.setId(id);
        userGroupRepo.save(transientGroup);
        return transientGroup;
    }

    @Override
    public UserGroup findById(Long id) {
        return userGroupRepo.findById(id).orElse(null);
    }

    @Override
    public UserGroup remove(Long id) {
        UserGroup group = findById(id);
        if (group != null)
            userGroupRepo.delete(group);
        return group;
    }

    @Override
    public Collection<UserGroup> findAll() {
        return userGroupRepo.findAll();
    }

}
