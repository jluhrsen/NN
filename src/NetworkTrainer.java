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
import java.util.UUID;

class NetworkTrainer {

    private static final int numNetsToTrain = 1;
    private static File inputDataFile = new File("./data/aprox1200_most_recent_with_dvoa_no_teams_normalized_input_no_header.csv");
    private static File expectedResultsDataFile = new File("./data/aprox1200_most_recent_with_dvoa_no_teams_normalized_output_no_header.csv");

    public static void main(String[] args) throws Trainer.DataNotInitializedException, IOException {
        String networkLocation = "./networks/autonets/";
        for (int netNumber = 1; netNumber <= numNetsToTrain; netNumber++) {
            UUID uuid = UUID.randomUUID();
            String netFileName = "autonet-" + uuid + ".xml";
            File netFile = new File(networkLocation + netFileName);
            OutputStream savedNetFile = new FileOutputStream(netFile);
            test(savedNetFile);
            savedNetFile.flush();
            savedNetFile.close();

            FileTransferService transferService = new FileTransferService();
            transferService.transferFile(networkLocation, netFileName, "/data/autonets/", netFileName);
        }
    }

    /**
     * Test the neural network.
     * @param savedNetFile
     */
    public static void test(OutputStream savedNetFile) throws Trainer.DataNotInitializedException, IOException {

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
        int iterations = 0;
        double mse = 1.0;
        while (mse >= 0.001) {
            iterations++;
            // save the net every 100 iterations of training
            if (iterations % 100 == 0) {
                OutputStream backupNetFile = new FileOutputStream("/tmp/backupNet.xml");
                networkComponent.save(backupNetFile, "xml");
            }
            trainer.iterate();
            mse = trainer.getError();
            System.out.println("MSE: " + mse);
        }

        network.clearActivations();
        network.fireNeuronsUpdated();

        networkComponent.save(savedNetFile, "xml");
    }

}