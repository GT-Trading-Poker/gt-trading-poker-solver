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
    private long handToBitmask(ArrayList<Card> hand) {
        long result = 0;
        for (Card card : hand) {
            //assuming Ace is rank 14
            long mask = 1L << 13 * suitToNumber(card.getSuite()) + card.getRank() - 2;
            result |= mask;
        }
        return result;
    }

    private int suitToNumber(char suit) {
        return switch (suit) {
            case 'S' -> 0;
            case 'H' -> 1;
            case 'D' -> 2;
            case 'C' -> 3;
            default -> throw new RuntimeException("Invalid suit: " + suit);
        };
    }

    /**
     * Counts the frequency of each rank in the hand.
     * 
     * @param hand ArrayList of cards
     * @return int array where index represents rank (2-14) and value is count
     *         Index 0-1 are unused, index 2 = twos, index 3 = threes, ..., index 14 = aces
     */
    private int[] getRankCounts(ArrayList<Card> hand) {
        // Array size 15 to accommodate ranks 2-14 (Ace = 14)
        int[] counts = new int[15];
        
        // Iterate through each card and increment the count for its rank
        for (Card card : hand) {
            counts[card.getRank()]++;
        }
        
        return counts;
    }

    /**
     * Checks if the hand contains a flush (5+ cards of the same suit).
     * 
     * @param hand ArrayList of cards
     * @return true if hand contains a flush, false otherwise
     */
    private boolean isFlush(ArrayList<Card> hand) {
        // Array to count cards of each suit: [Spades, Hearts, Diamonds, Clubs]
        int[] suitCounts = new int[4];
        
        // Count how many cards of each suit we have
        for (Card card : hand) {
            suitCounts[suitToNumber(card.getSuite())]++;
        }
        
        // Check if any suit has 5 or more cards
        for (int count : suitCounts) {
            if (count >= 5) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Checks if the hand contains a straight (5+ consecutive ranks).
     * 
     * @param rankCounts Array where index is rank and value is count
     * @return true if hand contains a straight, false otherwise
     */
    private boolean isStraight(int[] rankCounts) {
        // Track how many consecutive ranks we've found
        int consecutive = 0;
        
        // Iterate through ranks from 2 to 14 (Ace)
        for (int rank = 2; rank <= 14; rank++) {
            // If this rank exists in the hand, increment consecutive counter
            if (rankCounts[rank] > 0) {
                consecutive++;
                // If we found 5 consecutive ranks, we have a straight
                if (consecutive >= 5) {
                    return true;
                }
            } else {
                // Break in the sequence, reset counter
                consecutive = 0;
            }
        }
        
        // Special case: Ace-low straight (A-2-3-4-5)
        // Check if we have Ace (14) and 2-3-4-5
        if (rankCounts[14] > 0 && rankCounts[2] > 0 && rankCounts[3] > 0 
            && rankCounts[4] > 0 && rankCounts[5] > 0) {
            return true;
        }
        
        return false;
    }
}
