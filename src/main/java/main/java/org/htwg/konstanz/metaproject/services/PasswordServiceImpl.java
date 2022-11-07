package main.java.org.htwg.konstanz.metaproject.services;

import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A password security util class. Get an instance of this class with public
 * getInstance method.
 * 
 * @author SiKelle
 *
 */
@Service
public class PasswordServiceImpl implements PasswordService {

	private final static Logger log = LoggerFactory.getLogger(PasswordServiceImpl.class);

	@Override
	public String securePassword(String plainPassword) {
		try {
			log.debug("Hash password");
			MessageDigest md = MessageDigest.getInstance(Constants.HASH_ALGORITHM);
			md.update(plainPassword.getBytes());
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder();
			for (byte aByte : bytes) {
				sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			log.error("Failed to hash password");
			throw new IllegalStateException(e);
		}
	}

}
