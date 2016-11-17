/**
 * Created by jamo on 11/16/2016.
 */


import org.simbrain.network.NetworkComponent;
import org.simbrain.network.core.*;
import org.simbrain.network.subnetworks.*;
import org.simbrain.network.trainers.*;
import org.simbrain.util.*;
import org.simbrain.workspace.*;
import java.io.*;

class NetworkTrainer {
    /**
     * Test method.
     *
     * @param args
     */
    public static void main(String[] args) throws Trainer.DataNotInitializedException, IOException {
        String networkLocation = "/home/jluhrsen/jamo/NNet/SimBrain/jamo_networks";
        int numNetsToTrain = 20;
        for (int netNumber = 1; netNumber <= numNetsToTrain; netNumber++) {
            String netFilePrefix = "net" + String.format("%03d", netNumber);
            OutputStream savedNetFile = new FileOutputStream(networkLocation + "/autonet" + netFilePrefix + ".xml");
            test(savedNetFile);
        }
    }

    /**
     * Test the neural network.
     * @param savedNetFile
     */
    public static void test(OutputStream savedNetFile) throws Trainer.DataNotInitializedException, IOException {
        String dataLocation = "/home/jluhrsen/jamo/OpenDaylight/git/nfl_examples/resources/data";
        File inputDataFile = new
                File(dataLocation + "/aprox1200_most_recent_with_dvoa_no_teams_normalized_input_no_header.csv");
        File expectedResultsDataFile = new
                File(dataLocation + "/aprox1200_most_recent_with_dvoa_no_teams_normalized_output_no_header.csv");

        Workspace ws = new Workspace();
        Network network;
        BackpropNetwork bp;

        ws.clearWorkspace();

        NetworkComponent networkComponent = new NetworkComponent("NN Training");
        network = networkComponent.getNetwork();
        bp = new BackpropNetwork(network, new int[]{226,113,2});
        ws.addWorkspaceComponent(networkComponent);
        network.addGroup(bp);

        bp.getTrainingSet().setInputData(Utils.getDoubleMatrix(inputDataFile));
        bp.getTrainingSet().setTargetData(Utils.getDoubleMatrix(expectedResultsDataFile));

        BackpropTrainer trainer = new BackpropTrainer(bp, bp.getNeuronGroupsAsList());
        trainer.randomize();
        trainer.setLearningRate(.01);
        System.out.println("----Training----");
        double mse = 1.0;
        while (mse > 0.015) {
            trainer.iterate();
            mse = trainer.getError();
            System.out.println("MSE: " + mse);
        }

        network.clearActivations();
        network.fireNeuronsUpdated();

        networkComponent.save(savedNetFile, "xml");
    }

}