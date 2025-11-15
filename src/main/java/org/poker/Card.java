package org.poker;

public class Card implements Comparable<Card> {
    private int rank;
    private char suite;

    public Card(String rankStr, char suite) {
        this.rank = Byte.parseByte(rankStr.substring(0, rankStr.length()));
        this.suite = suite;
    }

    public Card(int rank, char suite) {
        this.rank = rank;
        this.suite = suite;
    }

    @Override
    public int compareTo (Card other) {
        return this.rank - other.getRank();
    }

    public int getRank() {
        return this.rank;
    }

    public char getSuite() {
        return this.suite;
    }
}
