package by.mksn.rememberthedictionary.util;

public class CannotReadFileException extends Exception {

    public CannotReadFileException() {
        super();
    }

    public CannotReadFileException(String message) {
        super(message);
    }

    public CannotReadFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotReadFileException(Throwable cause) {
        super(cause);
    }
}
