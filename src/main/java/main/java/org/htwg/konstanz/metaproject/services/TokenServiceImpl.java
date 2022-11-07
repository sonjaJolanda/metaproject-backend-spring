package main.java.org.htwg.konstanz.metaproject.services;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.TokenInfo;
import main.java.org.htwg.konstanz.metaproject.entities.TokenKey;
import main.java.org.htwg.konstanz.metaproject.entities.User;
import main.java.org.htwg.konstanz.metaproject.persistance.TokenKeyDAO;
import main.java.org.htwg.konstanz.metaproject.persistance.UserDAO;
import net.oauth.jsontoken.Checker;
import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.JsonTokenParser;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;
import net.oauth.jsontoken.crypto.HmacSHA256Verifier;
import net.oauth.jsontoken.crypto.SignatureAlgorithm;
import net.oauth.jsontoken.crypto.Verifier;
import net.oauth.jsontoken.discovery.VerifierProvider;
import net.oauth.jsontoken.discovery.VerifierProviders;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.List;

/**
 * Service for token verification and token handling. Get an instance of this
 * class with public getInstance method.
 * 
 * @author SiKelle
 *
 */
@Service
public class TokenServiceImpl implements TokenService {

	private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
	private static final String WEB_TOKEN_AUDIENCE = "User";

