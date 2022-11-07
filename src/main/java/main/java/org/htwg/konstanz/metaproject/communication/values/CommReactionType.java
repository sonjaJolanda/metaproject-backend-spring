package main.java.org.htwg.konstanz.metaproject.communication.values;

/**
 * This enum describes all possible answers/reactions for a communication
 * object.
 * 
 * @author SiKelle
 *
 */
public enum CommReactionType {

	/**
	 * This type only has a flag, whether it is read or not. The user can only
	 * click OK or READ.
	 */
	INFORMATION,

	/**
	 * This type has two possible answers: YES, NO.
	 */
	AGREE_REJECT

}
