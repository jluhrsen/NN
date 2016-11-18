import com.Ostermiller.util.CSVPrinter;
import org.simbrain.network.NetworkComponent;
import org.simbrain.network.core.Network;
import org.simbrain.network.groups.NeuronGroup;
import org.simbrain.network.subnetworks.BackpropNetwork;
import org.simbrain.util.Utils;
import org.simbrain.workspace.WorkspaceSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.simbrain.util.Utils.getDoubleMatrix;

/**
 * Created by jamo on 11/9/16.
 */
public class ProcessPredictions {
    /**
     * load already trained network(s), validate on pre-existing input data, and save outputs
     * in .csv format.
     */
    private static int numNetsToProcess = 12;
    private static String netName = "net";
    private static String outputLayerLabel = "output";
    //private static String outputLayerLabel = "Layer 3";

    public static void main(String[] args) throws IOException, InterruptedException {

        String networkLocation = "/home/jluhrsen/jamo/NNet/SimBrain/jamo_networks";
        String inputDataLocation = "/home/jluhrsen/jamo/OpenDaylight/git/nfl_examples/resources";
        File CombinedPredictionDataFileCsv = new File(networkLocation + "/" + "combined_predictions.csv");
        FileOutputStream fCombined = new FileOutputStream(CombinedPredictionDataFileCsv);
        CSVPrinter fCombinedPrinter = new CSVPrinter(fCombined);

        for (int netNumber = 1; netNumber <= numNetsToProcess; netNumber++) {

            String netFilePrefix = netName + String.format("%03d", netNumber);
            File networkXmlFile = new File(networkLocation + "/" + netFilePrefix + ".xml");
            File inputDataFile = new File(inputDataLocation + "/2016_examples_normalized_no_headers.csv");
            // pull input data in to array
            double inputData[][] = getDoubleMatrix(inputDataFile);

            int numberOfExamples = inputData.length;
            int numberOfInputs = inputData[0].length;
            int numberOfOutputs = 2;

            // found these examples in simbrain docs, and it works, but not sure any better way
            NetworkComponent networkComponent = (NetworkComponent) WorkspaceSerializer
                    .open(NetworkComponent.class, networkXmlFile);
            Network networkWorkSpace = networkComponent.getNetwork();

            // the network saved in simbrain GUI ends up as a 0th subnetwork in a "group list"
            List groupList = networkWorkSpace.getGroupList();
            BackpropNetwork bpNet = (BackpropNetwork) groupList.get(0);

            // the output layer was renamed to "output" in the simbrain GUI before saving.
            NeuronGroup outputNeurons = bpNet.getNeuronGroupByLabel(outputLayerLabel);

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
                String output1 = Utils.round(outputNeurons.getNeuronList().get(0).getActivation(), 3);
                String output2 = Utils.round(outputNeurons.getNeuronList().get(1).getActivation(), 3);
                System.out.print(output1 + ", " + output2 + "\n");
                predictions[row][0] = output1;
                predictions[row][1] = output2;
            }

            // have to write to csv with these java libs, but like to see results in ods for personal reasons
            File predictionDataFileCsv = new File(networkLocation + "/" + netFilePrefix + "_predictions.csv");
            File predictionDataFileOds = new File(networkLocation + "/" + netFilePrefix + "_predictions.ods");

            // combined prediction file is open from the beginning.
            predictionFileWriter(fCombinedPrinter, predictionDataFileCsv.getName(), predictions);

            // we also create individual prediction files per net, so have to create a new csvprinter each time
            FileOutputStream f = new FileOutputStream(predictionDataFileCsv);
            CSVPrinter filePrinter = new CSVPrinter(f);
            predictionFileWriter(filePrinter, predictionDataFileCsv.getName(), predictions);

            try {
                // opening .csv files with libreoffice calc forces an import dialog every time.  converting to .ods
                // format and deleting the .csv file to keep things cleaner
                Process p = Runtime.getRuntime().exec(new String[]{"bash","-c", "csv2ods -i " + predictionDataFileCsv + " -o " + predictionDataFileOds});
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            predictionDataFileCsv.delete();
        }
    }

    private static void predictionFileWriter(CSVPrinter predictionPrinter, String netName, String[][] data) {
        // just doing the basic writing and formating of the output data to the csv file
        predictionPrinter.printlnComment("");
        predictionPrinter.printlnComment("File: " + netName);
        predictionPrinter.printlnComment("");
        predictionPrinter.println();
        predictionPrinter.println(data);
        predictionPrinter.println();
    }
}
