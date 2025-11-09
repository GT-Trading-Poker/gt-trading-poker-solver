package org.poker.CFR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InfoSet {
    private final String key;
    private final ArrayList<String> actions;
    private final Map<String, Double> regretSum;
    private final Map<String, Double> strategySum;

    public InfoSet(String key, ArrayList<String> actions) {
        this.key = key;
        this.actions = new ArrayList<>(actions);
        this.regretSum = new HashMap<>();
        this.strategySum = new HashMap<>();
        for (String a : actions) {
            regretSum.put(a, 0.0);
            strategySum.put(a, 0.0);
        }
    }

    public Map<String, Double> getStrategy() {
        Map<String, Double> strategy = new HashMap<>();
        double sumPositive = 0.0;

        for (String a : actions) {
            double r = regretSum.getOrDefault(a, 0.0);
            if (r > 0) sumPositive += r;
        }

        for (String a : actions) {
            double r = regretSum.getOrDefault(a, 0.0);
            strategy.put(a, sumPositive > 0 ? Math.max(r, 0.0) / sumPositive : 1.0 / actions.size());
        }
        return strategy;
    }

    public void addToRegretSum(String action, double value) {
        regretSum.put(action, regretSum.getOrDefault(action, 0.0) + value);
    }

    public void addToStrategySum(String action, double value) {
        strategySum.put(action, strategySum.getOrDefault(action, 0.0) + value);
    }

    public Map<String, Double> getAverageStrategy() {
        Map<String, Double> avg = new HashMap<>();
        double total = 0.0;
        for (String a : actions) total += strategySum.getOrDefault(a, 0.0);
        for (String a : actions)
            avg.put(a, total > 0 ? strategySum.get(a) / total : 1.0 / actions.size());
        return avg;
    }

    public double getTotalRegret() {
        double total = 0;
        for (String a : actions) {
            if (regretSum.get(a) > 0) {
                total += regretSum.get(a);
            }
        }
        return total;
    }

    public String getKey() { return key; }
}
