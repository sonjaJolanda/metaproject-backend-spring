package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.entities.TokenKey;
import java.util.List;

/**
 * Data access object for TokenKey class.
 * 
 * @author SiKelle
 *
 */
public interface TokenKeyDAO {

	List<TokenKey> findAll();

	/**
	 * Save a token key to database and returns handled version.
	 */
	TokenKey save(TokenKey key);

}
