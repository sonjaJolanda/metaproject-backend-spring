package main.java.org.htwg.konstanz.metaproject.mail;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.persistance.EmailDAO;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * This service contains scheduled methods to send emails, which are persisted
 * in the database. It implements {@link Job} and can be used for scheduling
 * with quartz.
 *
 * @author SiKelle
 */


@Service
public class MailSenderServiceJob {

    private static final Logger log = LoggerFactory.getLogger(MailSenderServiceJob.class);

    private final EmailDAO mailDao;

    private final MailService mailService;

    public MailSenderServiceJob(EmailDAO mailDao, MailService mailService) {
        this.mailDao = mailDao;
        this.mailService = mailService;
    }

    /**
     * This method is scheduled and runs in a cron job, sending all emails
     * together to every user. Every user only gets one email with all
     * information.
     */
    @Scheduled(cron = Constants.MAIL_SENDER_SERVICE_CRON)
    public void sendAllEmails() {

        //commented out because of spam
        //SiKelle you had one job :>

		/*

		log.debug("Send all waiting emails.");

		// find all waiting mails first
		Collection<Email> unsendMails = mailDao.findAllWaitingEmails();

		// if no mails are found, return and log result
		if (unsendMails.isEmpty()) {
			log.debug("No waiting mails are found.");
			return;
		}
		log.info("{} emails are found.", unsendMails.size());

		// iterate over all unsend mails
		for (Email email : unsendMails) {
			// try to send the email
			try {
				// send with default account
				String userEmail = email.getReceiver().getUserEmail();
				log.info("Send mail to {}", userEmail);
				mailService.send(MailAccounts.HTWG, userEmail, email.getSubject(),
						email.getBody());
				// set sent status for every mail
				email.setStatus(EmailStatus.SENT);
				// persist new status
				mailDao.update(email, email.getId());
			} catch (MessagingException | NullPointerException | SecurityException e) {
				// this should catch nearly everything and update status for
				// these mails
				log.error("Failed to send Email", e);
				// set error status for every mail
				email.setStatus(EmailStatus.SENDING_ERROR);
				// persist updated status
				mailDao.update(email, email.getId());
			}
		}

		*/

    }

}
