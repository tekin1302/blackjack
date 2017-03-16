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

    public Card() {
    }

    public Card(int value, String label, boolean isAce) {
        this.value = value;
        this.label = label;
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

    @Override
    public String toString() {
        return isFaceDown() ? CardSymbols.FACE_DOWN : label;
    }
}
