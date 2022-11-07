package main.java.org.htwg.konstanz.metaproject.repositories;

import main.java.org.htwg.konstanz.metaproject.communication.EmailTemplate;
import main.java.org.htwg.konstanz.metaproject.communication.EmailTemplateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    Optional<EmailTemplate> findByEmailTemplateType(EmailTemplateType emailTemplateType);

}
