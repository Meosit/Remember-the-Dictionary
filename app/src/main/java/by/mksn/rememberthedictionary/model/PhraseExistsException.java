package by.mksn.rememberthedictionary.model;

public class PhraseExistsException extends Exception {

    public PhraseExistsException() {
    }

    public PhraseExistsException(String message) {
        super(message);
    }

    public PhraseExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhraseExistsException(Throwable cause) {
        super(cause);
    }
}
