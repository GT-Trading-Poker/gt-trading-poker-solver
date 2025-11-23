package org.poker.HandEval;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.poker.Card;

public class PreComputedHandEval extends HandEval {
    private final Map<String, Integer> rankTable;

    public PreComputedHandEval(Map<String, Integer> rankTable) {
        this.rankTable = rankTable;
    }

    public PreComputedHandEval(String fileName) throws FileNotFoundException, IOException {
        this.rankTable = new HashMap<>();
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)))) {

            int count = in.readInt();

            for (int i = 0; i < count; i++) {
                int keyLen = in.readShort();
                byte[] keyBytes = new byte[keyLen];
                in.readFully(keyBytes);
                String key = new String(keyBytes, "UTF-8");

                int value = in.readInt();
                this.rankTable.put(key, value);
            }
        }
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

    private static String key(List<Card> hand) {
        List<String> parts = new ArrayList<>(hand.size());
        for (Card c : hand) {
            parts.add(c.getRank() + String.valueOf(c.getSuite()));
        }
        parts.sort((x, y) -> {
            int rx = Integer.parseInt(x.substring(0, x.length() - 1));
            int ry = Integer.parseInt(y.substring(0, y.length() - 1));
            if (rx != ry)
                return Integer.compare(rx, ry);
            char sx = x.charAt(x.length() - 1);
            char sy = y.charAt(y.length() - 1);
            return Character.compare(sx, sy);
        });
        return String.join("-", parts);
    }

    public static Map<String, Integer> generateRankTable(String fileName, ArrayList<Card> deck,
            BitmaskHandEval eval5) throws FileNotFoundException, IOException {
        Map<String, Integer> table = new HashMap<>(140_000_000, 0.75f);

        int n = deck.size();
        List<Card> seven = new ArrayList<>(7);
        List<Card> five = new ArrayList<>(5);

        final int[][] COMBOS_7_5 = {
                { 0, 1, 2, 3, 4 },
                { 0, 1, 2, 3, 5 },
                { 0, 1, 2, 3, 6 },
                { 0, 1, 2, 4, 5 },
                { 0, 1, 2, 4, 6 },
                { 0, 1, 2, 5, 6 },
                { 0, 1, 3, 4, 5 },
                { 0, 1, 3, 4, 6 },
                { 0, 1, 3, 5, 6 },
                { 0, 1, 4, 5, 6 },
                { 0, 2, 3, 4, 5 },
                { 0, 2, 3, 4, 6 },
                { 0, 2, 3, 5, 6 },
                { 0, 2, 4, 5, 6 },
                { 0, 3, 4, 5, 6 },
                { 1, 2, 3, 4, 5 },
                { 1, 2, 3, 4, 6 },
                { 1, 2, 3, 5, 6 },
                { 1, 2, 4, 5, 6 },
                { 1, 3, 4, 5, 6 },
                { 2, 3, 4, 5, 6 }
        };

        for (int i0 = 0; i0 <= n - 7; i0++) {
            for (int i1 = i0 + 1; i1 <= n - 6; i1++) {
                for (int i2 = i1 + 1; i2 <= n - 5; i2++) {
                    for (int i3 = i2 + 1; i3 <= n - 4; i3++) {
                        for (int i4 = i3 + 1; i4 <= n - 3; i4++) {
                            for (int i5 = i4 + 1; i5 <= n - 2; i5++) {
                                for (int i6 = i5 + 1; i6 <= n - 1; i6++) {

                                    seven.clear();
                                    seven.add(deck.get(i0));
                                    seven.add(deck.get(i1));
                                    seven.add(deck.get(i2));
                                    seven.add(deck.get(i3));
                                    seven.add(deck.get(i4));
                                    seven.add(deck.get(i5));
                                    seven.add(deck.get(i6));

                                    int bestRank = Integer.MIN_VALUE;
                                    for (int[] combo : COMBOS_7_5) {
                                        five.clear();
                                        for (int idx : combo) {
                                            five.add(seven.get(idx));
                                        }
                                        // TODO: need strength method implemented in BitmaskHandEval

                                        // int r = eval5.strength(five);
                                        // if (r > bestRank) {
                                        // bestRank = r;
                                        // }
                                    }

                                    String k = key(seven);
                                    table.put(k, bestRank);
                                }
                            }
                        }
                    }
                }
            }
        }

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))) {

            out.writeInt(table.size());

            for (Map.Entry<String, Integer> e : table.entrySet()) {
                byte[] keyBytes = e.getKey().getBytes("UTF-8");
                out.writeShort(keyBytes.length);
                out.write(keyBytes);
                out.writeInt(e.getValue());
            }
        }

        return table;
    }
}
