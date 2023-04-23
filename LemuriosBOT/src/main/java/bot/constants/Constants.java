package bot.constants;


public enum Constants {
    /**
     * MESSAGES
     */
    ASSEMBLEMURS_MESSAGE(", Assemble! it's play time. @"),
    SORRY_FOR_SPAM_MESSAGE(" Sorry for any spam this is a testing session"),
    ASSEMLEMURS_COMMAND("!assemblemurs"),
    INVITE_MESSAGE(", has invited you to play. Go join them ASAP."),
    HELP_COMMENT(" I will be your guide. Find below our commands :)"),
    HELLO("Hello "),
    GTFO_MESSAGE ("NOW GTFO HERE! \n With best regards, from the Lemurs Management."),
    SORRY_MSG  ("We are sowwy :("),


    /**
     * BOT COMMANDS
     */
    HELP_COMMAND("!help"),
    CREDITS_COMMAND("!credits"),
    PLAY_COMMAND("!play"),
    PAUSE_COMMAND("!pause"),
    SKIP_COMMAND("!skip"),
    STOP_COMMAND("!stop"),
    MEME_COMMAND("!meme"),
    HISTORY_COMMAND("!history"),
    KICK_STAM_COMMAND("!kickStam"),
    AVAILABLE_NAMES("!available-names"),
    UPLOAD_MEME_COMMAND("!upload"),
    DETECT_IMAGE_EDGES("!detect-edges"),
    /**
     * DIRECTORIES
     */
    DATA_IN_DIR("images"),
    IMAGE_DETECTION_IMAGE_IN_DIR("detection/in"),
    IMAGE_DETECTION_IMAGE_OUT_DIR("detection/out");

    private final String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
