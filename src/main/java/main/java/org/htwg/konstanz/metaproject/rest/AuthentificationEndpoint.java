package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.TokenKey;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.persistance.RoleDAO;
import main.java.org.htwg.konstanz.metaproject.persistance.UserDAO;
import main.java.org.htwg.konstanz.metaproject.rights.DefaultRoles;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import main.java.org.htwg.konstanz.metaproject.services.LDAPService;
import main.java.org.htwg.konstanz.metaproject.services.PasswordService;
import main.java.org.htwg.konstanz.metaproject.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author FaAmbros, SiKeller
 * @version 1.2
 */

@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/authenticate")
public class AuthentificationEndpoint {

    private static final Logger log = LoggerFactory.getLogger(AuthentificationEndpoint.class);

    private final UserDAO userDao;

    private final RoleDAO roleDao;

    private final TokenService tokenService;

    private final LDAPService ldapService;

    private final PasswordService passwordService;

    private final RightService rightService;

    public AuthentificationEndpoint(UserDAO userDao, RoleDAO roleDao, TokenService tokenService, LDAPService ldapService, PasswordService passwordService, RightService rightService) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.tokenService = tokenService;
        this.ldapService = ldapService;
        this.passwordService = passwordService;
        this.rightService = rightService;
    }

    /**
     * Authenticate a User by login credentials UserName and UserPassword and return its token.
     */
    @PostMapping(value = "")
    public ResponseEntity<Object> authentificate(@RequestBody User user) {

        long startTime = System.currentTimeMillis();
        //log.info("startTime {}", startTime);
        log.info("Authenticate user {}", user.getUserName());

        //Alter table teamproject_db.user ADD UNIQUE (userName);

        // Check parameters
        if (user.getUserName() == null || user.getUserPassword() == null) {
            log.error("Invalid credentials username password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Check, if user exists in database
        String encryptedPassword = passwordService.securePassword(user.getUserPassword());
        User checkedUser = userDao.checkIfUserIsInDB(user.getUserName(), encryptedPassword);

        if (checkedUser.getError() == 1) {
            log.info("User is not found in database");

            // Check new login with LDAP
            User ldapUser;

            try {
                log.info("Loading server connection");
                ldapUser = ldapService.checkUserCredentials(user.getUserName(), user.getUserPassword());
            } catch (Exception e) {
                log.trace(e.getMessage(), e);
                log.info("No LDAP-Server Connection");
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
            }

            long endTime = System.currentTimeMillis();
            //log.info("endTime {}", endTime);
            long waitingTime = endTime - startTime;
            //log.info("waitingTime {}", waitingTime);
            if (waitingTime >= 10000) {
                log.info("No LDAP-Server Connection");
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
            }

            // If LDAP user is not authenticated
            if (ldapUser == null) {
                log.info("User is not found in LDAP -> FORBIDDEN");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            log.info("User {} is found in LDAP, update password or add to database", ldapUser.getUserName());
            checkedUser = createOrUpdateUser(ldapUser); // Add User to database
        }

        // User is now found/verified -> create and persist token
        TokenKey keyGenFromDB = tokenService.getSharedSecretFromDB();
        String token = tokenService.generateJWTToken(checkedUser.getUserId().toString(), keyGenFromDB.getKeyValue());
        log.info("Create token {}", token);
        userDao.saveTokenToDB(checkedUser, token);

        String json = "{\"token\": \"" + token + "\"}";
        return ResponseEntity.ok(json);
    }

    /**
     * Get a User from database by a token value. This is the current logged in
     * user. If no user is found by token, this method returns a 200 with an
     * empty body.
     *
     * @return ResponseEntity
     */
    @GetMapping(value = "")
    public ResponseEntity<Object> getUser(@RequestHeader String token) {
        log.info("Get User by token");
        try {
            User user = tokenService.getUserByToken(token);
            if (user == null) {
                log.info("User not found for this token {}", token);
                return ResponseEntity.ok().build(); // The empty response is indicates the failed auth.
            }
            log.info("Found user {}", user.getUserName());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Add User with Role global {@link DefaultRoles#USER} and
     * {@link DefaultRoles#USER_EDITOR} with link to itself to database. If user
     * already exists in database, only password has changed and only the
     * password field is updated.
     *
     * @return User
     */
    private User createOrUpdateUser(User user) {
        log.info("Add new user with role 8: {}", user.getUserName());
        log.info("Check whether user already exists in database");
        User databaseUser = userDao.findByUsername(user.getUserName());

        if (databaseUser != null) {
            // User is already in database, but password has changed
            log.info("User was found in database but password has changed");
            changePassword(databaseUser, user.getUserPassword());
            return databaseUser;
        }

        try {
            log.info("User wasn't found in database, create new one");
            // If user isn't already in database
            user.setUserPassword(passwordService.securePassword(user.getUserPassword())); // Secure password
            user.setUserId(null); // Set user id to null, to trigger auto generation
            user = userDao.save(user); // persist user

            // Set initial role for unregistered user to this new user
            roleDao.addRoleAppToUser(user, DefaultRoles.USER);
            roleDao.addRoleUserToUser(user, user, DefaultRoles.USER_EDITOR);

            // Set roles and relation for new user for all metaprojects without needed preregistration
            rightService.createRelationNonPreRegMetaToUserAddRoleToNormalUser(user);
            return user;
        }
        // set error code to 2 (tried to create user twice (bug))
        catch (Exception e) {
            log.trace(e.getMessage(), e);
            user.setError(2);
            log.info("Dublicate User, Aborting");
            return null;
        }
    }

    private User changePassword(User dbUser, String newPassword) {
        dbUser.setUserPassword(passwordService.securePassword(newPassword));
        return userDao.save(dbUser);
    }

    @PutMapping(value = "/user/{id}")
    public ResponseEntity<Object> changeUserPassword(@RequestBody User user, @PathVariable Long id, @RequestHeader String token) {
        log.info("changeUserPassword: check Token: " + token);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User dbUser = userDao.findById(id);
        if (dbUser == null) {
            log.info("No user found for id " + id);
            return ResponseEntity.badRequest().build();
        }

        changePassword(dbUser, user.getUserPassword());
        return ResponseEntity.ok().build();
    }

}