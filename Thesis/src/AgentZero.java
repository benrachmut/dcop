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
	private List<MessageNormal> messageBox;
	private List<MessageNormal> rMessageBox;
	private List<MessageNormal> timeStempMessageBox;

	private Set<Neighbors> neighbors;
	private int iteration;
	private double p3;
	private double p4;
	private int delayUb;
	private AgentField[] agents;

	public AgentZero(int iteration, Set<Neighbors> neighbors, AgentField[] agents) {
		this.agents = agents;
		this.messageBox = new ArrayList<MessageNormal>();
		rMessageBox = new ArrayList<MessageNormal>();
		timeStempMessageBox = new ArrayList<MessageNormal>();
		this.neighbors = neighbors;
		this.iteration = iteration;
	}

	public void createMsgs(int currentIteration) {
		for (Neighbors n : this.neighbors) {

			AgentField a1 = (AgentField) n.getA1();

			AgentField a2 = (AgentField) n.getA2();

			int delay12 = createDelay();
			int delay21 = createDelay();

			// int delay12 = n.getDelay12(currentIteration);
			// int delay21 = n.getDelay21(currentIteration);

			MessageNormal msg12 = new MessageNormal(a1, a2, a1.getValue(), delay12, currentIteration);
			MessageNormal msg21 = new MessageNormal(a2, a1, a2.getValue(), delay21, currentIteration);

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
			// int delay12 = n.getDelay12(currentIteration);
			// int delay21 = n.getDelay21(currentIteration);

			MessageNormal msg12 = new MessageNormal(a1, a2, a1.getR(), delay12, currentIteration);
			MessageNormal msg21 = new MessageNormal(a2, a1, a2.getR(), delay21, currentIteration);

			this.rMessageBox.add(msg12);
			this.rMessageBox.add(msg21);
		}

	}

	public void emptyRMessageBox() {
		this.rMessageBox.clear();

	}

	private List<MessageNormal> handleDelay(List<MessageNormal> input) {
		Collections.sort(input);
		List<MessageNormal> msgToSend = new ArrayList<MessageNormal>();

		Iterator it = input.iterator();

		while (it.hasNext()) {
			MessageNormal msg = (MessageNormal) it.next();
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
		List<MessageNormal> msgToSend = handleDelay(this.rMessageBox);
		for (MessageNormal msg : msgToSend) {
			int senderId = msg.getSender().getId();
			int senderR = msg.getSenderValue();
			AgentField reciver = msg.getReciever();
			reciver.reciveRMsg(senderId, senderR, msg.getDate());
		}

	}

	public void sendMsgs() {
		List<MessageNormal> msgToSend = handleDelay(this.messageBox);
		for (MessageNormal msg : msgToSend) {
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
		return this.delayUb;
	}

	public double getP3() {
		return this.p3;
	}


	public void emptyTimeStempBoxMessage() {
		this.timeStempMessageBox.clear();

	}

	private int createDelay() {
		int rndDelay;
		rndDelay = 0;
		double rnd = Main.rP3.nextDouble();
		if (rnd < Main.currentP3) {
			rndDelay = Main.getRandomInt(Main.rDelay, 1, Main.currentUb);
			rnd = Main.rP4.nextDouble();
			if (rnd < Main.currentP4) {
				rndDelay = Integer.MAX_VALUE;
			}

		}
		return rndDelay;
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

		List<MessageNormal> msgToSend = handleDelay(this.messageBox);
		// boolean hasMsgUpFlag = false;
		//Set<MessageAnyTimeUp> dueToMessageUp = new HashSet<MessageAnyTimeUp>();
		//Set<AgentField> dueToChangeInCounter = new HashSet<AgentField>();

		for (MessageNormal msg : msgToSend) {
			int senderId = msg.getSender().getId();
			AgentField reciever = msg.getReciever();

			if (msg instanceof MessageAnyTimeUp) {				
				MessageAnyTimeUp mau = (MessageAnyTimeUp) msg;	
				reciever.createPermutataionsDueToMessage(mau);	
								
			} else {

				int senderValue = msg.getSenderValue();
				reciever.reciveUnsynchMsg(senderId, senderValue, msg.getDate());
				
				if (Main.anyTime) {
					if (reciever.isLeaf()) {
						reciever.leafAddAnytimeUp();
					} else {
						reciever.createPermutataionsDueChangeInCounter();
					}
				}
			} // if msg is with value
		}
	}



	public void iterateOverWhoCanDecide(List<AgentField> whoCanDecide, int currentIteration) {
		for (AgentField a : whoCanDecide) {
			a.setDecisionCounter(a.getDecisonCounter() + 1);
			a.setCounterAndValueHistory();
			createUnsynchMsgs(a, currentIteration);
		}
	}

	private void createUnsynchMsgs(AgentField currentAgent, int currentIteration) {

		List<AgentField> neighborsAgents = getNeighborsAgents(currentAgent);
		for (AgentField n : neighborsAgents) {
			MessageNormal m = createUnsynchOneMsg(currentAgent, n, currentIteration);
			this.messageBox.add(m);

		}
	}

	
	private MessageNormal createUnsynchOneMsg(AgentField sender, AgentField reciever, int currentIteration) {

		int senderValue = sender.getValue();
		int delay = this.createDelay();

		return new MessageNormal(sender, reciever, senderValue, delay, currentIteration);

	}

	public void createAnyTimeUp() {
		// List<AgentField> agentsSendAnytime = new ArrayList<AgentField>()
		for (AgentField a : agents) {
			boolean isHead = a.getFather() == null;
			
			if (a.hasAnytimeUpToSend() && !isHead) {
				Set<Permutation> pToSendA = a.getPermutationsToSend();
				for (Permutation p : pToSendA) {
					int delay = this.createDelay();

					MessageNormal m = new MessageAnyTimeUp(a, a.getFather(), delay, p);
					this.messageBox.add(m);
				}
				a.removeAllPermutationToSend();
			} // if not had and have something to send
			
		
		}
	}

	public void createAnyTimeDownFather(List<AgentField> fathers, int date) {
		for (AgentField top : fathers) {
			if (top.isTopHasAnytimeNews()) {
				top.resettopHasAnytimeNews();
				placeMessageInBox(top, date);
			}
		}
		
	}

	private void placeMessageInBox(AgentField from, int date) {
		
		for (AgentField son : from.getSons()) {
			AgentField sender = from;
			AgentField reciever = son;
			int delay = this.createDelay();
			int currentIteration = date;
			Permutation permutationToSend = from.getBestPermutation();
			MessageNormal m = new MessageAnyTimeDown(sender, reciever, -100, delay, currentIteration, permutationToSend);
			
		}

	}
	
}// class
