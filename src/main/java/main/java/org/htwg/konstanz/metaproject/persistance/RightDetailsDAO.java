package main.java.org.htwg.konstanz.metaproject.persistance;

import main.java.org.htwg.konstanz.metaproject.rights.RightDetails;
import main.java.org.htwg.konstanz.metaproject.rights.Rights;
import java.util.Map;

/**
 * Data access object for right details and information.
 * 
 * @author SiKelle
 *
 */
public interface RightDetailsDAO {

	/**
	 * Find details for a {@link Rights}, but there is an empty
	 * {@link RightDetails} object, if this right isn't described in database.
	 */
	RightDetails findById(Rights right);

	/**
	 * List all right details as Map from database. It contains all existing
	 * {@link Rights}, but there is an empty {@link RightDetails} object, if
	 * this right isn't described in database.
	 */
	Map<Rights, RightDetails> findAll();

}