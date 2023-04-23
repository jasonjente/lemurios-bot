package bot.constants;

public enum Lemurioi {
    LEMURIOS_000("Lemurios 000"),
    LEMURIOS_001("Lemurios 001"),
    LEMURIOS_002("Lemurios 002"),
    LEMURIOS_003("Lemurios 003"),
    LEMURIOS_004("Lemurios 004"),
    LEMURIOS_005("Lemurios 005"),
    LEMURIOS_007("Lemurios 007"),
    LEMURIOS_666("Lemurios 626"),
    LEMURIOS_626("Lemurios 666");

    private final String value;

    Lemurioi(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    public static String usedNames() {
        StringBuilder sb = new StringBuilder();

        sb.append("Lemurios 000")
          .append("\n Lemurios 001")
          .append("\n Lemurios 002")
          .append("\n Lemurios 003")
          .append("\n Lemurios 004")
          .append("\n Lemurios 005")
          .append("\n Lemurios 007")
          .append("\n Lemurios 626")
          .append("\n Lemurios 666");
        return sb.toString();

    }
}
