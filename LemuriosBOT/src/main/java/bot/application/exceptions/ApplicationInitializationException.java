package bot.application.exceptions;

/**
 * Base Exception class for fails during the application initialization.
 * For example, it is used by the properties util class in case of a fail on startup.
 */
public class ApplicationInitializationException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ApplicationInitializationException(String message) {
        super(message);
    }

    public ApplicationInitializationException(Throwable cause) {
        super(cause);
    }

}
