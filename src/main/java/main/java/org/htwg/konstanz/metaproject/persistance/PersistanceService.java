package main.java.org.htwg.konstanz.metaproject.persistance;

/**
 * Persistance service to access methods of hibernate persistance object
 * mapping. These methods should only be used if really needed, they could
 * create issues and bugs.
 * 
 * @author SiKelle
 *
 */
public interface PersistanceService {

	/**
	 * Clear Hibernate cache.
	 */
	void clearCache();

}