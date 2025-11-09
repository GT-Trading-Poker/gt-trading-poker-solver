package org.poker.logging;

import org.mlflow.api.proto.Service;
import org.mlflow.tracking.MlflowClient;
import org.mlflow.api.proto.Service.Experiment;

import java.util.HashMap;
import java.util.Optional;

public class WandBLogger {
    private String runId;
    private MlflowClient client;

    public WandBLogger(HashMap<String,String> params, String url, String experimentName) {
        client = new MlflowClient(url);

        Optional<Experiment> experiment = client.getExperimentByName(experimentName);
        String experimentId;

        if (experiment.isPresent()) {
            experimentId = experiment.get().getExperimentId();
        } else {
            experimentId = client.createExperiment(experimentName);
        }

        runId = client.createRun(experimentId).getRunId();

        for (String key : params.keySet()) {
            client.logParam(runId, key, params.get(key));
        }

    }

    public void log(HashMap<String,Double> metrics, int iteration) {
        for (String key : metrics.keySet()) {
            client.logMetric(runId,key,metrics.get(key), System.currentTimeMillis(), iteration);
        }
    }

    public void finish() {
        client.setTerminated(runId, Service.RunStatus.FINISHED, System.currentTimeMillis());
    }
}
