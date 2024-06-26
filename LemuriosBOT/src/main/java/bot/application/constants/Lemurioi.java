package bot.application.constants;

public enum Lemurioi {
    LEMURIOS_000("Lemurios 000"),
    LEMURIOS_001("Lemurios 001"),
    LEMURIOS_002("Lemurios 002"),
    LEMURIOS_003("Lemurios 003"),
    LEMURIOS_004("Lemurios 004"),
    LEMURIOS_005("Lemurios 005"),
    LEMURIOS_007("Lemurios 007"),
    LEMURIOS_010("Lemurios 010"),
    LEMURIOS_021("Lemurios 021"),
    LEMURIOS_298("Lemurios 298"),
    LEMURIOS_362("Lemurios 362"),
    LEMURIOS_626("Lemurios 626"),
    LEMURIOS_666("Lemurios 666");

    private final String value;

    Lemurioi(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    public static String usedNames() {
        var sb = new StringBuilder();
        var lemurConstants = Lemurioi.class.getEnumConstants();
        for (var lemurName : lemurConstants) {
            sb.append(lemurName.getValue()).append("\n");
        }
        return sb.toString();
    }

}
