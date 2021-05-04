package musichub.business.exception;

import java.lang.Exception;

public class NoElementFoundException extends Exception {

	public NoElementFoundException (String msg) {
		super(msg);
	}
}