package main.java.org.htwg.konstanz.metaproject.mail;

import main.java.org.htwg.konstanz.metaproject.communication.CommAbstract;
import org.apache.xmlbeans.impl.piccolo.io.IllegalCharException;

import java.util.Map;

/**
 * This service contains methods to build an email with a template from a
 * communication object.
 * 
 * @author SiKelle
 *
 */
public interface EmailTemplateService {

	/**
	 * Replace all placeholder in a template string. The placeholder are marked
	 * with that syntax:
	 * 
	 * <pre>
	 * ...text {{placeholderName}} text...
	 * </pre>
	 *
	 * @return
	 * @throws IllegalCharException
	 */
	public String fillPlaceholder(String template, Map<String, String> values) throws IllegalCharException;

	/**
	 * Extract all param values of a communication object. All getter methods
	 * with prefix "param" are used for this. These methods have to use the
	 * accessor PUBLIC and have to require no arguments. This method adds some
	 * global params too:
	 * <ul>
	 * <li>systemBaseUrl
	 * </ul>
	 */
	public <T extends CommAbstract> Map<String, String> getParamMapOfCommunication(T object);

}