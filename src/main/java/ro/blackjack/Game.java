package ro.blackjack;

import java.util.*;

import static java.lang.System.out;
import static ro.blackjack.CardSymbols.*;
import static ro.blackjack.PlayerType.DEALER;
import static ro.blackjack.PlayerType.PLAYER;

/**
 * The main class for this BlackJack game
 */
public class Game {

    public static final CardSymbols[] SUITS = new CardSymbols[] {HEARTS, SPADES, CLUBS, DIAMONDS};

    public static final int GOAL = 21;
    public static final int DEALER_HIT_THRESHOLD = 17;

    private List<Card> cardsPack;
    private List<Card> playerCards;
    private List<Card> dealerCards;
    private boolean isPlayerTurn;

    private int dealerScore;
    private int playerScore;

    public static void main(String[] args) {

        Game game = new Game();
        game.init();
        game.startInConsole();
    }

    /**
     * Initialize the game data
     */
    public void init() {
        createCardsPack();
        shufflePack();
        initFirstRound();
    }

    /**
     * Start the console game
     */
    private void startInConsole() {
        printKeyboardCommands();
        showCardsOnTable();

        // check if first round is decisive
        boolean gameOver = isGameOver(true);
        if (gameOver) return;

        try (Scanner scanner = new Scanner(System.in)) {
            isPlayerTurn = true;

            while (isPlayerTurn && !gameOver) {
                String command = scanner.nextLine();
                boolean validCommand = executeCommand(command);

                if (validCommand) {
                    showCardsOnTable();
                    gameOver = isGameOver(false);
                }
            }
        }

        // it's the dealers turn
        makeDealerMoves(gameOver);
    }

    public void makeDealerMoves(boolean gameOver) {
        while (!gameOver) {
            boolean hitSuccess = false;

            if (canAskForCard() && decideIfShouldHit()) {
                hitMe();
                hitSuccess = true;
            }

            if (hitSuccess) {
                showCardsOnTable();
                gameOver = isGameOver(false);
            } else {
                gameOver = true;
                decideWinner();
            }
        }
    }

    /**
     * A simple algorithm to decide if should get another card
     * @return The decision whether to do it or not
     */
    public boolean decideIfShouldHit() {
        int bigCards = countBigCards(playerCards) + countBigCards(dealerCards);
        int totalNrOfCards = playerCards.size() + dealerCards.size();

        if (dealerScore < playerScore) {
            // dealer has less points; has to risk it
            return true;
        } else if (dealerScore > playerScore) {
            // winning, no need for a hit
            return false;
        } else {
            // if there are too many big cards on the table, don't risk it; go for a tie
            return bigCards > totalNrOfCards / 2;
        }
    }

    /**
     * Count cards that are big (10 points)
     * @param cards
     * @return
     */
    private int countBigCards(List<Card> cards) {
        int bigCards = 0;
        for (Card card : cards) {
            if (card.getValue() == 10) {
                bigCards++;
            }
        }
        return bigCards;
    }

    /**
     * Search of a winner
     */
    private void decideWinner() {
        if (playerScore > dealerScore) {
            declarePlayerVictory();
        } else if (dealerScore > playerScore) {
            declarePlayerDefeat();
        } else {
            declareTie();
        }
    }

    /**
     * Calculates the scores and decide if the game is over
     * @param firstRound If this is true, then the scores for both players are calculated
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver(boolean firstRound) {
        boolean gameOver;
        if (firstRound) {
            calculateScore(PLAYER);
            calculateScore(DEALER);
        } else if (isPlayerTurn) {
            calculateScore(PLAYER);
        } else {
            calculateScore(DEALER);
        }

        gameOver = checkScores();
        return gameOver;
    }

    /**
     * Execute the command given by the user in console
     * @param command
     * @return true if the command was valid (and the corresponding action was done), false otherwise
     */
    private boolean executeCommand(String command) {
        switch (command) {
            case "h":
                hitMe();
                break;
            case "f":
                finishUserTurn();
                break;
            default: {
                // not a valid command
                out.println("Invalid command!");
                return false;
            }
        }
        return true;
    }

    /**
     * Marks the moment when the user gives up his turn
     */
    public void finishUserTurn() {
        isPlayerTurn = false;
        out.println("It's the dealers turn");
    }

    /**
     * Receive a card from the pack
     */
    public void hitMe() {
        List<Card> cards = isPlayerTurn ? playerCards : dealerCards;
        cards.add(cardsPack.remove(cardsPack.size() - 1));
    }

    /**
     * Check if the dealer can as for a card (according to the rules)
     * @return true if the dealer can draw a card, false otherwise
     */
    private boolean canAskForCard() {

        // does not apply for the player, only the dealer
        if (isPlayerTurn) return true;

        int sum = 0;

        for (Card card : dealerCards) {
            sum += card.isAce() ? 1 : card.getValue(); // in this case Ace 1 one
        }
        return sum < DEALER_HIT_THRESHOLD;
    }

    /**
     * Check if the game is over or not and print an appropriate message to the console
     * @return true if the game is over, false if not
     */
    private boolean checkScores() {
        if (playerScore == dealerScore && playerScore == GOAL) {
            declareTie();
            return true;
        } else if (dealerScore > GOAL || playerScore == GOAL){
            declarePlayerVictory();
            return true;
        } else if (playerScore > GOAL || dealerScore == GOAL){
            declarePlayerDefeat();
            return true;
        }
        return false;
    }

