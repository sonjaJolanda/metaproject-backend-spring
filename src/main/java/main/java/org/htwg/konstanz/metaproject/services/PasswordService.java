package main.java.org.htwg.konstanz.metaproject.services;

/**
 * A password security utility class.
 *
 * @author SiKelle
 */
public interface PasswordService {

    /**
     * Create a md5-hashed password to avoid plain stored passwords in database
     *
     * @return generatedPassword
     */
    String securePassword(String plainPassword);

}