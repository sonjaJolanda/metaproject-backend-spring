package main.java.org.htwg.konstanz.metaproject.communication.values;

/**
 * This model describes the current status of a communication object.
 * 
 * @author SiKelle
 *
 */
public enum CommStatus {

	/**
	 * This state describes, that no interactions are done with this
	 * communication object.
	 */
	UNTOUCHED,

	/**
	 * This state describes, that this communication object is answered/read by
	 * the target user, but any other user has to read the answer. The object
	 * with this state is not finished.
	 */
	ANSWERED,

	/**
	 * This state describes, that this communication object is read from all
	 * users and there is no interaction possible with this object. All
	 * communications with this state appears in history.
	 */
	FINISHED

}
