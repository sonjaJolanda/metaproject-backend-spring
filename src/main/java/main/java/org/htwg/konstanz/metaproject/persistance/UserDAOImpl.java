package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Implementation of data access object for User class.
 *
 * @author SiKelle, ThPenzko
 */
@Service
public class UserDAOImpl implements UserDAO {

    private static final Logger log = LoggerFactory.getLogger(UserDAOImpl.class);

    private final UserRepository userRepo;

    public UserDAOImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User save(User user) {
        user.setUserGraduation(trim(user.getUserGraduation()));
            return userRepo.save(user);
    }

    @Override
    public User updateMatrikelnumber(User transientUser, Long id) {
        User user = findById(id);
        if (user == null)
            return null;

        user.setMatrikelNumber(transientUser.getMatrikelNumber());
        return update(user, id);
    }

    @Override
    public User updateCourseOfStudies(User transientUser, Long id) {
        User user = findById(id);
        if (user == null)
            return null;

        user.setCourseOfStudies(transientUser.getCourseOfStudies());
        return update(user, id);
    }

    @Override
    public User updateUserSemesters(User transientUser, Long id) {
        User user = findById(id);
        if (user == null)
            return null;

        user.setUserSemesters(transientUser.getUserSemesters());
        return update(user, id);
    }

    @Override
    public User updateUserGraduation(User transientUser, Long id) {
        User user = findById(id);
        if (user == null)
            return null;

        user.setUserGraduation(transientUser.getUserGraduation());
        return update(user, id);
    }

    @Override
    public User update(User transientUser, Long id) {
        User user = findById(id);
        if (user == null)
            return null;

        transientUser.setUserId(id);
        userRepo.save(transientUser);
        return transientUser;
    }

    @Override
    public User updateUserProfilePicture(User transientUser, Long id) {
        User user = findById(id);
        if (user == null)
            return null;

        user.setProfilePicture(transientUser.getProfilePicture());
        return update(user, id);
    }

    @Override
    public User findById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        if (username == null)
            return null;
        return userRepo.findByUserName(username.toLowerCase()).orElse(null);
    }

    @Override
    public Collection<User> findBySearchString(String searchString, int max) {
        String likeString = "%" + searchString + "%";

        List<User> user = userRepo.findBySearchString(likeString);
        if (user.size() > max)
            user = user.subList(0, max);

        return user;
    }

    @Override
    public Collection<User> findByMetaprojectTeamLess(Long metaprojectId) {
        return userRepo.findByMetaprojectTeamLess(metaprojectId);
    }

    @Override
    public User checkIfUserIsInDB(String name, String pass) {
        log.info("Check if user is in db: {}", name);

        try {
            Optional<User> userOptional = userRepo.findByUserName(name.toLowerCase());

            if (userOptional.isEmpty()) {
                log.trace("User " + name + " is not found in database.");
                User user = new User();
                user.setError(1);
                return user;
            } else if (userOptional.get().getUserPassword().equals(pass)) {
                return userOptional.get();
            } else {
                log.trace("User {} with given password not found in database.", name);
                User user = new User();
                user.setError(1);
                return user;
            }
        } catch (Exception e) {
            log.trace(e.getMessage(), e);
            User user = new User();
            user.setError(2);
            log.info("Error occured", user.getError());
            return user;
        }
    }

    @Override
    public void saveTokenToDB(User user, String token) {
        log.info("Save token to database");
        user.setTokenJwt(token);
        userRepo.save(user);
    }

    @Override
    public User setInactive(Long id) {
        User user = findById(id);
        if (user == null)
            return null;

        //already inactive users should not be set inactive
        if (user.getUserName().contains("_inactive"))
            return null;

        List<BigInteger> results = userRepo.getInactiveUserCount(user.getUserName());
        long count = results.get(0).longValue();
        String inactiveUserName = user.getUserName() + "_inactive";

        // if we already have this userName set inactive
        if (count > 0)
            inactiveUserName = inactiveUserName + count;

        user.setUserName(inactiveUserName);
        return update(user, id);
    }

    @Override
    public Collection<User> getAll() {
        return userRepo.findAll();
    }

    @Override
    public Long highestId() {
        List<BigInteger> result = userRepo.getMaxUserId();
        Long maxID = result.get(0).longValue();

        log.info("#####################################################" + maxID);
        return maxID;
    }

    @Override
    public Collection<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public List<User> findAllActive(int page, int size, String sortAttribute, boolean isDescending) {
        Pageable pageable;
        if (isDescending)
            pageable = PageRequest.of(page, size, Sort.by(sortAttribute).descending());
        else
            pageable = PageRequest.of(page, size, Sort.by(sortAttribute).ascending());

        return userRepo.getActiveUserPaginated(pageable).getContent();
    }

    @Override
    public List<User> findAllInactive(int page, int size, String sortAttribute, boolean isDescending) {
        Pageable pageable;
        if (isDescending)
            pageable = PageRequest.of(page, size, Sort.by(sortAttribute).descending());
        else
            pageable = PageRequest.of(page, size, Sort.by(sortAttribute).ascending());
        return userRepo.getInactiveUserPaginated(pageable).getContent();
    }
}
