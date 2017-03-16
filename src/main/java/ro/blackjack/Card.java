package ro.blackjack;

/**
 * This class represent a playing card.
 */

public class Card {

    private int value;

    // Something like: K♠, 9♥, A♣ etc.
    private String label;

    private boolean faceDown;
    private boolean isAce;
    private String code;

    public Card() {
    }

    public Card(int value, String label, String code, boolean isAce) {
        this.value = value;
        this.label = label;
        this.code = code;
        this.isAce = isAce;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isFaceDown() {
        return faceDown;
    }

    public void setFaceDown(boolean faceDown) {
        this.faceDown = faceDown;
    }

    public boolean isAce() {
        return isAce;
    }

    public void setAce(boolean ace) {
        isAce = ace;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return isFaceDown() ? CardSymbols.FACE_DOWN.getIcon() : label;
    }
}
