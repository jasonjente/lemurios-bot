package bot.constants;


public enum Constants {
    /**
     * MESSAGES
     */
    ASSEMBLEMURS_MESSAGE(", Assemble! it's play time. "),
    SORRY_FOR_SPAM_MESSAGE(" Sorry for any spam this is a testing session"),
    INVITE_MESSAGE(", has invited you to play. Go join them ASAP."),
    HELP_COMMENT(" I will be your guide. Find below our commands :)"),
    HELLO("Hello "),
    GTFO_MESSAGE ("NOW GTFO HERE! \n With best regards, from the Lemurs Management."),
    SORRY_MSG  ("We are sowwy :("),


    /**
     * DIRECTORIES
     */
    DATA_IN_DIR("LemuriosBOT/images"),
    IMAGE_DETECTION_IMAGE_IN_DIR("LemuriosBOT/detection/in"),
    IMAGE_DETECTION_IMAGE_OUT_DIR("LemuriosBOT/detection/out");

    private final String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
