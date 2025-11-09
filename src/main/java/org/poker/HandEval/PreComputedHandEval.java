package org.poker.HandEval;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.poker.Card;

public class PreComputedHandEval extends HandEval {
    private final Map<String, Integer> rankTable;

    public PreComputedHandEval(Map<String, Integer> rankTable) {
        this.rankTable = rankTable;
    }

    @Override
    public int compareHands(ArrayList<Card> a, ArrayList<Card> b) {
        int ra = strength(a);
        int rb = strength(b);
        return Integer.compare(ra, rb);
    }

    private int strength(ArrayList<Card> hand) {
        Integer v = rankTable.get(key(hand));
        if (v == null) {
            throw new IllegalStateException("No precomputed rank for hand key: " + key(hand));
        }
        return v;
    }
    
    private String key(ArrayList<Card> hand) {
        List<String> parts = new ArrayList<>(hand.size());
        for (Card c : hand) {
            parts.add(c.getRank() + String.valueOf(c.getSuite()));
        }
        parts.sort((x, y) -> {
            int rx = Integer.parseInt(x.substring(0, x.length() - 1));
            int ry = Integer.parseInt(y.substring(0, y.length() - 1));
            if (rx != ry) return Integer.compare(rx, ry);
            char sx = x.charAt(x.length() - 1);
            char sy = y.charAt(y.length() - 1);
            return Character.compare(sx, sy);
        });
        return String.join("-", parts);
    }
}
