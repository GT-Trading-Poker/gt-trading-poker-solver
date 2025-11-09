package org.poker.CFR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.poker.GameSim.GameSim;
import org.poker.CFR.History.AbstractHistory;
import org.poker.logging.WandBLogger;

public class CFR {
    private final GameSim game;
    private final int numPlayers;
    private final Map<String, InfoSet> infoSets;

    public CFR(GameSim game) {
        this.game = game;
        this.numPlayers = game.numPlayers();
        this.infoSets = new HashMap<>();
    }

    private InfoSet getInfoSet(AbstractHistory history, int player, ArrayList<String> actions) {
        String key = history.infoSetKey(player);
        infoSets.putIfAbsent(key, new InfoSet(key, actions));
        return infoSets.get(key);
    }

    public double traverse(AbstractHistory history, double[] reach, int targetPlayer) {
        // Terminal
        if (game.isTerminal(history)) {
            double[] util = game.terminalUtility(history);
            return util[targetPlayer];
        }

        // Chance node (dealing cards) - check if cards have been dealt yet
        // Cards have been dealt if there are Deal actions in the history
        boolean hasDealActions = false;
        for (String action : history.getActions()) {
            if (action.startsWith("Deal")) {
                hasDealActions = true;
                break;
            }
        }
        
        if (!hasDealActions) {
            // Need to deal cards first (no Deal actions yet)
            double nodeValue = 0.0;
            ArrayList<AbstractHistory> deals = game.generateAllDeals(history);
            double prob = 1.0 / deals.size();

            for (AbstractHistory h : deals) {
                nodeValue += prob * traverse(h, reach, targetPlayer);
            }
            return nodeValue;
        }

        int currentPlayer = game.currentPlayer(history);

        // Player decision
        ArrayList<String> actions = game.getAvailableActions(history);
        if (actions.isEmpty()) return 0.0;

        InfoSet infoSet = getInfoSet(history, currentPlayer, actions);
        Map<String, Double> strategy = infoSet.getStrategy();

        Map<String, Double> actionVals = new HashMap<>();
        double nodeValue = 0.0;

        for (String a : actions) {
            AbstractHistory next = history.copy();
            next.addAction("P" + currentPlayer + ":" + a);

            double[] newReach = reach.clone();
            newReach[currentPlayer] *= strategy.get(a);

            double v = traverse(next, newReach, targetPlayer);
            actionVals.put(a, v);
            nodeValue += strategy.get(a) * v;
        }

        // Update strategy sum
        for (String a : actions) {
            infoSet.addToStrategySum(a, reach[currentPlayer] * strategy.get(a));
        }

        // Regret update
        if (currentPlayer == targetPlayer) {
            double cfReach = 1.0;
            for (int i = 0; i < numPlayers; i++) {
                if (i != currentPlayer) cfReach *= reach[i];
            }

            for (String a : actions) {
                double regret = actionVals.get(a) - nodeValue;
                infoSet.addToRegretSum(a, cfReach * regret);
            }
        }

        return nodeValue;
    }

    public void train(int iterations, AbstractHistory initialHistory, WandBLogger logger, int logFrequency) {
        HashMap<String,Double> metrics = new HashMap<String,Double>();

        double totalRegret;

        for (int i = 0; i < iterations; i++) {
            for (int p = 0; p < numPlayers; p++) {
                double[] reach = new double[numPlayers];
                for (int j = 0; j < numPlayers; j++) reach[j] = 1.0;
                traverse(initialHistory.copy(), reach, p);
            }
            if (i % logFrequency == 0) {
                // Log the regrets
                totalRegret = 0;
                for (InfoSet iset : infoSets.values()) {
                    totalRegret += iset.getTotalRegret();
                }
                metrics.put("Cumulative Regret", totalRegret);

                logger.log(metrics,i);
            }
        }
    }

    public Map<String, InfoSet> getInfoSets() {
        return infoSets;
    }
}
