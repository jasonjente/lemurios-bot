package bot.constants;

public enum Commands {

    /**
     * BOT COMMANDS / Points earned per command
     */
    ASSEMLEMURS_COMMAND("assemblemurs", 5),
    CREDITS_COMMAND("credits", 2),
    HELP_COMMAND("help", 1),
    PLAY_COMMAND("play", 2),
    PLAY_RADIO("play-radio", 1),
    SET_RADIO("set-radio-url", 1),
    GET_RADIO("get-radio-urls", 1),
    DELETE_ALL("delete-all", 1),
    DELETE_GENRE("delete-genre", 1),
    PAUSE_COMMAND("pause", 1),
    RESUME_COMMAND("resume", 1),
    SKIP_COMMAND("skip", 1),
    STOP_COMMAND("stop", 1),
    JOIN_COMMAND("join", 1),
    NOW_PLAYING("now-playing", 1),
    MEME_COMMAND("meme", 2),
    HISTORY_COMMAND("history", 1),
    TAKEN_NAMES("taken-names", 2),
    UPLOAD_MEME_COMMAND("upload", 5),
    UPLOAD_BATCH_MEMES_COMMAND("upload-batch", 15),
    DETECT_IMAGE_EDGES_COMMAND("detect-edges", 2),
    DISCONNECT_COMMAND("disconnect", 2),
    LEADERBOARD_COMMAND("leaderboard", 0),
    SCHEDULE_COMMAND("schedule", 5),
    INVITE_LINK("invite", 0);

    private final String commandName;
    private final int points;

    Commands(String commandName, int points) {
        this.commandName = commandName;
        this.points = points;
    }

    public String getCommandName() {
        return commandName;
    }

    public int getPoints() {
        return points;
    }
}
