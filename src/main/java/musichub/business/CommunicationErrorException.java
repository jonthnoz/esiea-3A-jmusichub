package musichub.business;

import java.lang.Exception;

public class CommunicationErrorException extends Exception {
	
	public CommunicationErrorException (String msg) {
		super(msg);
	}
}
