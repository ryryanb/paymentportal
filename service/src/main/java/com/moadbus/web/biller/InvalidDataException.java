package web.biller;

public class InvalidDataException extends Exception {

	private static final long serialVersionUID = -3746136511681199673L;
	public InvalidDataException(String message) {
		super(message);
	}
}
