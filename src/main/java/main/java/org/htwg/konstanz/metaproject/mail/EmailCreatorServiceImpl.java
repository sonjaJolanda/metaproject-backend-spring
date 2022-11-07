package main.java.org.htwg.konstanz.metaproject.mail;

import main.java.org.htwg.konstanz.metaproject.communication.*;
import main.java.org.htwg.konstanz.metaproject.persistance.EmailTemplateDAO;
import org.apache.xmlbeans.impl.piccolo.io.IllegalCharException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * This service is used to create {@link Email} for a specific case.
 *
 * @author SiKelle
 */
@Service
public class EmailCreatorServiceImpl implements EmailCreatorService {

    private static final Logger log = LoggerFactory.getLogger(EmailCreatorServiceImpl.class);

    private final EmailTemplateDAO emailTemplateDAO;

    private final EmailTemplateService emailTemplateService;

    public EmailCreatorServiceImpl(EmailTemplateDAO emailTemplateDAO, EmailTemplateService emailTemplateService) {
        this.emailTemplateDAO = emailTemplateDAO;
        this.emailTemplateService = emailTemplateService;
    }

    @Override
    public <T extends CommAbstract> Collection<Email> getEmailForType(T comm, EmailTemplateType emailType) {
        // find template from database
        final EmailTemplate emailTemplate = emailTemplateDAO.findById(emailType);
        Email email = new Email();
        // check whether the template is found
        if (emailTemplate == null) {
            log.error("No email template found for that type {}! An error email is created.", emailType.toString());
            email.setStatus(EmailStatus.CREATION_ERROR);
            email.setSubject("");
            email.setBody("");
            email.setReceiver(comm.getTargetUser());
            Collection<Email> result = new LinkedList<>();
            result.add(email);
            return result;
        }
        // find values of this communication to fill template placeholder
        Map<String, String> values = emailTemplateService.getParamMapOfCommunication(comm);

        // set target user as receiver
        email.setReceiver(comm.getTargetUser());

        try {
            // create subject and body
            String subject = emailTemplateService.fillPlaceholder(emailTemplate.getSubject(), values);
            email.setSubject(subject);
            String body = emailTemplateService.fillPlaceholder(emailTemplate.getTemplate(), values);
            email.setBody(body);
        } catch (IllegalCharException e) {
            log.error(e.getMessage(), e);
            email.setStatus(EmailStatus.CREATION_ERROR);
            email.setSubject("");
            email.setBody("");
        }
        // return single email as list of emails with one entry
        Collection<Email> result = new LinkedList<>();
        result.add(email);
        return result;
    }

}
