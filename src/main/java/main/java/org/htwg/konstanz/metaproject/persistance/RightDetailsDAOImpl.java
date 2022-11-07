package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.repositories.RightDetailsRepository;
import main.java.org.htwg.konstanz.metaproject.rights.RightDetails;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data access object for right details and information.
 *
 * @author SiKelle
 */
@Service
public class RightDetailsDAOImpl implements RightDetailsDAO {

    private static final Logger log = LoggerFactory.getLogger(RightDetailsDAOImpl.class);

    private final RightDetailsRepository rightDetailsRepo;

    public RightDetailsDAOImpl(RightDetailsRepository rightDetailsRepo) {
        this.rightDetailsRepo = rightDetailsRepo;
    }

    @Override
    public Map<Rights, RightDetails> findAll() {
        log.debug("Find all existing right details");
        List<RightDetails> allRightDetails = rightDetailsRepo.findAll();

        // insert all rights into result map
        Map<Rights, RightDetails> rights = new HashMap<>();
        for (Rights right : Rights.values()) {
            rights.put(right, null);
        }
        // override all rights which are configured in db and add additional details and information
        for (RightDetails details : allRightDetails) {
            rights.put(details.getRight(), details);
        }
        return rights;
    }

    @Override
    public RightDetails findById(Rights right) {
        return rightDetailsRepo.findByRight(right).orElse(null);
    }

}
