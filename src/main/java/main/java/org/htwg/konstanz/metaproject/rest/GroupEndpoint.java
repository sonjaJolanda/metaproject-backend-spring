package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.dtos.SaveGroupDTO;
import main.java.org.htwg.konstanz.metaproject.entities.TokenInfo;
import main.java.org.htwg.konstanz.metaproject.entities.UserGroup;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import main.java.org.htwg.konstanz.metaproject.services.GroupService;
import main.java.org.htwg.konstanz.metaproject.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint for group management.
 *
 * @author SiKelle
 */
@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/groups")
public class GroupEndpoint {

    private static final Logger log = LoggerFactory.getLogger(MetaprojectEndpoint.class);

    private final GroupService groupService;

    private final RightService rightService;

    private final TokenService tokenService;

    public GroupEndpoint(GroupService groupService, RightService rightService, TokenService tokenService) {
        this.groupService = groupService;
        this.rightService = rightService;
        this.tokenService = tokenService;
    }

    @PostMapping()
    public ResponseEntity createGroup(@RequestBody SaveGroupDTO group, @RequestHeader String token) {
        log.info("Request <-- POST /groups");
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.GROUP_CREATE).validate();
        if (!hasRights)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UserGroup createdGroup = groupService.create(group.getName(), group.getSelectedUsers(), group.getSelectedSubgroups());
        return ResponseEntity.ok(createdGroup);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity updateGroup(@RequestBody SaveGroupDTO group, @PathVariable Long id, @RequestHeader String token) {
        log.info("Request <-- PUT /groups/" + id);
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.GROUP_EDIT).validate();
        if (!hasRights)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UserGroup updatedGroup;
        try {
            updatedGroup = groupService.edit(id, group.getName(), group.getSelectedUsers(), group.getSelectedSubgroups());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CYCLE"); //eTag("")?
        }

        if (updatedGroup == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        else
            return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity getAll(@RequestHeader String token) {
        log.info("Request <-- GET /groups");
        TokenInfo tokenInfo = tokenService.checkExpirationOfToken(token);
        if (tokenInfo == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(groupService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getById(@RequestHeader String token, @PathVariable Long id) {
        log.info("Request <-- GET /groups/" + id);
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.GROUP_LIST).validate();
        if (!hasRights)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UserGroup group = groupService.findById(id);
        if (group == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        else
            return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteGroupById(@RequestHeader String token, @PathVariable Long id) {
        log.info("Request <-- DELETE /groups/" + id);
        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser().checkForAppRight(Rights.GROUP_DELETE).validate();
        if (!hasRights)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (groupService.delete(id))
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
