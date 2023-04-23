package bot.music.exceptions;

public class UserNotInAudioChannel extends RuntimeException {
    public UserNotInAudioChannel(String error_msg) {
        super(error_msg);
    }
}
