import java.io.File;

/**
 * Created by jamo on 11/9/16.
 */
public class ProcessPredictions {
    /**
     * load already trained network(s), validate on pre-existing input data, and save outputs
     * in .csv format.
     */

    String networkLocations = "/home/jluhrsen/jamo/NNet/SimBrain/jamo_networks";
    String inputDataLocations = "/home/jluhrsen/jamo/OpenDaylight/git/nfl_examples/resources";
    File networkXmlFile = new File(networkLocations + "/net001.xml");
    File inputDataFile = new File(inputDataLocations + "/2016_examples_normalized_no_headers.csv");

}
