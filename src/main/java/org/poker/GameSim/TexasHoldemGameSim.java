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
       // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'terminalUtility'");
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
     * TODO: javadocs
     *
     */
    @Override
    public ArrayList<AbstractHistory> generateAllDeals(AbstractHistory history) {
        ArrayList<AbstractHistory> deals = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        loadSuit('H', cards);
        loadSuit('C', cards);
        loadSuit('S', cards);
        loadSuit('D', cards);

        for (int i = 0; i < cards.size(); i++) {
            for (int j = 0; j < cards.size(); j++) {
                for (int k = 0; k < cards.size(); k++) {
                    for (int l = 0; l < cards.size(); l++) {
                        if (i == j) continue;
                        if (i == k) continue;
                        if (i == l) continue;
                        if (j == k) continue;
                        if (j == l) continue;
                        if (k == l) continue;

                        AbstractHistory h = new KuhnPokerHistory(); // TODO: change to proper history
                        Card card;

                        h.addCard(0, card = cards.get(i));
                        h.addAction("Deal P0: " + card.getRank());
                        h.addCard(0, card = cards.get(j));
                        h.addAction("Deal P0: " + card.getRank());

                        h.addCard(1, card = cards.get(k));
                        h.addAction("Deal P1: " + card.getRank());
                        h.addCard(1, card = cards.get(l));
                        h.addAction("Deal P1: " + card.getRank());
                        
                        deals.add(h);
                    }
                }
            }
        }

        return deals;
    }

    /**
     * TODO: javadocs
     *
     *
     */
    @Override
    public AbstractHistory randomDeal(AbstractHistory history) {
        AbstractHistory copy = history.copy();
        dealInitialCards(copy);
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
