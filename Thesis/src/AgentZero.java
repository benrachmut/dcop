import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class AgentZero {
	private List<Message> messageBox;
	private List<Message> rMessageBox;
	private List<Message> timeStempMessageBox;

	private Set<Neighbors> neighbors;
	private int iteration;
	private double p3;
	private double p4;
	private int delayUb;
	private AgentField[] agents;

	public AgentZero(int iteration, Set<Neighbors> neighbors, AgentField[] agents) {
		this.agents = agents;
		this.messageBox = new ArrayList<Message>();
		rMessageBox = new ArrayList<Message>();
		timeStempMessageBox = new ArrayList<Message>();
		this.neighbors = neighbors;
		this.iteration = iteration;
	}

	public void createMsgs(int currentIteration) {
		for (Neighbors n : this.neighbors) {

			AgentField a1 = (AgentField) n.getA1();
			

			AgentField a2 = (AgentField) n.getA2();
			
			int delay12 = createDelay();
			int delay21 = createDelay();
			
			//int delay12 = n.getDelay12(currentIteration);
			//int delay21 = n.getDelay21(currentIteration);

			Message msg12 = new Message(a1, a2, a1.getValue(), delay12, currentIteration);
			Message msg21 = new Message(a2, a1, a2.getValue(), delay21, currentIteration);

			this.messageBox.add(msg12);
			this.messageBox.add(msg21);
		}

	}
	public void createRiMsgs(int currentIteration) {
		for (Neighbors n : this.neighbors) {

			AgentField a1 = (AgentField) n.getA1();

			AgentField a2 = (AgentField) n.getA2();
			int delay12 = createDelay();
			int delay21 = createDelay();
			//int delay12 = n.getDelay12(currentIteration);
			//int delay21 = n.getDelay21(currentIteration);

			Message msg12 = new Message(a1, a2, a1.getR(), delay12, currentIteration);
			Message msg21 = new Message(a2, a1, a2.getR(), delay21, currentIteration);

			this.rMessageBox.add(msg12);
			this.rMessageBox.add(msg21);
		}

	}

	public void emptyRMessageBox() {
		this.rMessageBox.clear();

	}

	private List<Message> handleDelay(List<Message> input) {
		Collections.sort(input);
		List<Message> msgToSend = new ArrayList<Message>();

		Iterator<Message> it = input.iterator();

		while (it.hasNext()) {
			Message msg = (Message) it.next();
			if (msg.getDelay() == 0) {
				msgToSend.add(msg);
				it.remove();
			} else {
				msg.setDelay(msg.getDelay() - 1);
			}

		}

		return msgToSend;
	}

	public void changeCommunicationProtocol(double p3Input, int delayUbInput, Double p4Input) {
		this.p3 = p3Input;
		this.delayUb = delayUbInput;
		this.p4 = p4Input;

		for (Neighbors n : this.neighbors) {
			n.createFluds(p3, delayUb, p4);
		}

	}

	public void sendRiMsgs() {
		List<Message> msgToSend = handleDelay(this.rMessageBox);
		for (Message msg : msgToSend) {
			int senderId = msg.getSender().getId();
			int senderR = msg.getSenderValue();
			AgentField reciver = msg.getReciever();

			reciver.reciveRMsg(senderId, senderR, msg.getDate());

		}

	}

	public void sendMsgs() {

		List<Message> msgToSend = handleDelay(this.messageBox);
		for (Message msg : msgToSend) {
			int senderId = msg.getSender().getId();
			int senderValue = msg.getSenderValue();
			AgentField reciver = msg.getReciever();

			reciver.reciveMsg(senderId, senderValue, msg.getDate());

		}

	}



	public void emptyMessageBox() {
		this.messageBox.clear();
	}

	public int getUb() {
		// TODO Auto-generated method stub
		return this.delayUb;
	}

	public double getP3() {
		// TODO Auto-generated method stub
		return this.p3;
	}
/*
	public void createTimeStempMsgs(int currentIteration) {

		for (AgentField father : agents) {
			for (AgentField son : father.getSons()) {

				int fatherId = father.getId();
				int sonId = son.getId();
				Neighbors n;
				int delayFatherSon;
				if (fatherId<sonId) {
					n = lookForNeighbor(father, son);
					delayFatherSon = n.getDelay12(currentIteration);
				}else {
					n = lookForNeighbor(son, father);
					delayFatherSon = n.getDelay21(currentIteration);
				}
				
	
				Message m = new Message(father,son, father.getTimeStemp(), delayFatherSon, currentIteration);
				this.timeStempMessageBox.add(m);
				// Neighbor n =

				
			}
		}

		

	}
*/
/*
	private Neighbors lookForNeighbor(AgentField a1, AgentField a2) {
		
		Neighbors inputN = new Neighbors(a1, a2);
		for (Neighbors n : this.neighbors) {
			if (inputN.equals(n)) {
				return n;
			}
		}
		return null;
	}
*/
/*
	public void sendTimeStempMsgs() {
		List<Message> msgToSend = handleDelay(this.timeStempMessageBox);
		for (Message msg : msgToSend) {
			int senderId = msg.getSender().getId();
			int senderValue = msg.getSenderValue();
			AgentField reciver = msg.getReciever();
			reciver.reciveTimeStempMsg(senderId, senderValue, msg.getDate());

		}
		
	}
*/
	public void emptyTimeStempBoxMessage() {
		this.timeStempMessageBox.clear();
		
	}
	
	private int createDelay() {
		double rnd = Main.rProblem.nextDouble();
		int rndDelay;
		rndDelay = Integer.MAX_VALUE;

		if (rnd<Main.currentP3) {			
			rndDelay = Main.getRandomInt(Main.rProblem,1, Main.currentUb);
			rnd = Main.rProblem.nextDouble();
			if (rnd<Main.currentP4) {			
				rndDelay = Integer.MAX_VALUE;
			}	
			
		}else {
			rndDelay =0;
		}
		return rndDelay;
	}

	public void createUnsynchMessage(List<AgentField> whoCanDecide, int currentIteration) {
		for (AgentField a : whoCanDecide) {
			a.setDecisionCounter(a.getDecisonCounter()+1);
			List<AgentField> neighborsAgents = getNeighborsAgents(a);
			for (AgentField n : neighborsAgents) {
				AgentField sender =a;
				AgentField reciever =n;
				int senderValue = a.getValue();
				int delay = this.createDelay();
				Message m = new Message(sender, reciever, senderValue, delay, currentIteration);
				this.messageBox.add(m);
			}
		}
		
	}

	private List<AgentField> getNeighborsAgents(AgentField a) {
		List<AgentField> ans = new ArrayList<AgentField>();
		for (Integer neigborNumber : a.getNeighborIds()) {
			for (AgentField aTemp : agents) {
				if (aTemp.getId() == neigborNumber) {
					ans.add(aTemp);
				}
			}
		}
		return ans;
	}

	public void sendUnsynchMsgs() {

		List<Message> msgToSend = handleDelay(this.messageBox);
		for (Message msg : msgToSend) {
			int senderId = msg.getSender().getId();
			int senderValue = msg.getSenderValue();
			AgentField reciver = msg.getReciever();

			reciver.reciveUnsynchMsg(senderId, senderValue, msg.getDate());
			
		}
		
	}

}
