package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.persistance.UserDAO;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rest endpoint for user actions.
 *
 * @author FaAmbros, StChiari, SiKelle
 * @version 1.1
 */
@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/user")
public class UserEndpoint {

    private final static Logger log = LoggerFactory.getLogger(UserEndpoint.class);

    private final UserDAO userDao;

    private final RightService rightService;

    public UserEndpoint(UserDAO userDao, RightService rightService) {
        this.userDao = userDao;
        this.rightService = rightService;
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity updateUser(@RequestBody User transientUser, @PathVariable Long id, @RequestHeader String token) {
        log.info("updateUser: check Token: " + token);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForUserRight(Rights.USER_EDIT, id).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Request <-- PUT /user/{} : {}", id, transientUser.getUserId());
        User user = userDao.updateMatrikelnumber(transientUser, id);
        user = userDao.updateCourseOfStudies(transientUser, id);
        user = userDao.updateUserSemesters(transientUser, id);
        user = userDao.updateUserGraduation(transientUser, id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Update Profilepicture
     */
    @PutMapping(value = "/picture/{id}")
    public ResponseEntity updatePicture(@RequestBody User transientUser, @PathVariable Long id, @RequestHeader String token) {
        log.info("updateProfilePicture: check Token: " + token);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForUserRight(Rights.USER_EDIT, id).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Request <-- PUT /user/{} : {}", id, transientUser.getUserId());
        transientUser.getProfilePicture();
        User user = userDao.updateUserProfilePicture(transientUser, id);
        if (user == null) {
            log.info("user null");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.info("after Response");
        return ResponseEntity.ok(user);
    }

    /**
     * Find user by userName, firstName, lastName or userMail. This method
     * returns a list of maximal n users order by userName asc.
     */
    @GetMapping(value = "/search/{searchString}/{max}")
    public ResponseEntity<Object> findUserBySearchString(@PathVariable String searchString, @PathVariable int max, @RequestHeader String token) {
        log.info("findUser: check Token: " + token);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.METAPROJECT_VIEW_INFO).validate();

        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Request <-- GET /user/search/{}/{}", searchString, max);
        return ResponseEntity.ok(userDao.findBySearchString(searchString, max));
    }

    /**
     * Find user by userName, firstName, lastName or userMail. This method
     * returns a list of maximal n users order by userName asc.
     */
    @GetMapping(value = "/search/{searchString}/{max}/{metaId}")
    public ResponseEntity findByMetaTeamlessByString(@PathVariable String searchString, @PathVariable int max,
                                                     @PathVariable Long metaId, @RequestHeader String token) {
        Collection<User> teamless = userDao.findByMetaprojectTeamLess(metaId);
        Collection<User> search = userDao.findBySearchString(searchString, max);
        Collection<User> result = new ArrayList<>();
        for (User searchUser : search) {
            for (User teamlessUser : teamless) {
                if (searchUser.getUserId() == teamlessUser.getUserId()) {
                    result.add(searchUser);
                }
            }
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getUserById(@PathVariable Long id, @RequestHeader String token) {
        log.info("getUser: check Token: " + token);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.USER_VIEW).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Request <-- GET /user/{}", id);
        User user = userDao.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Delete a User in Database
     *
     * @return Response
     */
    @PutMapping("/{id}/inactive")
    public ResponseEntity setUserInactive(@PathVariable Long id, @RequestHeader String token) {
        log.info("deleteUser: check Token: " + token);
        // Routine to check if token is valid & if user has the permissions
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.SUPER_USER)
                .validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isUserToDeleteAdmin = rightService.newRightHandler(id).checkForSuperUser().checkForAppRight(Rights.SUPER_USER).validate();
        if (isUserToDeleteAdmin) // user to delete is admin -> do not delete
            return ResponseEntity.status(423).build();

        User user = userDao.setInactive(id);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(userDao.getAll());
    }

    @GetMapping("")
    public ResponseEntity<Object> getAllUser(@RequestHeader String token) {
        log.info("Request<--GET/user");
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.USER_VIEW).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(userDao.findAll());
    }

    @GetMapping("active")
    public ResponseEntity<Object> getAllActiveUser(@RequestHeader String token, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size,
                                                   @RequestParam(required = false) String sortAttribute, @RequestParam(required = false) Boolean isDescending) {
        log.info("Request<--GET/user/activewith sorting: " + sortAttribute);
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.USER_VIEW).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        page = (page == null) ? 0 : page;
        size = (size == null) ? Integer.MAX_VALUE : size;

        sortAttribute = (sortAttribute == null || sortAttribute.equals("id")) ? "userId" : sortAttribute;
        isDescending = isDescending != null && isDescending;

        return ResponseEntity.ok(userDao.findAllActive(page, size, sortAttribute, isDescending));
    }

    @GetMapping("inactive")
    public ResponseEntity<Object> getAllInActiveUser(@RequestHeader String token, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size,
                                                     @RequestParam(required = false) String sortAttribute, @RequestParam(required = false) Boolean isDescending) {
        log.info("Request<--GET/user/inactive with sorting: " + sortAttribute);
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.USER_VIEW).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        page = (page == null) ? 0 : page;
        size = (size == null) ? Integer.MAX_VALUE : size;

        sortAttribute = (sortAttribute == null || sortAttribute.equals("id")) ? "userId" : sortAttribute;
        isDescending = isDescending != null && isDescending;

        return ResponseEntity.ok(userDao.findAllInactive(page, size, sortAttribute, isDescending));
    }

    @GetMapping(value = "/{activeorinactive}/number")
    public ResponseEntity<Long> getNumberOfActiveUsers(@RequestHeader String token, @PathVariable String activeorinactive) {
        log.info("Request<--GET/user/" + activeorinactive + "/number");
        if (activeorinactive.equals("active"))
            return ResponseEntity.ok(userDao.findAll().stream().filter(u -> !u.getUserName().contains("_inactive")).count());
        if (activeorinactive.equals("inactive"))
            return ResponseEntity.ok(userDao.findAll().stream().filter(u -> u.getUserName().contains("_inactive")).count());
        else
            return ResponseEntity.badRequest().build();
    }
}
