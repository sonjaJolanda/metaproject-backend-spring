package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.SystemVariable;
import main.java.org.htwg.konstanz.metaproject.repositories.SystemVariableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Implementation of data access object for User class.
 *
 * @author SiKelle
 */
@Service
public class SystemVariableDAOImpl implements SystemVariableDAO {

    private static final Logger log = LoggerFactory.getLogger(SystemVariableDAOImpl.class);

    private final SystemVariableRepository systemVariableRepo;

    public SystemVariableDAOImpl(SystemVariableRepository systemVariableRepo) {
        this.systemVariableRepo = systemVariableRepo;
    }

    @Override
    public SystemVariable save(SystemVariable secret) {
        return systemVariableRepo.save(secret);
    }

    @Override
    public SystemVariable update(SystemVariable transientSecret, String secretKey) {
        SystemVariable persistentSecret = findByKey(secretKey);
        if (persistentSecret == null)
            return null;

        return save(transientSecret);
    }

    @Override
    public SystemVariable findByKey(String secretKey) {
        return systemVariableRepo.findByKey(secretKey).orElse(null);
    }

    @Override
    public Collection<SystemVariable> findAll() {
        return systemVariableRepo.findAll();
    }

    @Override
    public Collection<SystemVariable> remove(String secretKey) {
        log.info("Using Entity Manager to remove key: " + secretKey);

        SystemVariable sv = this.findByKey(secretKey);
        log.info((sv != null) ? "Systemvariable with given key found" : "No Systemvariable with given key found");

        systemVariableRepo.delete(sv);
        return this.findAll();
    }

    @Override
    public String getVariableKey(String key, String fallbackValue) {
        if (fallbackValue == null || fallbackValue.equals(""))
            throw new IllegalArgumentException("Fallbackvalue is required!");

        SystemVariable systemVariable;
        if (key == null || key.equals("")) {
            systemVariable = this.findByKey(fallbackValue);
        } else {
            systemVariable = this.findByKey(key);
        }

        return systemVariable.getValue();
    }


}
