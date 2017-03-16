package ro.blackjack;

/**
 * An interface with some symbol types.
 */
public enum CardSymbols {
    HEARTS(String.valueOf((char)'\u2764'), "H"),
    SPADES(String.valueOf((char)'\u2660'), "S"),
    CLUBS(String.valueOf((char)'\u2663'), "C"),
    DIAMONDS(String.valueOf((char)'\u2666'), "D"),
    FACE_DOWN(String.valueOf((char)'\u25A1'), "F");

    private String icon;
    private String code;

    CardSymbols(String icon, String code) {
        this.icon = icon;
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public String getCode() {
        return code;
    }
}
