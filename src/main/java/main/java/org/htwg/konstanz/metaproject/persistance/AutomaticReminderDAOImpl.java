package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.AutomaticReminder;
import main.java.org.htwg.konstanz.metaproject.repositories.AutomaticReminderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AutomaticReminderDAOImpl implements AutomaticReminderDAO {

    private static final Logger log = LoggerFactory.getLogger(AutomaticReminderDAOImpl.class);

    private final AutomaticReminderRepository automaticReminderRepo;

    public AutomaticReminderDAOImpl(AutomaticReminderRepository automaticReminderRepo) {
        this.automaticReminderRepo = automaticReminderRepo;
    }

    @Override
    public AutomaticReminder findById(Long id) {
        return automaticReminderRepo.findById(id).orElse(null);
    }

    @Override
    public AutomaticReminder save(AutomaticReminder automaticReminder) {
        return automaticReminderRepo.save(automaticReminder);
    }

    @Override
    public AutomaticReminder update(Long autoRemId, AutomaticReminder transientAutomaticReminder) {
        AutomaticReminder automaticReminder = automaticReminderRepo.findById(autoRemId).orElse(null);
        if (automaticReminder == null)
            return null;

        transientAutomaticReminder.setAutoRemId(autoRemId);
        return automaticReminderRepo.save(transientAutomaticReminder);
    }

    @Override
    public AutomaticReminder delete(Long autoRemId) {
        AutomaticReminder automaticReminder = automaticReminderRepo.findById(autoRemId).orElse(null);
        if (automaticReminder != null)
            automaticReminderRepo.delete(automaticReminder);
        return automaticReminder;
    }

    @Override
    public Collection<AutomaticReminder> findAll() {
        return automaticReminderRepo.findAll();
    }
}
