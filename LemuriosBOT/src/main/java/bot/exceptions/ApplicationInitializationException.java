package bot.exceptions;

/**
 * Base Exception class for fails during the application initialization.
 * For example, it is used by the properties util class in case of a fail on startup.
 */
public class ApplicationInitializationException extends RuntimeException{


    public ApplicationInitializationException() {
    }

    public ApplicationInitializationException(String message) {
        super(message);
    }

    public ApplicationInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationInitializationException(Throwable cause) {
        super(cause);
    }

    public ApplicationInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
