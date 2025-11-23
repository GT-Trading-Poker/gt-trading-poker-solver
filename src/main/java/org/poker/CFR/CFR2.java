package org.poker.CFR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.poker.GameSim.GameSim;
import org.poker.CFR.History.AbstractHistory;

public class CFR2 {
    private final GameSim game;
    private final int numPlayers;
    private final Map<String, InfoSet> infoSets;
    private final Random random;

    public CFR2(GameSim game) {
        this.game = game;
        this.numPlayers = game.numPlayers();
        this.infoSets = new HashMap<>();
        this.random = new Random();
    }

    private InfoSet getInfoSet(AbstractHistory history, int player, ArrayList<String> actions) {
        String key = history.infoSetKey(player);
        infoSets.putIfAbsent(key, new InfoSet(key, actions));
        return infoSets.get(key);
    }
    
    private String sampleAction(Map<String, Double> strategy, ArrayList<String> actions) {
        double r = this.random.nextDouble();
        double cumulativeProb = 0.0;
        for (String a : actions) {
            cumulativeProb += strategy.get(a);
            if (r < cumulativeProb) {
                return a;
            }
        }
        return actions.get(actions.size() - 1);
    }

    public double traverseMCCFR(AbstractHistory history, int targetPlayer, double piReach, double oppReach) {
        if (game.isTerminal(history)) {
            double[] util = game.terminalUtility(history);
            return util[targetPlayer];
        }

        boolean isChance = false;
        for (String action : history.getActions()) {
            if (action.startsWith("Deal")) {
                isChance = true;
                break;
            }
        }

        if (isChance) {
            ArrayList<AbstractHistory> deals = game.generateAllDeals(history);
            int sampledIndex = random.nextInt(deals.size());
            AbstractHistory sampledDeal = deals.get(sampledIndex);
            double probSample = 1.0 / deals.size();
            return traverseMCCFR(sampledDeal, targetPlayer, piReach, oppReach) / probSample;
        }

        int currentPlayer = game.currentPlayer(history);
        ArrayList<String> actions = game.getAvailableActions(history);
        if (actions.isEmpty()) return 0.0;

        InfoSet infoSet = getInfoSet(history, currentPlayer, actions);
        Map<String, Double> strategy = infoSet.getStrategy();

        if (currentPlayer == targetPlayer) {
            String sampledAction = sampleAction(strategy, actions);
            AbstractHistory next = history.copy();
            next.addAction("P" + currentPlayer + ":" + sampledAction);

            double childValue = traverseMCCFR(next, targetPlayer, piReach * strategy.get(sampledAction), oppReach);

            double nodeValue = 0.0;
            for (String a : actions) {
                double cfv = (a.equals(sampledAction)) ? childValue : 0.0;
                nodeValue += strategy.get(a) * cfv;
            }

            for (String a : actions) {
                double regret = ((a.equals(sampledAction) ? childValue : 0.0) - nodeValue) * oppReach;
                infoSet.addToRegretSum(a, regret);
                infoSet.addToStrategySum(a, strategy.get(a) * piReach);
            }

            return nodeValue;

        } else {
            String sampledAction = sampleAction(strategy, actions);
            AbstractHistory next = history.copy();
            next.addAction("P" + currentPlayer + ":" + sampledAction);

            return traverseMCCFR(next, targetPlayer, piReach, oppReach * strategy.get(sampledAction));
        }
    }

    public double traverse(AbstractHistory history, int targetPlayer) {
        if (game.isTerminal(history)) {
            double[] util = game.terminalUtility(history);
            return util[targetPlayer];
        }

        boolean hasDealActions = false;
        for (String action : history.getActions()) {
            if (action.startsWith("Deal")) {
                hasDealActions = true;
                break;
            }
        }
        
        if (!hasDealActions) {
            ArrayList<AbstractHistory> deals = game.generateAllDeals(history);
            int dealIndex = this.random.nextInt(deals.size());
            AbstractHistory sampledDeal = deals.get(dealIndex);

            return traverse(sampledDeal, targetPlayer);
        }

        int currentPlayer = game.currentPlayer(history);

        ArrayList<String> actions = game.getAvailableActions(history);
        if (actions.isEmpty()) return 0.0;

        InfoSet infoSet = getInfoSet(history, currentPlayer, actions);
        Map<String, Double> strategy = infoSet.getStrategy();

        if (currentPlayer == targetPlayer) {
            Map<String, Double> actionVals = new HashMap<>();
            double nodeValue = 0.0;

            for (String a : actions) {
                AbstractHistory next = history.copy();
                next.addAction("P" + currentPlayer + ":" + a);

                double v = traverse(next, targetPlayer);
                actionVals.put(a, v);
                nodeValue += strategy.get(a) * v;
            }

            for (String a : actions) {
                infoSet.addToStrategySum(a, strategy.get(a));
                double regret = actionVals.get(a) - nodeValue;
                infoSet.addToRegretSum(a, regret);
            }
            return nodeValue;

        } else {
            // Opponnet
            String sampledAction = sampleAction(strategy, actions);
            AbstractHistory next = history.copy();
            next.addAction("P" + currentPlayer + ":" + sampledAction);
            return traverse(next, targetPlayer);
        }
    }

    // public void train(int iterations, AbstractHistory initialHistory) {
    //     for (int i = 0; i < iterations; i++) {
    //         int targetPlayer = i % numPlayers;
    //         traverse(initialHistory.copy(), targetPlayer);
    //     }
    // }

    public void train(int iterations, AbstractHistory initialHistory) {
        for (int i = 0; i < iterations; i++) {
            int targetPlayer = i % numPlayers;
            traverseMCCFR(initialHistory.copy(), targetPlayer, 1.0, 1.0);
        }
    }

    public Map<String, InfoSet> getInfoSets() {
        return infoSets;
    }
}