	private static final Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);

	private final UserDAO userDao;

	private final TokenKeyDAO tokenKeyDao;

	public TokenServiceImpl(UserDAO userDao, TokenKeyDAO tokenKeyDao) {
		this.userDao = userDao;
		this.tokenKeyDao = tokenKeyDao;
	}

	/**
	 * Check expiration of token. Returns the tokenInfo object for this token or
	 * null if token isn't valid or expired.
	 *
	 * @return tokenInfo, or null if expired
	 */
	@Override
	public TokenInfo checkExpirationOfToken(String token) {
		log.debug("Check expiration of token: {}", token);

		if (token == null || token.equals("")) {
			log.error("Token is invalid or null: {}", token);
			return null;
		}

		Long now = Calendar.getInstance().getTimeInMillis();
		TokenInfo tokenInfo = getTokenInfo(token);

		// it is important to check null values because invalid token are null
		// and this should avoid NullPointerExceptions
		if (tokenInfo == null || tokenInfo.getExpires() == null) {
			return null;
		}

		log.debug("Token expiration, date in millis now: {} and at expiration: {}", now,
				tokenInfo.getExpires().getMillis());

		// Check whether now is greater than token expiration date
		if (now > tokenInfo.getExpires().getMillis()) {
			log.info("Token expired: {}", token);
			return null;
		}
		
		log.debug("Token not expired for user {}", tokenInfo.getUserId());
		return tokenInfo;
	}

	/**
	 * Get an specific user by token.
	 */
	@Override
	public User getUserByToken(String token) {
		TokenInfo infoFromToken = getTokenInfo(token);
		if (infoFromToken == null) {
			// no token or no user
			return null;
		}
		return userDao.findById(infoFromToken.getUserId());
	}

	/**
	 * Verify and get TokenInfo from token String. Verifies a json web token's
	 * validity and extracts the user id and other information from it.
	 */
	@Override
	public TokenInfo getTokenInfo(String token) {
		log.debug("Get token info: {}", token);

		TokenKey ssFormDb = getSharedSecretFromDB();
		String ss = ssFormDb.getKeyValue();
		if (token == null || ss.equals("ERROR")) {
			log.error("Token error: {}", token);
			log.error("Try to get token info, but secret from database is empty.");
			return null;
		}
		try {
			final Verifier hmacVerifier = new HmacSHA256Verifier(ss.getBytes());
			VerifierProvider hmacLocator = new VerifierProvider() {
				@Override
				public List<Verifier> findVerifier(String id, String key) {
					return Lists.newArrayList(hmacVerifier);
				}
			};
			VerifierProviders locators = new VerifierProviders();

			locators.setVerifierProvider(SignatureAlgorithm.HS256, hmacLocator);
			Checker checker = new Checker() {
				@Override
				public void check(JsonObject payload) throws SignatureException {
					// TODO Auto-generated method stub
					// and why is nothing checked here?
				}
			};
			JsonTokenParser parser = new JsonTokenParser(locators, checker);
			JsonToken jsonToken;

			jsonToken = parser.verifyAndDeserialize(token);
			JsonObject payload = jsonToken.getPayloadAsJsonObject();
			TokenInfo tokenInfo = new TokenInfo();

			String issuer = payload.getAsJsonPrimitive("iss").getAsString();
			Long userIdLong = payload.getAsJsonObject("info").getAsJsonPrimitive("userId").getAsLong();
			if (!issuer.equals(Constants.TOKEN_ISSUER)) {
				log.error("Incorrect token issuer: {}", issuer);
				return null;
			}
			tokenInfo.setUserId(userIdLong);
			tokenInfo.setIssued(new DateTime(payload.getAsJsonPrimitive("iat").getAsLong() * 1000));
			tokenInfo.setExpires(new DateTime(payload.getAsJsonPrimitive("exp").getAsLong() * 1000));
			return tokenInfo;
		} catch (SignatureException | InvalidKeyException | IllegalArgumentException | IllegalStateException e) {
			log.error(e.getMessage(), e);
			return null;
		}

	}

	/**
	 * Creates a json web token. The signing key is secret. That ensures that
	 * the token is authentic and has not been modified.
	 *
	 * @return JWTToken as String
	 */
	@Override
	public String generateJWTToken(String userID, String sharedSecret) {
		log.info("Generate json web token for {} days for user {}", Constants.TOKEN_EXPIRATION_DAYS, userID);
		try {
			Calendar cal = Calendar.getInstance();
			HmacSHA256Signer signer = new HmacSHA256Signer(Constants.TOKEN_ISSUER, null, sharedSecret.getBytes());
			JsonToken token = new JsonToken(signer);
			token.setAudience(WEB_TOKEN_AUDIENCE);

			token.setIssuedAt(new Instant(cal.getTimeInMillis()));
			long expirationDuration = 1000L * 60L * 60L * 24L * Constants.TOKEN_EXPIRATION_DAYS;
			token.setExpiration(
					new Instant(cal.getTimeInMillis() + expirationDuration));
			JsonObject request = new JsonObject();
			request.addProperty("userId", userID);

			JsonObject payload = token.getPayloadAsJsonObject();
			payload.add("info", request);
			return token.serializeAndSign();
		} catch (InvalidKeyException | SignatureException e) {
			log.error(e.getMessage(), e);
			return "ERROR";
		}
	}

	/**
	 * Get SharedSecretFrom Database. If no secret in database, then create new.
	 * 
	 * @return TokenKey
	 */
	@Override
	public TokenKey getSharedSecretFromDB() {
		log.debug("Get shared secret from database");

		try {
			// Find all token keys
			List<TokenKey> keys = tokenKeyDao.findAll();
			// Check whether token keys are found
			if (!keys.isEmpty()) {
				// Return last key
				return keys.get((keys.size() - 1));
			}
			log.info("No token key found in database");
			// Create new one
			TokenKey keyToDB = new TokenKey();
			keyToDB.setKeyValue(createSharedSecret());
			// And persist it to database
			keyToDB = tokenKeyDao.save(keyToDB);
			return keyToDB;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Generate a Random key
	 * 
	 * @return String secretKey
	 */
	@Override
	public String createSharedSecret() {
		log.info("Create a new shared secret (random key)");
		try {
			byte[] bytes = new byte[1024 / 8];
			SecureRandom sr = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
			sr.nextBytes(bytes);
			return new String(Hex.encodeHex(bytes));
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
			throw new IllegalStateException();
		}
	}

}
