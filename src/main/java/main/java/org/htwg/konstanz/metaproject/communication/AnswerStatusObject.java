package main.java.org.htwg.konstanz.metaproject.communication;

/**
 * This object stores the success or error message for a communication process.
 * 
 * @author SiKelle
 *
 */
public class AnswerStatusObject {

	private ErrorCode errorCode;

	private AnswerStatusObject(ErrorCode errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public static AnswerStatusObject getInstance(ErrorCode errorCode) {
		return new AnswerStatusObject(errorCode);
	}

}
