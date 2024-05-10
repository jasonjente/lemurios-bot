package bot.application.constants;


public enum Constants {
    /**
     * This enum contains some values that are being used by the bot.
     */
    ASSEMBLEMURS_MESSAGE(", Assemble! it's play time. "),
    INVITE_MESSAGE(", has invited you to play. Go join them ASAP."),
    HELP_COMMENT(" I will be your guide. Find below our commands :)"),
    HELLO("Hello "),
    GTFO_MESSAGE ("NOW GTFO HERE! \n With best regards, from the Lemurs Management."),
    SORRY_MSG  ("We are sowwy :(");

    private final String value;

    Constants(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
