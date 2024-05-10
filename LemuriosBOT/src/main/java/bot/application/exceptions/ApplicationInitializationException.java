package bot.application.exceptions;

/**
 * Base Exception class for fails during the application initialization.
 * For example, it is used by the properties util class in case of a fail on startup.
 */
public class ApplicationInitializationException extends Exception{

    public ApplicationInitializationException(Throwable cause) {
        super(cause);
    }

}
