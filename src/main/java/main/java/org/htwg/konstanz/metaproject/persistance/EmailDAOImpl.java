package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.communication.Email;
import main.java.org.htwg.konstanz.metaproject.communication.EmailStatus;
import main.java.org.htwg.konstanz.metaproject.repositories.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Implementation for Email data access object.
 *
 * @author SiKelle
 */
@Service
public class EmailDAOImpl implements EmailDAO {

    private static final Logger log = LoggerFactory.getLogger(EmailDAOImpl.class);

    private final EmailRepository emailRepo;

    public EmailDAOImpl(EmailRepository emailRepo) {
        this.emailRepo = emailRepo;
    }

    @Override
    public Email save(Email email) {
        Email savedEmail = emailRepo.save(email);
        return savedEmail;
    }

    @Override
    public Email update(Email transientEmail, Long id) {
        Email email = findById(id);
        if (email == null)
            return null;

        transientEmail.setId(id);
        Email updatedEmail = emailRepo.save(transientEmail);
        return updatedEmail;
    }

    @Override
    public Collection<Email> updateAll(Collection<Email> transientEmails) {
        Collection<Email> updatedEmails = new LinkedList<>();
        for (Email email : transientEmails) {
            updatedEmails.add(update(email, email.getId()));
        }
        return updatedEmails;

    }

    @Override
    public Email findById(Long id) {
        return emailRepo.findById(id).orElse(null);
    }

    @Override
    public Collection<Email> findAllWaitingEmails() {
        return emailRepo.findByStatus(EmailStatus.WAITING);
    }

}
