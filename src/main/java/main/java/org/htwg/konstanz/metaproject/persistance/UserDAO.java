package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.Metaproject;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

/**
 * Data access object for User class.
 *
 * @author SiKelle
 */
public interface UserDAO {

    /**
     * Save a new user in database, the returned user is updated with generated
     * id and is managed. The passed instance you pass in will not be managed
     * (any changes will not be part of the transaction - unless you call save
     * again).
     */
    User save(User user);

    /**
     * Update existing user with user id. If user does not exist, null is
     * returned. The passed instance in will not be managed (any changes will
     * not be part of the transaction - unless you call update again).
     */
    User update(User transientUser, Long id);

    /**
     * Update only profile picture field of a user object. Returned user is
     * handled in database.
     */
    User updateUserProfilePicture(User transientUser, Long id);

    /**
     * Update only matrikelnumber field of a user object. Returned user is
     * handled in database.
     */
    User updateMatrikelnumber(User transientUser, Long id);

    /**
     * Update only courseOfStudies field of a user object. Returned user is
     * handled in database.
     */
    User updateCourseOfStudies(User transientUser, Long id);

    /**
     * Update only semesters field of a user object. Returned user is
     * handled in database.
     */
    User updateUserSemesters(User transientUser, Long id);

    /**
     * Update only graduation field of a user object. Returned user is
     * handled in database.
     */
    User updateUserGraduation(User transientUser, Long id);

    User findById(Long id);

    /**
     * Find a user by case insensitive username. Returns null, if no user was
     * found.
     */
    User findByUsername(String username);

    /**
     * Find a list of users by a search string, matching with
     * <ul>
     * <li>userName
     * <li>userEmail
     * <li>userFirstName
     * <li>userLastName
     * <li>userFirstNam userLastName (userName)
     * </ul>
     * The parameter max limits the result of found users.
     */
    Collection<User> findBySearchString(String searchString, int max);

    /**
     * Find a list of users <b>in a metaproject, which are not in a team</b>.
     */
    Collection<User> findByMetaprojectTeamLess(Long metaprojectId);

    /**
     * Find user with username and password in database. Username could is case
     * insensitive and password should be encrypted.
     */
    User checkIfUserIsInDB(String name, String pass);

    /**
     * Save a token to given user to database. (This is an update method.)
     */
    void saveTokenToDB(User user, String token);

    /**
     * Remove existing user by id from database. If user isn't found in database
     * this method returns null instead of removed user.
     */
    User setInactive(Long id);

    Collection<User> getAll();

    /**
     * Gets the highest Id form database
     *
     * @return id
     */
    Long highestId();

    Collection<User> findAll();

    List<User> findAllActive(int page, int size, String sortAttribute, boolean isDescending);

    List<User> findAllInactive(int page, int size, String sortAttribute, boolean isDescending);
}
