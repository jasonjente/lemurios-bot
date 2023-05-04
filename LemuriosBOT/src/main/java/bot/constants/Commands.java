package bot.constants;

public enum Commands {

    /**
     * BOT COMMANDS
     */
    ASSEMLEMURS_COMMAND("assemblemurs"),
    CREDITS_COMMAND("credits"),
    HELP_COMMAND("help"),
    PLAY_COMMAND("play"),
    PAUSE_COMMAND("pause"),
    RESUME_COMMAND("resume"),
    SKIP_COMMAND("skip"),
    STOP_COMMAND("stop"),
    JOIN_COMMAND("join"),
    NOW_PLAYING("now-playing"),
    MEME_COMMAND("meme"),
    HISTORY_COMMAND("history"),
    TAKEN_NAMES("taken-names"),
    UPLOAD_MEME_COMMAND("upload"),
    DETECT_IMAGE_EDGES_COMMAND("detect-edges"),
    DISCONNECT_COMMAND("disconnect");

    private final String value;

    Commands(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
