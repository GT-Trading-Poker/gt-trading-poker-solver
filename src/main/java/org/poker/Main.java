package org.poker;

import org.poker.GameSim.KuhnPokerGameSim;
import org.poker.CFR.CFR;
import org.poker.CFR.History.KuhnPokerHistory;
import org.poker.CFR.InfoSet;
import org.poker.logging.WandBLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter number of iterations:");
        int iterations = sc.nextInt();

        System.out.println("Enter Logging URL:");
        String url = sc.next();

        System.out.println("Enter Expirement Name:");
        String exp = sc.next();

        System.out.println("Enter Logging Frequency:");
        int freq = sc.nextInt();

        KuhnPokerGameSim game = new KuhnPokerGameSim();
        KuhnPokerHistory initial = new KuhnPokerHistory();

        HashMap<String,String> params = new HashMap<String,String>();

        params.put("Iterations",String.valueOf(iterations));

        WandBLogger logger = new WandBLogger(params,url,exp);
        CFR trainer = new CFR(game);
        trainer.train(iterations, initial, logger,freq);
        printStrategies(trainer.getInfoSets());

        sc.close();
        logger.finish();
    }

    private static void printStrategies(Map<String, InfoSet> infoSets) {
        System.out.println("Average strategies:");
        for (InfoSet iset : infoSets.values()) {
            System.out.println(iset.getKey() + " : " + iset.getAverageStrategy());
        }
    }


}
