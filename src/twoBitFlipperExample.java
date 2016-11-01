/**
 * Created by jluhrsen on 12/29/15.
 */


import org.simbrain.network.NetworkComponent;
import org.simbrain.network.core.Network;
import org.simbrain.network.core.Neuron;
import org.simbrain.network.groups.NeuronGroup;
import org.simbrain.network.subnetworks.BackpropNetwork;
import org.simbrain.util.Utils;
import org.simbrain.workspace.WorkspaceSerializer;


import java.io.File;
import java.lang.System;
import java.util.List;

import static org.simbrain.util.Utils.getDoubleMatrix;

public class twoBitFlipperExample {

    public static void main(String[] args) {

        File networkXmlFile = new File("./networks/twoBitFlipper.xml");
        File inputDataFile = new File("./networks/data/twoBitFlipperInput.csv");

        double inputData[][]= getDoubleMatrix(inputDataFile);
        //double inputData[][] = { { 0.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 1.0, 1.0 } };

        System.out.format("loading file %s as network", networkXmlFile.toString());

        NetworkComponent networkComponent = (NetworkComponent) WorkspaceSerializer
                .open(NetworkComponent.class, networkXmlFile);

        Network networkWorkSpace = networkComponent.getNetwork();

        List groupList = networkWorkSpace.getGroupList();
        BackpropNetwork bpNet = (BackpropNetwork) groupList.get(0);

        NeuronGroup inputNeurons = bpNet.getNeuronGroupByLabel("input");
        NeuronGroup hiddenNeurons = bpNet.getNeuronGroupByLabel("hidden");
        NeuronGroup outputNeurons = bpNet.getNeuronGroupByLabel("output");
        NeuronGroup inputLayer = bpNet.getInputLayer();
        NeuronGroup outputLayer = bpNet.getOutputLayer();

        networkWorkSpace.update();

        for (int row = 0; row < inputData.length; row++) {
            for (int i = 0; i < 2; i++) {
                bpNet.getInputNeurons().get(i).forceSetActivation(inputData[row][i]);
            }
            bpNet.update();
            for (Neuron neuron : outputNeurons.getNeuronList()) {
                System.out.print(Utils.round(neuron.getActivation(), 2) + " " + "\n");
            }
        }
    }

}
