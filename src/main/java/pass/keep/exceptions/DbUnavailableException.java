package pass.keep.exceptions;

public class DbUnavailableException extends Exception {

    public DbUnavailableException(String message) {
        super(message);
    }

    public DbUnavailableException(Throwable cause) {
        super(cause);
    }
}
