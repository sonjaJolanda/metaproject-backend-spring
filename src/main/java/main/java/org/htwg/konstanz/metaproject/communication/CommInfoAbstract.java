package main.java.org.htwg.konstanz.metaproject.communication;

import main.java.org.htwg.konstanz.metaproject.communication.values.CommAnswer;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommReactionType;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommRead;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommStatus;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import static org.reflections.Reflections.log;

/**
 * This is an communication object of type {@link CommReactionType#INFORMATION}
 * and has a READ/UNREAD status. Communication of this type has to extends this
 * class.
 * 
 * @author SiKelle
 *
 */
@Entity
public abstract class CommInfoAbstract extends CommAbstract {

	private static final long serialVersionUID = 1L;

	/**
	 * The read/unread status for this communication object. Default is
	 * {@link CommAnswer#NOT_ANSWERED}.
	 */
	@NotNull(message = "readStatus is null")
	@Enumerated(EnumType.ORDINAL)
	private CommRead readStatus = CommRead.NOT_READ;

	@Override
	public CommReactionType getReactionType() {
		return CommReactionType.INFORMATION;
	}

	public String paramReadStatus() {
		return readStatus.toString();
	}

	/**
	 * Return actual read status.
	 * 
	 * @return
	 */
	public CommRead getReadStatus() {
		return this.readStatus;
	}

	public void setReadStatus(CommRead readStatus) {
		this.readStatus = readStatus;
	}

	/**
	 * This method reads the information and set the status for this
	 * communication object to {@link CommStatus#FINISHED} and therefore deletes a message for the user
	 * 
	 * @return
	 */
	public void readComm() {
		// check whether communication object has the right state for this
		// action
		if (!this.getStatus().equals(CommStatus.UNTOUCHED)) {
			throw new IllegalArgumentException("Communication object is already read.");
		}
		// update communication status
		this.setStatus(CommStatus.FINISHED);
		log.info("readComm(): " + this.getStatus());
		this.readStatus = CommRead.READ;

	}

}
