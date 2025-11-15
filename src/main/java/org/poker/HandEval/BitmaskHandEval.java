package org.poker.HandEval;

import org.poker.CFR.History.AbstractHistory;
import org.poker.Card;

import java.util.ArrayList;

public class BitmaskHandEval extends HandEval {
    @Override
    public int compareHands(ArrayList<Card> a, ArrayList<Card> b) {
        if (a.size() != 5 && b.size() != 5) {
            throw new IllegalArgumentException("Cards must have exactly 5 cards total.");
        }
        return evaluateHand(a) - evaluateHand(b);
    }

    @Override
    public ArrayList<Double> utilityFromHistory(AbstractHistory history) {
        return null;
    }

    public static int evaluateHand(ArrayList<Card> hand) {
        long handMask = handToBitmask(hand);
        int rankMask = getRankMask(handMask);
        int flushMask = getFlush(handMask);
        int straight = getStraight(rankMask);
        int[] rankCounts = getRankCounts(handMask);

        // Format 0xXXXXXX
        // Straight Flush
        if (flushMask >= 0 && straight >= 0) {
            return 0x900000 + straight;
        }
        // Four of a Kind
        int quads = -1;
        int high = -1;
        for (int i = 0; i < 13; i++) {
            if (rankCounts[i] == 4) {
                quads = i;
            }
            if (rankCounts[i] == 1) {
                high = i;
            }
        }
        if (quads >= 0) {
            return 0x800000 + (quads << 4) + high;
        }
        // Full House
        int fh_trips = -1;
        int fh_pair = -1;
        for (int i = 0; i < 13; i++) {
            if (rankCounts[i] == 3) {
                fh_trips = i;
            }
            if (rankCounts[i] == 2) {
                fh_pair = i;
            }
        }
        if (fh_trips >= 0 && fh_pair >= 0) {
            return 0x700000 + (fh_trips << 4) + fh_pair;
        }
        // Flush
        if (flushMask >= 0) {
            int weight = 0x600000;
            for (int i = 4; i >= 0; i--) {
                weight += (31 - Integer.numberOfLeadingZeros(flushMask)) << (i * 4);
                flushMask -= Integer.highestOneBit(flushMask);
            }
            return weight;
        }
        // Straight
        if (straight >= 0) {
            return 0x500000 + straight;
        }
        // Three of a Kind
        int trips = -1;
        int tr_highCards = 0x00;
        for (int i = 12; i >= 0; i--) {
            if (rankCounts[i] == 3) {
                trips = i;
            }
            if (rankCounts[i] == 1) {
                if (tr_highCards == 0) {
                    tr_highCards = (i << 4);
                } else {
                    tr_highCards += i;
                }
            }
        }
        if (trips >= 0) {
            return 0x400000 + (trips << 8) + tr_highCards;
        }
        // Two Pair
        int twoPair = 0x00;
        int highCard = 0;
        for (int i = 12; i >= 0; i--) {
            if (rankCounts[i] == 2) {
                if (twoPair == 0) {
                    twoPair = (i + 1 << 4);
                } else {
                    twoPair += i + 1;
                }
            }
            if (rankCounts[i] == 1) {
                highCard = i;
            }
        }
        if (twoPair > 13 && (twoPair & 0xF) != 0) {
            return 0x300000 + (twoPair << 4) + highCard;
        }
        // One Pair
        int onePair = -1;
        int op_highCards = 0x000;
        int op_index = 2;
        for (int i = 12; i >= 0; i--) {
            if (rankCounts[i] == 2) {
                onePair = i;
            }
            if (rankCounts[i] == 1) {
                op_highCards += i << (op_index * 4);
                op_index--;
            }
        }
        if (onePair >= 0) {
            return 0x200000 + (onePair << 12) + op_highCards;
        }
        // High Card
        int weight = 0x100000;
        int index = 4;
        for (int i = 12; i >= 0; i--) {
           if (rankCounts[i] == 1) {
               weight += i << (index * 4);
               index--;
           }
        }
        return weight;
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
     * Bits 53-64: None, just 0s
     *
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

    private static int getSuitMask(long handMask, int suit) {
        // suit = 0 (Spades), 1 (Hearts), 2 (Diamonds), 3 (Clubs)
        return (int)((handMask >> (13 * suit)) & 0x1FFF); // 0x1FFF = 13 bits set
    }

    private static int getRankMask(long handMask) {
        int rankMask = 0;
        for (int suit = 0; suit < 4; suit++) {
            rankMask |= getSuitMask(handMask, suit);
        }
        return rankMask;
    }

    private static int getFlush(long handMask) {
        for (int suit = 0; suit < 4; suit++) {
            int suitMask = getSuitMask(handMask, suit);
            if (Integer.bitCount(suitMask) >= 5) return suitMask;
        }
        return -1;
    }

    private static int getStraight(int rankMask) {
        // Ace-low straight (A,2,3,4,5)
        if ((rankMask & 0b1000000001111) == 0b1000000001111) return 5;

        // Regular straights
        for (int i = 8; i >= 0; i--) {
            if ((rankMask & (0b11111 << i)) == (0b11111 << i)) return i + 4;
        }

        return -1;
    }

    // Counts how many of each rank exist across suits
    private static int[] getRankCounts(long handMask) {
        int[] counts = new int[13]; // 2–A
        for (int suit = 0; suit < 4; suit++) {
            int suitMask = getSuitMask(handMask, suit);
            for (int rank = 0; rank < 13; rank++) {
                if (((suitMask >> rank) & 1L) != 0) {
                    counts[rank]++;
                }
            }
        }
        return counts;
    }

    /*
     * Test main method.
     *
     * To compile all files and run
     * javac -d out $(find src/main/java -name "*.java")
     * java -cp out org.poker.HandEval.BitmaskHandEval
     */
    public static void main(String[] args) {

        java.util.function.BiConsumer<String, ArrayList<Card>> test =
                (name, hand) -> {
                    int value = evaluateHand(hand);
                    System.out.printf("%s: %d (0x%06X)%n", name, value, value);
                };

        // Royal Flush
        ArrayList<Card> royalFlush = new ArrayList<>();
        royalFlush.add(new Card(10, 'S'));
        royalFlush.add(new Card(11, 'S'));
        royalFlush.add(new Card(12, 'S'));
        royalFlush.add(new Card(13, 'S'));
        royalFlush.add(new Card(14, 'S'));
        test.accept("Royal Flush", royalFlush);

        // Straight Flush
        ArrayList<Card> straightFlush = new ArrayList<>();
        straightFlush.add(new Card(6, 'H'));
        straightFlush.add(new Card(7, 'H'));
        straightFlush.add(new Card(8, 'H'));
        straightFlush.add(new Card(9, 'H'));
        straightFlush.add(new Card(10, 'H'));
        test.accept("Straight Flush", straightFlush);

        // Four of a Kind (Quads)
        ArrayList<Card> quads = new ArrayList<>();
        quads.add(new Card(9, 'S'));
        quads.add(new Card(9, 'H'));
        quads.add(new Card(9, 'D'));
        quads.add(new Card(9, 'C'));
        quads.add(new Card(5, 'S'));
        test.accept("Quads", quads);

        // Full House (3 + 2)
        ArrayList<Card> fullHouse = new ArrayList<>();
        fullHouse.add(new Card(7, 'S'));
        fullHouse.add(new Card(7, 'H'));
        fullHouse.add(new Card(7, 'C'));
        fullHouse.add(new Card(4, 'D'));
        fullHouse.add(new Card(4, 'S'));
        test.accept("Full House", fullHouse);

        // Three of a Kind
        ArrayList<Card> trips = new ArrayList<>();
        trips.add(new Card(5, 'S'));
        trips.add(new Card(5, 'H'));
        trips.add(new Card(5, 'D'));
        trips.add(new Card(9, 'S'));
        trips.add(new Card(12, 'H'));
        test.accept("Three of a Kind", trips);

        // Two Pair
        ArrayList<Card> twoPair = new ArrayList<>();
        twoPair.add(new Card(8, 'S'));
        twoPair.add(new Card(8, 'H'));
        twoPair.add(new Card(3, 'S'));
        twoPair.add(new Card(3, 'D'));
        twoPair.add(new Card(11, 'C'));
        test.accept("Two Pair", twoPair);

        // One Pair
        ArrayList<Card> onePair = new ArrayList<>();
        onePair.add(new Card(10, 'S'));
        onePair.add(new Card(10, 'H'));
        onePair.add(new Card(2, 'C'));
        onePair.add(new Card(4, 'D'));
        onePair.add(new Card(9, 'S'));
        test.accept("One Pair", onePair);

        // High Card
        ArrayList<Card> highCard = new ArrayList<>();
        highCard.add(new Card(2, 'S'));
        highCard.add(new Card(5, 'H'));
        highCard.add(new Card(8, 'C'));
        highCard.add(new Card(10, 'D'));
        highCard.add(new Card(13, 'S'));
        test.accept("High Card", highCard);
    }
}
