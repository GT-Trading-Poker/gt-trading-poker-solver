package org.poker.CFR.History;

import org.poker.Card;

import java.util.ArrayList;

public class TexasHoldemHistory extends AbstractHistory {

    /**
     * initialize the history class
     */
    public TexasHoldemHistory() {
        super();
    }

    public void addAction() {

    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public double terminalUtility(int player) {
        return 0;
    }

    @Override
    public String infoSetKey(int player) {
        return "";
    }

    @Override
    public AbstractHistory copy() {
        return null;
    }

    @Override
    public void addCard(int player, Card card) {

    }

    @Override
    public ArrayList<Card> getHand(int player) {
        return null;
    }
}
