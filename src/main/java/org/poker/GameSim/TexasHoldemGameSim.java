package org.poker.GameSim;

import org.poker.Card;
import org.poker.CFR.History.AbstractHistory;
import org.poker.CFR.History.KuhnPokerHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TexasHoldemGameSim extends GameSim {

    private ArrayList<Card> deck;
    private Random rng = new Random();

    public TexasHoldemGameSim() {
        resetDeck();
    }

    /**
     * show the current player
     * @param history the game history object
     * @return the current player's number
     */
    @Override
    public int currentPlayer(AbstractHistory history) {
        // leaving history for CFR people to implement
        return history.getCurrentPlayer();
    }

    /**
     * @param history the game history object to write to
     * @return a list of available actions
     */
    @Override
    public ArrayList<String> getAvailableActions(AbstractHistory history) {
        ArrayList<String> actions = history.getActions();
        ArrayList<String> available = new ArrayList<>();
        if (history.isTerminal()) return available;

        String last = null;
        for (int i = actions.size() - 1; i >= 0; i--) {
            String newLast = actions.get(i);
            if (!newLast.startsWith("Deal")) {
                last = newLast;
            }
        }

        if (last == null || last.endsWith("Check")) {
            available.add("Check");
            available.add("Bet");
        } else {
            available.add("Call");
            available.add("Raise");
            available.add("Fold");
        }
        return available;
    }

    /**
     * deals the initial cards to the players
     * @param history the game history object to update
     */
    @Override
    public void dealInitialCards(AbstractHistory history) {
        // so that CFR team does not need to generate a bunch of games
        if (deck.size() < 52) resetDeck();

        // TODO: communicate with CFR team to add the addAction update to the addCard method itself
        Card removed;
        history.addCard(0, removed = deck.removeLast());
        history.addAction("Deal P0: " + removed.getRank());
        history.addCard(0, removed = deck.removeLast());
        history.addAction("Deal P0: " + removed.getRank());
        history.addCard(1, removed = deck.removeLast());
        history.addAction("Deal P1: " + removed.getRank());
        history.addCard(1, removed = deck.removeLast());
        history.addAction("Deal P1: " + removed.getRank());
        history.setCurrentPlayer(0);
    }

    /**
     * @return whether the current game has ended
     */
    @Override
    public boolean isTerminal(AbstractHistory history) {
        return history.isTerminal();
    }

    /**
     * TODO: javadocs
     *
     *
     */
    @Override
    public double[] terminalUtility(AbstractHistory history) {
        ArrayList<Double> utils = handEval.utilityFromHistory(history);
        double[] result = new double[utils.size()];
        for (int i = 0; i < utils.size(); i++) {
            result[i] = utils.get(i);
        }
        return result;
    }

    /**
     * shows total player count
     * @return the number of players in the game
     */
    @Override
    public int numPlayers() {
        return 2;
    }

    /**
     * generates histories containing all combinations of intial deals for 2 players
     * @return an ArrayList of said histories
     * TODO: once it is implemented, use TexasHoldemHistory instead of KuhnPokerHistory
     */
    @Override
    public ArrayList<AbstractHistory> generateAllDeals(AbstractHistory history) {
        ArrayList<AbstractHistory> deals = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        loadSuit('H', cards);
        loadSuit('C', cards);
        loadSuit('S', cards);
        loadSuit('D', cards);

        int N = cards.size();

        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                for (int k = j + 1; k < N; k++) {
                    for (int l = k + 1; l < N; l++) {
                        Card c1 = cards.get(i);
                        Card c2 = cards.get(j);
                        Card c3 = cards.get(k);
                        Card c4 = cards.get(l);

                        deals.addAll(allDealsHelper(c1, c2, c3, c4));
                    }
                }
            }
        }

        return deals;
    }

    private ArrayList<AbstractHistory> allDealsHelper(Card c1, Card c2, Card c3, Card c4) {

        // chagne to the correct history when it is implemented
        AbstractHistory h1 = new KuhnPokerHistory();
        AbstractHistory h2 = new KuhnPokerHistory();
        AbstractHistory h3 = new KuhnPokerHistory();
        AbstractHistory h4 = new KuhnPokerHistory();
        AbstractHistory h5 = new KuhnPokerHistory();
        AbstractHistory h6 = new KuhnPokerHistory();

        ArrayList<AbstractHistory> deals = new ArrayList<>();
        // h1
        deal(0, c1, c2, h1);
        deal(1, c3, c4, h1);
        // h2
        deal(0, c1, c3, h2);
        deal(1, c2, c4, h2);
        // h3
        deal(0, c1, c4, h3);
        deal(1, c2, c3, h3);
        // h4
        deal(0, c2, c3, h4);
        deal(1, c1, c4, h4);
        // h5
        deal(0, c2, c4, h5);
        deal(1, c1, c3, h5);
        // h6
        deal(0, c3, c4, h6);
        deal(1, c1, c2, h6);

        deals.add(h1);
        deals.add(h2);
        deals.add(h3);
        deals.add(h4);
        deals.add(h5);
        deals.add(h6);

        return deals;
    }

    private void deal(int player, Card c1, Card c2, AbstractHistory history) {
        history.addCard(player, c1);
        history.addAction(String.format("Deal P%d: %d", player, c1.getRank()));
        history.addCard(player, c2);
        history.addAction(String.format("Deal P%d: %d", player, c2.getRank()));
    }

    /**
     * TODO: javadocs
     *
     *
     */
    @Override
    public AbstractHistory randomDeal(AbstractHistory history) {
        AbstractHistory copy = history.copy();

        // if pre-flop, issue 3 cards
        String currentRound = ""; // can get this from history TODO: Implement
        if (currentRound.equals("pre-flop")) {
            Card c1 = deck.removeLast();
            Card c2 = deck.removeLast();
            Card c3 = deck.removeLast();
            // implement community cards in history, deal them there
            // change current round to flop
        }

        // if flop or turn, issue 1 card
        if (currentRound.equals("flop") || currentRound.equals("turn")) {
            Card c1 = deck.removeLast();
            // implement community cards in history
        }
        return copy;
    }


    /**
     * Possible addition?
     * @param history the game history object
     * @return the current betting round as a string
     */
    public String getCurrentBettingRound(AbstractHistory history) {
        //TODO Depending on Community cards
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentBettingRound'");
    }

    /**
     * generates cards 2 through Ace for a suit
     * @param suit the suit to generate cards for
     */
    private void loadSuit(char suit) {
        loadSuit(suit, deck);
    }

    private void loadSuit(char suit, ArrayList<Card> deck) {
        for (int i = 2; i <= 14; i++) {
            deck.add(new Card(Integer.toString(i), suit));
        }
    }

    /**
     * Sets a random seed for reproducible testing.
     * @param seed the seed
     */
    public void setSeed(long seed) {
        rng = new Random(seed);
    }

    /**
     * resets the Deck into its shuffled state.
     */
    private void resetDeck() {
        deck = new ArrayList<>();
        loadSuit('H'); // hearts
        loadSuit('D'); // diamonds
        loadSuit('S'); // spades
        loadSuit('C'); // clubs
        Collections.shuffle(deck, rng); //reproducible testing
    }
}
