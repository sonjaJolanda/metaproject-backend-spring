package main.java.org.htwg.konstanz.metaproject.persistance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnit;

/**
 * Implementation of persistance service.
 *
 * @author SiKelle
 */
@Service
public class PersistanceServiceImpl implements PersistanceService {

    @Autowired
    private EntityManager em;

    @Override
    public void clearCache() {
        em.clear();
    }

}
