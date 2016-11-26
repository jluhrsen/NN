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
import java.util.Random;

class NetworkTrainer {
    /**
     * Test method.
     *
     * @param args
     */
    private static final int netStartNumber = 14;
    private static final int netStopNumber = 15;

    public static void main(String[] args) throws Trainer.DataNotInitializedException, IOException {
        String networkLocation = "./networks/autonets/";
        for (int netNumber = netStartNumber; netNumber <= netStopNumber; netNumber++) {
            String netFilePrefix = "autonet" + String.format("%03d", netNumber);
            OutputStream savedNetFile = new FileOutputStream(networkLocation + netFilePrefix + ".xml");
            test(savedNetFile);
            savedNetFile.flush();
            savedNetFile.close();
        }
    }

    /**
     * Test the neural network.
     * @param savedNetFile
     */
    public static void test(OutputStream savedNetFile) throws Trainer.DataNotInitializedException, IOException {
        String dataLocation = "./data";
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

        Random r = new Random();
        int low = 113 - 20;
        int high = 113 + 20;
        int hidden = r.nextInt(high - low) + low;
        System.out.println("using " + hidden +" hidden nodes");

        bp = new BackpropNetwork(network, new int[]{226,hidden,2});
        ws.addWorkspaceComponent(networkComponent);
        network.addGroup(bp);

        bp.getTrainingSet().setInputData(Utils.getDoubleMatrix(inputDataFile));
        bp.getTrainingSet().setTargetData(Utils.getDoubleMatrix(expectedResultsDataFile));

        BackpropTrainer trainer = new BackpropTrainer(bp, bp.getNeuronGroupsAsList());
        trainer.randomize();

        r = new Random();
        low = 10 - 2;
        high = 20 + 2;
        int lr_int = r.nextInt(high - low) + low;
        double lr = (double) lr_int / 1000;
        System.out.println("using " + lr +" as learning rate");

        trainer.setLearningRate(lr);
        System.out.println("----Training----");
        double mse = 1.0;
        while (mse >= 0.001) {
            trainer.iterate();
            mse = trainer.getError();
            System.out.println("MSE: " + mse);
        }

        network.clearActivations();
        network.fireNeuronsUpdated();

        networkComponent.save(savedNetFile, "xml");
    }

}