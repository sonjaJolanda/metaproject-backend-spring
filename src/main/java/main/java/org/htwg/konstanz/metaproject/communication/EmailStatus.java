package main.java.org.htwg.konstanz.metaproject.communication;

/**
 * This enum describes different email states, whether an email is already send,
 * or waiting for sending.
 * 
 * @author SiKelle
 *
 */
public enum EmailStatus {

	/**
	 * Not sent.
	 */
	WAITING,

	/**
	 * Already successfully sent.
	 */
	SENT,

	/**
	 * Error occured while sending.
	 */
	SENDING_ERROR,

	/**
	 * Error occured while creating this email.
	 */
	CREATION_ERROR

}
