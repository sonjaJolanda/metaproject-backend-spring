package main.java.org.htwg.konstanz.metaproject.services;

import main.java.org.htwg.konstanz.metaproject.entities.TokenInfo;
import main.java.org.htwg.konstanz.metaproject.entities.TokenKey;
import main.java.org.htwg.konstanz.metaproject.entities.User;

/**
 * Service for token verification and token creation.
 *
 * @author SiKelle
 */
public interface TokenService {

    /**
     * Check expiration of token. Returns the tokenInfo object for this token or
     * null if token isn't valid or expired.
     */
    TokenInfo checkExpirationOfToken(String token);

    /**
     * Get an specific user by token.
     */
    User getUserByToken(String token);

    /**
     * Verify and get TokenInfo from token String. Verifies a json web token's
     * validity and extracts the user id and other information from it.
     */
    TokenInfo getTokenInfo(String token);

    /**
     * Creates a json web token. The signing key is secret. That ensures that
     * the token is authentic and has not been modified.
     *
     * @return JWTToken as String
     */
    String generateJWTToken(String userID, String sharedSecret);

    /**
     * Get SharedSecretFrom Database. If no secret in database, then create new.
     *
     * @return TokenKey
     */
    TokenKey getSharedSecretFromDB();

    /**
     * Generate a Random key
     *
     * @return String secretKey
     */
    String createSharedSecret();
}
