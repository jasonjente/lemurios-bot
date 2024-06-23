package bot.application.exceptions;

public class YoutubeSearchException extends Exception {
    public YoutubeSearchException(final String message, final Exception e) {
        super(message, e);
    }
}
