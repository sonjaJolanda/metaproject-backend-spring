package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.TokenKey;
import main.java.org.htwg.konstanz.metaproject.repositories.TokenKeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of data access object for TokenKey objects.
 *
 * @author SiKelle
 */
@Service
public class TokenKeyDAOImpl implements TokenKeyDAO {

    private static final Logger log = LoggerFactory.getLogger(TokenKeyDAOImpl.class);

    private final TokenKeyRepository tokenKeyRepo;

    public TokenKeyDAOImpl(TokenKeyRepository tokenKeyRepo) {
        this.tokenKeyRepo = tokenKeyRepo;
    }

    @Override
    public List<TokenKey> findAll() {
        log.debug("Find all token keys");
        return tokenKeyRepo.findAll();
    }

    @Override
    public TokenKey save(TokenKey key) {
        log.debug("Persist TokenKey");
        return tokenKeyRepo.save(key);
    }
}
