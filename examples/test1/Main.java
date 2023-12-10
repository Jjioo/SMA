package test1;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main extends Agent {

    public static void main(String[] args) throws StaleProxyException {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();

        AgentContainer ac = rt.createMainContainer(p);

        AgentController rmaAgentController = null;
        AgentController snifferAgentController = null;
        AgentController server1AgentController = null;
        AgentController server2AgentController = null;
        AgentController server3AgentController = null;
        AgentController server4AgentController = null;
        AgentController client1AgentController = null;

        try {
            rmaAgentController = ac.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
            rmaAgentController.start();

            snifferAgentController = ac.createNewAgent("snif", "jade.tools.sniffer.Sniffer", new Object[0]);
            snifferAgentController.start();

            server1AgentController = ac.createNewAgent("s1", "test1.AdditionServer", new Object[0]);
            server1AgentController.start();

            server2AgentController = ac.createNewAgent("s2", "test1.SubtractionServer", new Object[0]);
            server2AgentController.start();

            server3AgentController = ac.createNewAgent("s3", "test1.MultiplicationServer", new Object[0]);
            server3AgentController.start();

            server4AgentController = ac.createNewAgent("s4", "test1.DivisionServer", new Object[0]);
            server4AgentController.start();

            Thread.sleep(2000);

            client1AgentController = ac.createNewAgent("c1", "test1.Client", new Object[]{"15", "2"});
            client1AgentController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}