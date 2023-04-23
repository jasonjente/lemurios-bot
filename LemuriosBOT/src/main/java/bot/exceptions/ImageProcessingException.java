package bot.exceptions;

public class ImageProcessingException extends Exception{
    public ImageProcessingException() {
    }

    public ImageProcessingException(String message) {
        super(message);
    }

    public ImageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
