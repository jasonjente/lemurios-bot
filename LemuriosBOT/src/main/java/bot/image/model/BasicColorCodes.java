package bot.image.model;

/**
 * Enum holding basic color codes for easy development.
 */
public enum BasicColorCodes {
    BLACK(0x000000),
    WHITE(0xFFFFFF),
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF),
    YELLOW(0xFFFF00),
    CYAN(0x00FFFF),
    MAGENTA(0xFF00FF),
    PURPLE(0x800080);


    private final int code;

    BasicColorCodes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}