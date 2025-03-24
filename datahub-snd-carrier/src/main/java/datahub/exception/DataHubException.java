package datahub.exception;

/**
 * 独自例外
 *  
 */
public class DataHubException extends RuntimeException {
	
	/**
	 * 原因無し独自例外
	 * 
	 */
	public DataHubException(String message) {
		super(message);
	}
	
	/**
	 * 原因付き独自例外
	 * 
	 */
	public DataHubException(String message, Throwable cause) {
		super(message, cause);
	}

}
