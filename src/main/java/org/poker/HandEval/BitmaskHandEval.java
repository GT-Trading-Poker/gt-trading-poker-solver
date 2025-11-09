package org.poker.HandEval;

import org.poker.CFR.History.AbstractHistory;
import org.poker.Card;

import java.util.ArrayList;

public class BitmaskHandEval extends HandEval {
    @Override
    public int compareHands(ArrayList<Card> a, ArrayList<Card> b) {
        return 0;
    }

    @Override
    public ArrayList<Double> utilityFromHistory(AbstractHistory history) {
        return null;
    }

    // Change returns later
    public static String evaluateHand(ArrayList<Card> hand) {
        long mask = handToBitmask(hand);
        int rankMask = getRankMask(mask);
        boolean flush = hasFlush(mask);
        boolean straight = hasStraight(rankMask);

        if (flush && straight) return "Straight Flush";
        if (flush) return "Flush";
        if (straight) return "Straight";
        return "High Card";
    }

    /**
     * Converts list of cards into a bitset.
     *
     * Reading from right to left, bit 1 represents a 2 value,
     * bit 2 represents a 3 value ... and bit 13 represents an Ace
     *
     * Suits:
     * Bits 1-13: Spades
     * Bits 14-26: Hearts
     * Bits 27-39: Diamonds
     * Bits 40-52: Clubs
     *
     * @param hand Array of cards, presumably 7
     * @return Bitset represented as long
     */
    private static long handToBitmask(ArrayList<Card> hand) {
        long result = 0;
        for (Card card : hand) {
            //assuming Ace is rank 14
            long mask = 1L << ((13 * suitToNumber(card.getSuite())) + (card.getRank() - 2));
            result |= mask;
        }
        return result;
    }

    private static int suitToNumber(char suit) {
        return switch (suit) {
            case 'S' -> 0;
            case 'H' -> 1;
            case 'D' -> 2;
            case 'C' -> 3;
            default -> throw new RuntimeException("Invalid suit: " + suit);
        };
    }

    private static long getSuitMask(long handMask, int suit) {
        // suit = 0 (Spades), 1 (Hearts), 2 (Diamonds), 3 (Clubs)
        return (handMask >> (13 * suit)) & 0x1FFF; // 0x1FFF = 13 bits set
    }

    private static int getRankMask(long handMask) {
        int rankMask = 0;
        for (int suit = 0; suit < 4; suit++) {
            rankMask |= (int) getSuitMask(handMask, suit);
        }
        return rankMask;
    }

    private static boolean hasFlush(long handMask) {
        for (int suit = 0; suit < 4; suit++) {
            long suitMask = getSuitMask(handMask, suit);
            if (Long.bitCount(suitMask) >= 5) return true;
        }
        return false;
    }

    private static boolean hasStraight(int rankMask) {
        // Ace-low straight (A,2,3,4,5)
        if ((rankMask & 0b1000000001111) == 0b1000000001111) return true;

        // Regular straights
        for (int i = 0; i <= 8; i++) { // 8 = 13 - 5
            if ((rankMask & (0b11111 << i)) == (0b11111 << i)) return true;
        }
        return false;
    }


    /*
     * Test main method.
     * 
     * To compile all files and run
     * javac -d out $(find src/main/java -name "*.java")
     * java -cp out org.poker.HandEval.BitmaskHandEval
     */
    public static void main(String[] args) {
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(10, 'S'));
        hand.add(new Card(11, 'S'));
        hand.add(new Card(12, 'S'));
        hand.add(new Card(13, 'S'));
        hand.add(new Card(14, 'S'));
        System.out.println(evaluateHand(hand));
    }
}
