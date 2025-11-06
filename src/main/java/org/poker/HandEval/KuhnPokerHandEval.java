package org.poker.HandEval;

import java.util.ArrayList;
import org.poker.Card;
import org.poker.CFR.History.AbstractHistory;
import org.poker.CFR.History.KuhnPokerHistory;

/**
 * Kuhn Poker–specific hand evaluator.
 */
public class KuhnPokerHandEval extends HandEval {

    @Override
    public int compareHands(ArrayList<Card> a, ArrayList<Card> b) {
        if (a.isEmpty() || b.isEmpty()) {
            throw new IllegalArgumentException("Hands must each contain one card.");
        }
        Card c1 = a.get(0);
        Card c2 = b.get(0);
        int r1 = c1.getRank();
        int r2 = c2.getRank();
        return Integer.compare(r1, r2);
    }
}