    private void turnDealerCardAndShow() {
        dealerCards.get(0).setFaceDown(false);
        showCardsOnTable();
    }

    private void declareTie() {
        turnDealerCardAndShow();
        printScores();
        out.println("\nIt's a tie!");
    }
    private void declarePlayerVictory() {
        turnDealerCardAndShow();
        printScores();
        out.println("\nYou won!");
    }
    private void declarePlayerDefeat() {
        turnDealerCardAndShow();
        printScores();
        out.println("\nYou lost! Game Over!");
    }

    private void printScores() {
        out.print("\n");
        out.println("Dealer score: " + dealerScore);
        out.println("Player score: " + playerScore);
    }

    /**
     * Calculate the score and get a result
     * @param playerType
     * @return
     */
    private void calculateScore(PlayerType playerType) {

        List<Card> cards = playerType == PLAYER ? playerCards : dealerCards;

        int sum = 0;
        int nrOfAces = 0;

        for (Card card : cards) {
            sum += card.getValue();
            if (card.isAce()) {
                nrOfAces++;
            }
        }

        if (sum > GOAL && nrOfAces > 0) {
            sum = getMaxScoreWithAces(cards, nrOfAces);
        }

        if (playerType == PLAYER) {
            playerScore = sum;
        } else {
            dealerScore = sum;
        }
    }

    private void printKeyboardCommands() {
        out.println("Press 'h' to get a card (Hit) or 'f' to finish.\n");
    }

    private void showCardsOnTable() {
        out.println("___________________________________________\n");
        out.println("Dealer:");
        out.println(cardsToString(dealerCards));

        out.println();

        out.println("You:");
        out.println(cardsToString(playerCards));
    }

    /**
     * @param cards
     * @return A string representation of the cards
     */
    private String cardsToString(List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card).append(" ");
        }
        return sb.toString();
    }

    /**
     * Give the player and the dealer 2 cards each.
     * The dealer has one card face down (the hole card)
     */
    private void initFirstRound() {
        playerCards = new ArrayList<>();
        dealerCards = new ArrayList<>();

        playerCards.add(cardsPack.remove(cardsPack.size() - 1));
        playerCards.add(cardsPack.remove(cardsPack.size() - 1));

        dealerCards.add(cardsPack.remove(cardsPack.size() - 1));
        dealerCards.add(cardsPack.remove(cardsPack.size() - 1));
        dealerCards.get(0).setFaceDown(true);
    }

    /**
     * Randomly changes the order of the cards in the pack
     */
    private void shufflePack() {
        long seed = System.nanoTime();
        Collections.shuffle(cardsPack, new Random(seed));
    }

    /**
     * Creates a list of cards
     * @return The list of cards
     */
    private List<Card> createCardsPack() {
        cardsPack = new ArrayList<>();

        // for each suite
        for (int i = 0; i < SUITS.length; i++) {
            CardSymbols suit = SUITS[i];

            // add cards with face value
            for (int j = 2; j < 11; j++) {
                cardsPack.add(new Card(j, j + suit.getIcon(), j + "_" + suit.getCode(), false));
            }

            // add J, Q, K, A
            cardsPack.add(new Card(10, "J" + suit.getIcon(), "J_" + suit.getCode(), false));
            cardsPack.add(new Card(10, "Q" + suit.getIcon(), "Q_" + suit.getCode(), false));
            cardsPack.add(new Card(10, "K" + suit.getIcon(), "K_" + suit.getCode(), false));
            cardsPack.add(new Card(11, "A" + suit.getIcon(), "A_" + suit.getCode(), true));
        }

        return cardsPack;
    }

    /**
     * Calculates the max possible score for a hand that has aces
     * @param cards The cards
     * @param nrOfAces The number of aces
     * @return The max possible score for a hand that has aces
     */
    private int getMaxScoreWithAces(List<Card> cards, int nrOfAces) {
        List<boolean[]> acesCombinations = possibleAcesCombination(nrOfAces);

        int maxSum = 0;

        // try all combination of aces values (1 or 11)
        for (boolean[] acesCombination : acesCombinations) {

            int sum = 0;
            for (Card card : cards) {

                int aceIdx = 0;
                if (card.isAce()) {
                    // consult the boolean array to check which value should be added
                    sum += acesCombination[aceIdx] ?  11 : 1;
                    aceIdx++;
                } else {
                    sum += card.getValue();
                }
            }
            if (sum > maxSum) maxSum = sum;
        }
        return maxSum;
    }

    /**
     * Returns a list of boolean arrays that represent all the possible combinations of n booleans
     * @param n
     * @return
     */
    private List<boolean[]> possibleAcesCombination(int n) {
        List<boolean[]> result = new ArrayList<>();

        for (int i = 0; i < Math.pow(2, n); i++) {
            String bin = Integer.toBinaryString(i);
            while (bin.length() < n)
                bin = "0" + bin;
            char[] chars = bin.toCharArray();
            boolean[] boolArray = new boolean[n];
            for (int j = 0; j < chars.length; j++) {
                boolArray[j] = chars[j] == '0' ? true : false;
            }
            result.add(boolArray);
        }
        return result;
    }
    public List<Card> getCardsPack() {
        return cardsPack;
    }

    public List<Card> getPlayerCards() {
        return playerCards;
    }

    public List<Card> getDealerCards() {
        return dealerCards;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public int getDealerScore() {
        return dealerScore;
    }

    public int getPlayerScore() {
        return playerScore;
    }
}
