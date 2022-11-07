package main.java.org.htwg.konstanz.metaproject.rest;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.ContactForm;
import main.java.org.htwg.konstanz.metaproject.mail.MailAccounts;
import main.java.org.htwg.konstanz.metaproject.mail.MailService;
import main.java.org.htwg.konstanz.metaproject.persistance.SystemVariableDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping(Constants.METAPROJECT_REST_URL + "/contact")
public class ContactEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ContactEndpoint.class);

    private final MailService mailService;

    private final SystemVariableDAO systemVariableDAO;

    public ContactEndpoint(MailService mailService, SystemVariableDAO systemVariableDAO) {
        this.mailService = mailService;
        this.systemVariableDAO = systemVariableDAO;
    }

    @PostMapping(value = "/send")
    public ResponseEntity<Object> postSendContactMail(@RequestBody ContactForm contactForm, @RequestHeader String token) {
        log.info("Request <-- POST /contact/send");
        String receiverMail;
        try {
            receiverMail = systemVariableDAO.getVariableKey("DEFAULT_CONTACT_MAIL", Constants.DEFAULT_CONTACT_MAIL);
        } catch (Exception e) {
            log.warn(e.getMessage());
            receiverMail = Constants.DEFAULT_CONTACT_MAIL;
        }
        if (token != "" || token != null) {
            /**
             *
             */
        }

        /**
         * simple textmail with at least a bit of formatting.
         */
        String message = "Informationen zum Sender:";
        message += "<br />Name:\t" + contactForm.getSender();
        message += "<br />E-Mail:\t" + contactForm.getEmail();
        message += "<br /><br />";
        message += contactForm.getMessage().replaceAll("(\\r\\n|\\n)", "<br />");

        return this.sendContactMail(receiverMail, contactForm.getSubject(), message);
    }

    private ResponseEntity<Object> sendContactMail(String receiver, String subject, String message) {
        try {
            mailService.send(MailAccounts.HTWG, receiver, subject, message);
            return ResponseEntity.ok().build();
        } catch (MessagingException e) {
            log.info("Fehler: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
