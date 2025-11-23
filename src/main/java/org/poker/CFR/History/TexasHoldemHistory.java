package org.poker.CFR.History;

import org.poker.Card;

import static org.poker.CFR.History.BettingRound.*;

import java.util.ArrayList;

public class TexasHoldemHistory extends AbstractHistory {

    private ArrayList<Card> communityCards;
    /**
     * initialize the history class
     */
    public TexasHoldemHistory() {
        super();
        communityCards = new ArrayList<>();
    }

    /**
     * add an action to the history
     * TODO: implement using the action class
     */
    public void addAction(String action) {
        super.addAction(action);
    }

    /**
     * determines from the history whether the game has ended
     * TODO: implement using the betting rounds and the actions, use the Action class
     */
    @Override
    public boolean isTerminal() {
        return false;
    }

    /**
     * TODO: implement
     */
    @Override
    public double terminalUtility(int player) {
        return 0;
    }

    /**
     * TODO: implement
     */
    @Override
    public String infoSetKey(int player) {
        return "";
    }

    /**
     * TODO: implement
     */
    @Override
    public AbstractHistory copy() {
        return null;
    }

    /**
     * TODO: implement
     */
    @Override
    public void addCard(int player, Card card) {

    }

    /**
     * TODO: implement
     */
    @Override
    public ArrayList<Card> getHand(int player) {
        return null;
    }

    /**
     * return the current betting round
     * TODO: implement, use the enums
     */
    public BettingRound getRound() {
        return PRE_FLOP;
    }

    /**
     * get the community cards in the game
     * TODO: implement
     */
    public ArrayList<Card> community() {
        return communityCards;
    }

}
