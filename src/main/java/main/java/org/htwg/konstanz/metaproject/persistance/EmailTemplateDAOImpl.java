package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.communication.EmailTemplate;
import main.java.org.htwg.konstanz.metaproject.communication.EmailTemplateType;
import main.java.org.htwg.konstanz.metaproject.repositories.EmailTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of data access object for {@link EmailTemplate}.
 *
 * @author SiKelle
 */
@Service
public class EmailTemplateDAOImpl implements EmailTemplateDAO {

    private static final Logger log = LoggerFactory.getLogger(EmailTemplateDAOImpl.class);

    private final EmailTemplateRepository emailTemplateRepo;

    public EmailTemplateDAOImpl(EmailTemplateRepository emailTemplateRepo) {
        this.emailTemplateRepo = emailTemplateRepo;
    }

    @Override
    public EmailTemplate findById(EmailTemplateType type) {
        return emailTemplateRepo.findByEmailTemplateType(type).orElse(null);
    }

}
