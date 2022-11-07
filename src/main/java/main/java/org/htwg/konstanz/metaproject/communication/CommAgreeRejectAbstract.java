package main.java.org.htwg.konstanz.metaproject.communication;

import main.java.org.htwg.konstanz.metaproject.communication.values.CommAnswer;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommReactionType;
import main.java.org.htwg.konstanz.metaproject.communication.values.CommStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

/**
 * This is a communication object of type {@link CommReactionType#AGREE_REJECT}
 * and has an answer status with answer methods. Every communication of this
 * type has to extend this class.
 *
 * @author SiKelle
 */
@Entity
public abstract class CommAgreeRejectAbstract extends CommAbstract {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(CommAgreeRejectAbstract.class);

    /**
     * The answer status for this communication object. Default is
     * {@link CommAnswer#NOT_ANSWERED}.
     */
    @NotNull(message = "answer is null")
    @Enumerated(EnumType.ORDINAL)
    private CommAnswer answer = CommAnswer.NOT_ANSWERED;

    @Override
    public CommReactionType getReactionType() {
        return CommReactionType.AGREE_REJECT;
    }

    public String paramAnswer() {
        return answer.toString();
    }

    /**
     * This method returns the answer/status of a communication object.
     *
     * @return
     */
    public CommAnswer getAnswer() {
        return this.answer;
    }

    /**
     * Check whether the answer is currently allowed on this communication
     * object.
     *
     * @param commAnswer
     * @return
     */
    public boolean isValidAnswer(CommAnswer commAnswer) {
        // check whether communication object has the right state for this
        // action
        if (!this.getStatus().equals(CommStatus.UNTOUCHED)) {
            log.error("Communication object is already answered {}.", getStatus());
            return false;
        }
        // check whether answer is allowed
        if (!CommAnswer.NEGATIVE.equals(commAnswer) && !CommAnswer.POSITIVE.equals(commAnswer)) {
            log.error("This answer isn't allowed on this communication object.");
            return false;
        }
        return true;
    }

    /**
     * This method answers the request with given {@link CommAnswer}.
     *
     * @param commAnswer
     * @return
     */
    public void setAnswer(CommAnswer commAnswer) {
        // check whether communication object has the right state for this
        // action
        if (!isValidAnswer(commAnswer)) {
            throw new IllegalArgumentException("Invalid answer.");
        }
        // set answer
        this.answer = commAnswer;
        // update communication status
        this.setStatus(CommStatus.FINISHED);
    }

}
