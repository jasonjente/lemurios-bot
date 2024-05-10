package bot.application.exceptions;

public class YoutubeSearchException extends Exception {
    public YoutubeSearchException(final String message, final Throwable e) {
        super(message, e);
    }
}
