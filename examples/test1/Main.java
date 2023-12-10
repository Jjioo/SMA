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
        AgentController WordServerController = null;    
        AgentController client1AgentController = null;

        try {
            rmaAgentController = ac.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
            rmaAgentController.start();

            snifferAgentController = ac.createNewAgent("snif", "jade.tools.sniffer.Sniffer", new Object[0]);
            snifferAgentController.start();

           

            WordServerController = ac.createNewAgent("wordServer", "test1.WordServer", new Object[0]);
            WordServerController.start();

            Thread.sleep(2000);

            client1AgentController = ac.createNewAgent("c1", "test1.Client", new Object[]{"hey"});
            client1AgentController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}