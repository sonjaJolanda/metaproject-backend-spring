package main.java.org.htwg.konstanz.metaproject.configuration;

/**
 * A class, which is used to configure the metaproject application.
 * 
 * @author SiKeller
 *
 */
public class Constants {

	/**
	 * Metaproject url to generate REST interfaces with the correct url
	 */
	public static final String METAPROJECT_REST_URL = "/Metaproject/app";

	/**
	 * Url root for an HTTP request to metaproject. This url is used in mails
	 * and other messages as link to the metaproject application.
	 */
	public static final String METAPROJECT_ROOT_URL = "https://kp2.in.htwg-konstanz.de/Metaproject";

	/**
	 * Enabled debugging of mail sending.
	 */
	public static final Boolean MAIL_ENABLE_DEBUGGING = false;

	/**
	 * Cron job string for the retry transfer to INdigit Job.
	 */
	public static final String INDIGIT_TRANSFER_SERVICE_CRON = "0 */15 * * * *";

	/**
	 * Cron job string for the mail sender service. This variables configures
	 * the cron job for sending emails.
	 */
	public static final String MAIL_SENDER_SERVICE_CRON = "0 */5 * * * *";

	/**
	 * Path to file system, used to save files and assets to a project.
	 */
	public static final String FILE_LOCATION_PATH = "C:\\MetaprojectFileSystem\\";

	/**
	 * Token issuer for token validation and verification.
	 */
	public static final String TOKEN_ISSUER = "HTWG-Konstanz-Metaproject";

	/**
	 * Algorithm for password hashing.
	 * 
	 * <b>Important: A change to this constant needs a database update for all
	 * stored passwords, cause this algorithm is also used for comparison and
	 * authentication.
	 */
	public static final String HASH_ALGORITHM = "MD5";

	/**
	 * Duration days for a valid token until expiration.
	 */
	public static final Long TOKEN_EXPIRATION_DAYS = 10L;

	/**
	 * URL to LDAP system for user authentication.
	 */
	public static final String LDAP_URL = "ldap://ldap.htwg-konstanz.de:389";

	/**
	 * Persistance unit name from application server for metaproject persitance
	 * layer.
	 */
	public static final String PERSISTANCE_UNIT_NAME = "Metaproject-persistence-unit";

	/**
	 * Metaproject main package for component scans.
	 */
	public static final String MAIN_PACKAGE = "org.htwg.konstanz.meaproject";

	/**
	 * The default format to show a date.
	 */
	public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyy HH:mm";

	/**
	 * The default receiver for mails when a user sends the given form.
	 */
	public static final String DEFAULT_CONTACT_MAIL = "kp2in@htwg-konstanz.de";

	/**
	 * The default username and password for the mail-account which is used to send mails towards DEFAULT_CONTACT_MAIL
	 * They are set as environment variables in the docker.yaml file
	 */
	public static final String CONTACT_MAIL_USERNAME = System.getenv("KP2_CONTACT_USERNAME");
	public static final String CONTACT_MAIL_PASSWORD = System.getenv("KP2_CONTACT_PASSWORD");

	/**
	 * Key of SystemVariable which represents the maximum number of failed attempts to transfer to INdigit
	 */
	public static final String FAILED_INDIGIT_TRANSFER_ATTEMPTS = "FAILED_INDIGIT_TRANSFER_ATTEMPTS";

	/**
	 * Key of SystemVariable which represents the time between attempts of transfer to INdigit
	 */
	public static final String FAILED_INDIGIT_TRANSFER_INTERVAL = "FAILED_INDIGIT_TRANSFER_INTERVAL";

	/** public constructor to hide the implicit one */
	private Constants() {
		throw new IllegalAccessError();
	}

	public static final String[] PRIVATE_FIELDS = { "projectLeader","projectTitle","projectDescription","shortProjectDescription"};


}
