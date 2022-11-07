package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.FailedINdigitTransfer;
import main.java.org.htwg.konstanz.metaproject.entities.Project;
import main.java.org.htwg.konstanz.metaproject.entities.SystemVariable;
import main.java.org.htwg.konstanz.metaproject.persistance.FailedINdigitTransferDAO;
import main.java.org.htwg.konstanz.metaproject.persistance.ProjectDAO;
import main.java.org.htwg.konstanz.metaproject.persistance.SystemVariableDAO;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import main.java.org.htwg.konstanz.metaproject.security.RightService;
import main.java.org.htwg.konstanz.metaproject.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Sonja Klein
 * @version 1.0
 */
@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/failedindigittransfers")
public class FailedINdigitTransferEndpoint {

    static final String FAILEDINDIGITTRANSFERS = "failedindigittransfers";

    private final static Logger log = LoggerFactory.getLogger(FailedINdigitTransferEndpoint.class);

    private final RightService rightService;

    private final TokenService tokenService;

    private final FailedINdigitTransferDAO failedINdigitTransferDAO;

    private final SystemVariableDAO systemVariableDAO;

    private final ProjectDAO projectDAO;

    public FailedINdigitTransferEndpoint(RightService rightService, TokenService tokenService, FailedINdigitTransferDAO failedINdigitTransferDAO, SystemVariableDAO systemVariableDAO, ProjectDAO projectDAO) {
        this.rightService = rightService;
        this.tokenService = tokenService;
        this.failedINdigitTransferDAO = failedINdigitTransferDAO;
        this.systemVariableDAO = systemVariableDAO;
        this.projectDAO = projectDAO;
    }

    @GetMapping(value = "")
    public ResponseEntity getAllFailedIndigitTransfers(@RequestHeader String token) {
        log.info("Request <-- GET /" + FAILEDINDIGITTRANSFERS);

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.INDIGIT_VIEW).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<FailedINdigitTransfer> failedINdigitTransfers = failedINdigitTransferDAO.findAll();
        return ResponseEntity.ok(failedINdigitTransfers);
    }


    @GetMapping(value = "/minutesofintervall")
    public ResponseEntity getMinutesOfFailedTransfersIntervall(@RequestHeader String token) {
        log.info("Request <-- GET /" + FAILEDINDIGITTRANSFERS + "/minutesofintervall");

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.INDIGIT_VIEW).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SystemVariable attemptsSystemVariable = systemVariableDAO.findByKey(Constants.FAILED_INDIGIT_TRANSFER_INTERVAL);
        return ResponseEntity.ok((Integer.parseInt(attemptsSystemVariable.getValue()) / 1000 / 60));
    }

    @PutMapping(value = "/minutesofintervall")
    public ResponseEntity setMinutesOfFailedTransfersIntervall(@RequestHeader String token, @RequestBody Integer minutes) {
        log.info("Request <-- PUT /" + FAILEDINDIGITTRANSFERS + "/minutesofintervall");

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.INDIGIT_CONFIGURATION).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SystemVariable attemptsSystemVariable = systemVariableDAO.findByKey(Constants.FAILED_INDIGIT_TRANSFER_INTERVAL);
        attemptsSystemVariable.setValue(String.valueOf(minutes * 60 * 1000));
        systemVariableDAO.save(attemptsSystemVariable);
        return ResponseEntity.ok(Integer.parseInt(attemptsSystemVariable.getValue()) / 1000 / 60);
    }

    @GetMapping(value = "/numberofattempts")
    public ResponseEntity getNumberOfAttemptsUntilStop(@RequestHeader String token) {
        log.info("Request <-- GET /" + FAILEDINDIGITTRANSFERS + "/numberofattempts");

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.INDIGIT_VIEW).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SystemVariable attemptsSystemVariable = systemVariableDAO.findByKey(Constants.FAILED_INDIGIT_TRANSFER_ATTEMPTS);
        return ResponseEntity.ok(attemptsSystemVariable.getValue());
    }

    @PutMapping(value = "/numberofattempts")
    public ResponseEntity setNumberOfAttemptsUntilStop(@RequestHeader String token, @RequestBody Integer attempts) {
        log.info("Request <-- PUT /" + FAILEDINDIGITTRANSFERS + "/numberofattempts");

        boolean hasRights = rightService.newRightHandler(token).checkForSuperUser()
                .checkForAppRight(Rights.INDIGIT_CONFIGURATION).validate();
        if (!hasRights) {
            log.info("User has no permissions to do that operation.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (attempts > 99 || attempts < 0)
            return ResponseEntity.badRequest().body("attempts has to be a positive number (including 0 and less than 100");

        SystemVariable attemptsSystemVariable = systemVariableDAO.findByKey(Constants.FAILED_INDIGIT_TRANSFER_ATTEMPTS);
        attemptsSystemVariable.setValue(String.valueOf(attempts));
        systemVariableDAO.save(attemptsSystemVariable);
        return ResponseEntity.ok(attemptsSystemVariable.getValue());
    }

}
