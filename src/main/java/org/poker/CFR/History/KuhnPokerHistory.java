package org.poker.CFR.History;

import java.util.ArrayList;
import org.poker.Card;
import org.poker.HandEval.KuhnPokerHandEval;

public class KuhnPokerHistory extends AbstractHistory {
    private final ArrayList<ArrayList<Card>> hands;

    public KuhnPokerHistory() {
        super();
        hands = new ArrayList<>();
        hands.add(new ArrayList<>()); // Player 0
        hands.add(new ArrayList<>()); // Player 1
    }

    @Override
    public void addCard(int player, Card card) {
        hands.get(player).add(card);
    }

    @Override
    public ArrayList<Card> getHand(int player) {
        return hands.get(player);
    }

    @Override
    public boolean isTerminal() {
        // Check for fold or call
        for (String action : actions) {
            if (action.endsWith("Fold") || action.endsWith("Call")) {
                return true;
            }
        }
        
        // Check if both players have checked consecutively (after dealing)
        // This means we need at least 2 non-Deal actions, and they should both be Check
        int playerActionCount = 0;
        for (String action : actions) {
            if (!action.startsWith("Deal")) {
                playerActionCount++;
            }
        }
        // If both players have acted (checked), game ends in showdown
        if (playerActionCount >= 2) {
            // Check if last two non-deal actions are both checks
            int checkCount = 0;
            for (int i = actions.size() - 1; i >= 0; i--) {
                String action = actions.get(i);
                if (!action.startsWith("Deal")) {
                    if (action.endsWith("Check")) {
                        checkCount++;
                        if (checkCount == 2) {
                            return true; // Both players checked, showdown
                        }
                    } else {
                        break; // Found a non-check action, not terminal from checks
                    }
                }
            }
        }
        
        return false;
    }

    @Override
    public double terminalUtility(int player) {
        KuhnPokerHandEval eval = new KuhnPokerHandEval();
        ArrayList<Double> utils = eval.utilityFromHistory(this);
        return utils.get(player);
    }

    @Override
    public String infoSetKey(int player) {
        StringBuilder sb = new StringBuilder();

        // Player label and their private card
        sb.append("P").append(player).append(":");
        if (!hands.get(player).isEmpty()) {
            sb.append(hands.get(player).get(0).getRank());
        }

        // Add only public actions (no deals)
        sb.append("|");
        for (String action : actions) {
            if (action.startsWith("Deal")) continue; // hide private info
            sb.append(action.replace("P0:", "").replace("P1:", "")).append(",");
        }

        // Remove trailing comma if any
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    @Override
    public AbstractHistory copy() {
        KuhnPokerHistory newHist = new KuhnPokerHistory();
        newHist.actions = new ArrayList<>(this.actions);
        newHist.currentPlayer = this.currentPlayer;

        for (int i = 0; i < hands.size(); i++) {
            newHist.hands.get(i).addAll(this.hands.get(i));
        }

        return newHist;
    }
}
