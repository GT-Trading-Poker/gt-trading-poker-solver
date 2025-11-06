package org.poker.CFR.History;

import org.poker.Card;

import java.util.ArrayList;

public abstract class AbstractHistory {

    protected ArrayList<String> actions;
    protected int currentPlayer;

    public AbstractHistory() {
        actions = new ArrayList<>();
        currentPlayer = 0;
    }

    public AbstractHistory(ArrayList<String> actions, int currentPlayer) {
        this.actions = new ArrayList<>(actions);
        this.currentPlayer = currentPlayer;
    }

    /**
     * Adds an action to the history.
     * Automatically switches players only if it's a player action.
     * (Does NOT switch after "Deal" or other chance actions.)
     */
    public void addAction(String action) {
        actions.add(action);

        // Only alternate if this is not a chance ("Deal") action
        if (!action.startsWith("Deal")) {
            currentPlayer = 1 - currentPlayer;
        }
    }

    public ArrayList<String> getActions() {
        return actions;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Allows explicit control of which player acts next.
     * Useful for resetting after chance events.
     */
    public void setCurrentPlayer(int player) {
        this.currentPlayer = player;
    }

    // Abstract methods for game-specific logic
    public abstract boolean isTerminal();
    public abstract double terminalUtility(int player);
    public abstract String infoSetKey(int player);
    public abstract AbstractHistory copy();
    public abstract void addCard(int player, Card card);
    public abstract ArrayList<Card> getHand(int player);
}
