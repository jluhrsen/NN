/**
 * Created by jluhrsen on 12/29/15.
 */


import com.Ostermiller.util.CSVPrinter;
import org.apache.log4j.BasicConfigurator;
import org.simbrain.network.NetworkComponent;
import org.simbrain.network.core.Network;
import org.simbrain.network.core.Neuron;
import org.simbrain.network.groups.NeuronGroup;
import org.simbrain.network.subnetworks.BackpropNetwork;
import org.simbrain.util.Utils;
import org.simbrain.workspace.WorkspaceSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import static org.simbrain.util.Utils.getDoubleMatrix;
import static org.simbrain.util.Utils.writeMatrix;

public class predictionMaker {

    public static void main(String[] args) {

        int numberOfExamples = 98;
        int numberOfInputs = 226;
        int numberOfOutputs = 2;

        BasicConfigurator.configure();

        String netNumber = "011";
        // network file saved from simbrain GUI
        File networkXmlFile = new File("./networks/net" + netNumber + ".xml");

        // input and output files
        File inputDataFile = new File("/home/jluhrsen/jamo/python/NFLData/resources/2015_playoff_predictions_normalized_no_headers.csv");
        File predictionDataFile = new File("./networks/data/predictions_net" + netNumber + ".csv");

        // pull input data in to array
        double inputData[][]= getDoubleMatrix(inputDataFile);

        // found these examples in simbrain docs, and it works, but not sure any better way
        NetworkComponent networkComponent = (NetworkComponent) WorkspaceSerializer
                .open(NetworkComponent.class, networkXmlFile);
        Network networkWorkSpace = networkComponent.getNetwork();

        // the network saved in simbrain GUI ends up as a 0th subnetwork in a "group list"
        List groupList = networkWorkSpace.getGroupList();
        BackpropNetwork bpNet = (BackpropNetwork) groupList.get(0);

        // the output layer was renamed to "output" in the simbrain GUI before saving.
        NeuronGroup outputNeurons = bpNet.getNeuronGroupByLabel("output");

        // hard coded 10 x 2 prediction array
        String predictions[][] = new String[numberOfExamples][numberOfOutputs];

        // for each of the 10 rows in the input data, set the input activations, update
        // the network, pull the output activations and save to prediction array, printing
        // output along the way
        for (int row = 0; row < inputData.length; row++) {
            for (int i = 0; i < numberOfInputs; i++) {
                bpNet.getInputNeurons().get(i).forceSetActivation(inputData[row][i]);
            }
            bpNet.update();
            String output1 = Utils.round(outputNeurons.getNeuronList().get(0).getActivation(), 2);
            String output2 = Utils.round(outputNeurons.getNeuronList().get(1).getActivation(), 2);
            System.out.print(output1 + ", " + output2 +"\n");
            predictions[row][0] = output1;
            predictions[row][1] = output2;
        }

        // write the predictions to file
        // writeMatrix(predictions, predictionDataFile);

        FileOutputStream f = null;

        try {
            f = new FileOutputStream(predictionDataFile);
        } catch (Exception var4) {
            System.out.println("Could not open file stream: " + var4.toString());
        }

        if(f != null) {
            CSVPrinter thePrinter = new CSVPrinter(f);
            thePrinter.printlnComment("");
            thePrinter.printlnComment("File: " + predictionDataFile.getName());
            thePrinter.printlnComment("");
            thePrinter.println();
            thePrinter.println(predictions);
            thePrinter.println();
        }
    }

}
