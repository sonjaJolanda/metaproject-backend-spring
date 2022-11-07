package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.CategoryReminder;
import main.java.org.htwg.konstanz.metaproject.repositories.CategoryReminderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CategoryReminderDAOImpl implements CategoryReminderDAO {

    private static final Logger log = LoggerFactory.getLogger(CategoryReminderDAOImpl.class);

    private final CategoryReminderRepository categoryReminderRepo;

    public CategoryReminderDAOImpl(CategoryReminderRepository categoryReminderRepo) {
        this.categoryReminderRepo = categoryReminderRepo;
    }

    @Override
    public CategoryReminder findById(Long id) {
        return categoryReminderRepo.findById(id).orElse(null);
    }

    @Override
    public Collection<CategoryReminder> findAll() {
        return categoryReminderRepo.findAll();
    }

}
