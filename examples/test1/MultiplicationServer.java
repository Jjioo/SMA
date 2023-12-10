package test1;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class MultiplicationServer extends Agent {

    protected void setup() {
        // Register the service in the DF
        try {
            registerService("math-server");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // Listen for incoming messages
        addBehaviour(new ReceiveRequestBehaviour());
    }

    private void registerService(String serviceType) throws FIPAException {
        // Register the service in the DF
        jade.domain.DFService.register(this, createDFAgentDescription(serviceType));
    }

    private jade.domain.FIPAAgentManagement.DFAgentDescription createDFAgentDescription(String serviceType) {
        jade.domain.FIPAAgentManagement.DFAgentDescription dfd = new jade.domain.FIPAAgentManagement.DFAgentDescription();
        dfd.setName(getAID());
        jade.domain.FIPAAgentManagement.ServiceDescription sd = new jade.domain.FIPAAgentManagement.ServiceDescription();
        sd.setType(serviceType);
        sd.setName(getLocalName() + "*" + serviceType);
        dfd.addServices(sd);
        return dfd;
    }

    private class ReceiveRequestBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Listen for REQUEST messages
            ACLMessage msg = receive();
            if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                // Process the request and send the result back
                Object[] args = null;
                try {
                    args = (Object[]) msg.getContentObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (args != null && args.length >= 2) {
                    // Perform multiplication
                    int result = performMultiplication(args);

                    // Send the result back to the ClientAgent
                    sendResultToClient(msg.getSender(), result);

                    // Additional logging
                    System.out.println(getLocalName() + ": Received request from " + msg.getSender().getLocalName() +
                            " to perform multiplication on " + args[0] + " and " + args[1] + ". Result: " + result);
                } else {
                    System.err.println("Invalid number of arguments. Please provide at least two values for multiplication.");
                }
            } else {
                block();
            }
        }

        private void sendResultToClient(jade.core.AID clientAID, int result) {
            // Send the result back to the ClientAgent
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.addReceiver(clientAID);
            message.setContent(String.valueOf(result));
            myAgent.send(message);
        }

        private int performMultiplication(Object[] args) {
            int result = extractOperand(args[0]);
            for (int i = 1; i < args.length; i++) {
                result *= extractOperand(args[i]);
            }
            return result;
        }

        private int extractOperand(Object arg) {
            try {
                return Integer.parseInt(arg.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid operand: " + arg);
            }
        }
    }
}
