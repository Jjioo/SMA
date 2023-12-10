package test1;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

public class Client extends Agent {

    protected void setup() {
        // Search for all available servers in the DF
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("word-serve");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            for (DFAgentDescription server : result) {
                AID serverAID = server.getName();
                Object[] args = getArguments(); // Example arguments
                sendRequestToServer(serverAID, args);
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // Add a behavior to handle responses from servers
        addBehaviour(new ReceiveServerResponseBehaviour());
    }

    private void sendRequestToServer(AID serverAID, Object[] args) {
        // Send a message to the server
        addBehaviour(new RequestOperationBehaviour(serverAID, args));
    }

    private class RequestOperationBehaviour extends OneShotBehaviour {
        private AID serverAID;
        private Object[] arguments;

        RequestOperationBehaviour(AID serverAID, Object[] args) {
            this.serverAID = serverAID;
            this.arguments = args;
        }

        @Override
        public void action() {
            // Send a request message to the server
            try {
                myAgent.send(createRequestMessage(serverAID, arguments));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private ACLMessage createRequestMessage(AID receiver, Object[] args) throws IOException {
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            message.addReceiver(receiver);
            message.setContentObject(args);
            return message;
        }
    }

    private class ReceiveServerResponseBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            // Receive and process responses from servers
            ACLMessage response = receive();
            if (response != null) {
                System.out.println("Received response from " + response.getSender().getLocalName() + ": " + response.getContent());
            } else {
                block();
            }
        }
    }
}
