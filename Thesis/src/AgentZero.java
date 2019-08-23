import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.omg.CORBA.Current;

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

	public List<MessageNormal> handleDelay() {
		Collections.sort(this.messageBox);
		List<MessageNormal> msgToSend = new ArrayList<MessageNormal>();

		Iterator it = this.messageBox.iterator();

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

	// -------------- Unsynch Monotonic-------------

	public void sendUnsynchMonotonicMsgs(List<MessageNormal> msgToSend) {
		for (MessageNormal msg : msgToSend) {
			sendUnsynchMonotonicMsg(msg);
		}
	}

	private void sendUnsynchMonotonicMsg(MessageNormal msg) {
		int senderId = msg.getSender().getId();
		AgentField reciever = msg.getReciever();

		if (!(msg instanceof MessageAnyTimeUp) && !(msg instanceof MessageAnyTimeDown)) {

			int senderValue = msg.getSenderValue();
			reciever.reciveMsg(senderId, senderValue, msg.getDate());
			reciever.updateCounterAboveOrBelowMono(senderId);

			Permutation currPermutation = reciever.createCurrentPermutationMonotonic();
			if (Main.anytimeDfs) {
				reciever.addToPermutationPast(currPermutation);

				if (reciever.isAnytimeLeaf()) {
					reciever.addToPermutationToSend(currPermutation);
					// System.out.println("reciever.addToPermutationToSend(currPermutation);");
				} else {
					reciever.iterateOverSonsAndCombineWithInputPermutation(currPermutation);
					// System.out.println("reciever.iterateOverSonsAndCombineWithInputPermutation(currPermutation);");

				}
			}

		} // normal message
		if (Main.anytimeDfs) {
			if (msg instanceof MessageAnyTimeUp) {
				reciever.recieveAnytimeUpMonotonic(msg);
				// System.out.println("reciever.recieveAnytimeUpMonotonic(msg);");
			}
			if (msg instanceof MessageAnyTimeDown) {
				reciever.recieveAnytimeDownMonotonic(msg);
			}

		}
	}

	private Map<Integer, SortedSet<MessageNormal>> getMsgMapByReciever(List<MessageNormal> msgToSend) {

		Map<Integer, SortedSet<MessageNormal>> ans = new HashMap<Integer, SortedSet<MessageNormal>>();
		for (MessageNormal msg : msgToSend) {
			int recieverId = msg.getReciever().getId();
			if (!ans.containsKey(recieverId)) {
				ans.put(recieverId, new TreeSet<MessageNormal>(new ComparatorMsgDate()));
			}
			ans.get(recieverId).add(msg);
		}
		return ans;
	}

	private void anytimeMechanismAfterRecieveMsg(Set<AgentField> agentsRecieved) {
		/*
		 * Probably need to check all possible sequence of mail pick ups
		 */

		for (AgentField reciever : agentsRecieved) {
			Permutation currPermutation = reciever.createCurrentPermutationNonMonotonic();
			List<Permutation> toSend = new ArrayList<Permutation>();
			String reason = "";
			boolean leafFlag = false;
			if (reciever.isAnytimeLeaf()) {
				reciever.addToPermutationToSend(currPermutation);
				reason = "leaf a" + reciever.getId() + "creates a message because counters change";
				leafFlag = true;
			} else {
				// reciever.addToPermutationPast(currPermutation);
				toSend = reciever.tryToCombinePermutation(currPermutation);
				reason = "combine between permutations " + currPermutation;
			}
			/*
			 * if (Main.debug) { printDebugAnytimePermutation(leafFlag, reciever,
			 * currPermutation, toSend, reason); }
			 */
			reciever.addToPermutationPast(currPermutation);
		} // for msgs

	}

	private Set<AgentField> getAgents(Set<Integer> input) {
		Set<AgentField> ans = new HashSet<AgentField>();
		for (Integer i : input) {
			for (AgentField a : agents) {
				if (a.getId() == i) {
					ans.add(a);
					break;
				}
			}
		}
		return ans;
	}

	private void sendUnsynchNonMonotonicByValueMsg(MessageNormal msg) {
		int senderId = msg.getSender().getId();
		AgentField reciever = msg.getReciever();

		if (msgIsNotAnytime(msg)) {
			int senderValue = msg.getSenderValue();
			reciever.reciveMsg(senderId, senderValue, msg.getDate());

			if (!Main.tryAllMailBox) {
				Permutation p = reciever.createCurrentPermutationByValue();
				updateRecieverUponPermutationOneByOne(p, reciever);
			}
		} // normal message

		if (msg instanceof MessageAnyTimeUp) {
			reciever.recieveAnytimeUpBfs(msg);
		}
		if (msg instanceof MessageAnyTimeDown) {
			// still need to do
		}

	}

	private void sendUnsynchNonMonotonicMsg(MessageNormal msg) {
		int senderId = msg.getSender().getId();
		AgentField reciever = msg.getReciever();

		if (msgIsNotAnytime(msg)) {
			int senderValue = msg.getSenderValue();
			reciever.reciveMsg(senderId, senderValue, msg.getDate());
			updateCounterOfReciever(reciever, senderId, msg);

			if (!Main.tryAllMailBox) {
				Permutation currPermutation = reciever.createCurrentPermutationNonMonotonic();
				updateRecieverUponPermutationOneByOne(currPermutation, reciever);
			}
			/*
			 * if (Main.tryAgentRememberSequence) {
			 * reciever.recieveMsgAndPlaceItInSequence(msg); }
			 */

		} // normal message

		if (msg instanceof MessageAnyTimeUp) {
			reciever.recieveAnytimeUpBfs(msg);
		}
		if (msg instanceof MessageAnyTimeDown) {
			// still need to do
		}

	}

	private boolean msgIsNotAnytime(MessageNormal msg) {
		return !(msg instanceof MessageAnyTimeUp) && !(msg instanceof MessageAnyTimeDown);
	}

	private void updateRecieverUponPermutationOneByOne(Permutation currPermutation, AgentField reciever) {
		// Permutation currPermutation =
		// reciever.createCurrentPermutationNonMonotonic();
		if (reciever.isAnytimeLeaf()) {
			reciever.addToPermutationToSend(currPermutation);
		} else {
			reciever.tryToCombinePermutation(currPermutation);
		}
		reciever.addToPermutationPast(currPermutation);

	}

	private void updateCounterOfReciever(AgentField reciever, int senderId, MessageNormal msg) {
		if (Main.trySendSelfCounter) {
			reciever.updateCounterNonMonoWithSelfCounterSent(senderId, msg.getSenderSelfCounter());
		} else {
			reciever.updateCounterNonMono(senderId);
		}

	}

	public void afterDecideTakeActionUnsynchMonotonic(Collection<AgentField> agentsThatChanged, int currentIteration) {
		for (AgentField a : agentsThatChanged) {
			a.setDecisionCounterMonotonic(a.getDecisonCounter() + 1);
			a.setCounterAndValueHistory();
			createUnsynchMsgs(a, currentIteration);
		}
	}

	private void addPermutatioToAnytimeMechanism(AgentField a, Permutation p) {

		if (a.isAnytimeLeaf()) {
			a.addToPermutationToSend(p);
		}

		else {
			a.tryToCombinePermutation(p);
		}
	}

	private void printDebugAnytimePermutation(boolean leaf, AgentField reciever, Permutation currPermutation,
			List<Permutation> toSend, String reason) {
		if (leaf) {

			System.out.println("from: a" + reciever.getId() + ", to: a" + reciever.getAnytimeFather().getId() + ", "
					+ currPermutation + " " + ", reason: " + reason);
		} else {
			for (Permutation toAdd : toSend) {
				reason = reason + "and combined with " + toAdd;
				if (!reciever.isAnytimeTop()) {
					System.out.println("from: a" + reciever.getId() + ", to: a" + reciever.getAnytimeFather().getId()
							+ ", " + toAdd + " " + ", reason: combine between Permutations");
				} else {
					System.out.println("from: a" + reciever.getId() + ", to: ax, " + toAdd
							+ ", reason: combine between Permutations");
				}
			}
		}
	}

	private void createUnsynchMsgs(AgentField currentAgent, int currentIteration) {

		List<AgentField> neighborsAgents = getNeighborsAgents(currentAgent);
		for (AgentField n : neighborsAgents) {
			MessageNormal m;

			if (Main.trySendSelfCounter) {
				int selfCounter = currentAgent.getDecisonCounter();
				m = createUnsynchOneMsgTrySendSelfCounter(currentAgent, n, currentIteration, selfCounter);
			} else {
				m = createUnsynchOneMsg(currentAgent, n, currentIteration);
			}

			this.messageBox.add(m);
		}
	}

	private MessageNormal createUnsynchOneMsg(AgentField sender, AgentField reciever, int currentIteration) {
		int senderValue = sender.getValue();
		int delay = this.createDelay();
		return new MessageNormal(sender, reciever, senderValue, delay, currentIteration);
	}

	private MessageNormal createUnsynchOneMsgTrySendSelfCounter(AgentField sender, AgentField reciever,
			int currentIteration, int selfCounter) {
		int senderValue = sender.getValue();
		int delay = this.createDelay();
		return new MessageNormal(sender, reciever, senderValue, delay, currentIteration, selfCounter);

	}

	public void createAnyTimeUpUnsynchMono(int currentIteration) {
		// List<AgentField> agentsSendAnytime = new ArrayList<AgentField>()
		for (AgentField a : agents) {
			boolean isHead = a.getAnytimeFather() == null;
			if (a.hasAnytimeUpToSend() && !isHead) {
				Set<Permutation> pToSendA = a.getPermutationsToSend();
				for (Permutation p : pToSendA) {
					int delay = this.createDelay();
					MessageNormal m = new MessageAnyTimeUp(a, a.getAnytimeFather(), delay,currentIteration, p);
					this.messageBox.add(m);
				}
				a.removeAllPermutationToSend();
			} // if not had and have something to send
		}
	}

	public void createAnyTimeUpUnsynchNonMonotonic(int currentIteration) {
		// List<AgentField> agentsSendAnytime = new ArrayList<AgentField>()
		for (AgentField a : agents) {
			boolean isHead = a.getAnytimeFather() == null;
			if (a.hasAnytimeUpToSend() && !isHead) {
				Set<Permutation> pToSendA = a.getPermutationsToSend();
				for (Permutation p : pToSendA) {
					// p.createdIncluded(a);
					int delay = this.createDelay();
					MessageNormal m = new MessageAnyTimeUp(a, a.getAnytimeFather(), delay, currentIteration, p);
					this.messageBox.add(m);
				}
				a.removeAllPermutationToSend();
			} // if not had and have something to send
		}
	}

	public void createAnyTimeDownUnsynchMono(List<AgentField> fathers, int date) {
		for (AgentField top : fathers) {
			if (top.isTopHasAnytimeNews()) {
				top.resettopHasAnytimeNews();
				placeAnytimeDownMessageInBox(top, date);
			}
		}

		for (AgentField a : this.agents) {
			if (!a.isAnytimeTop()) {
				MessageAnyTimeDown m = a.moveDownToSend();
				if (m != null) {
					placeAnytimeDownMessageInBox(a, date);
				}
			}
		}
	}

	private void placeAnytimeDownMessageInBox(AgentField from, int date) {
		for (AgentField son : from.getAnytimeSons()) {
			AgentField sender = from;
			AgentField reciever = son;
			int delay = this.createDelay();
			int currentIteration = date;
			Permutation permutationToSend = from.getBestPermutation();
			MessageNormal m = new MessageAnyTimeDown(sender, reciever, -100, delay, currentIteration,
					permutationToSend);
			this.messageBox.add(m);
		}

	}

	public List<MessageNormal> getMsgBox() {
		return this.messageBox;
	}
	/*
	 * public void selfChangeReport(Set<AgentField> didDecide) { for (AgentField a :
	 * didDecide) { a.get }
	 * 
	 * }
	 */

	public void afterDecideTakeActionUnsynchNonMonotonicByCounter(Collection<AgentField> agentsThatChanged, int date) {
		for (AgentField a : agentsThatChanged) {
			a.setDecisionCounterNonMonotonic(a.getDecisonCounter() + 1);
			a.setCounterAndValueHistory(); // //record in map of agent its current value and self counter
			Permutation myPermutation = a.createCurrentPermutationNonMonotonic();
			a.addToPermutationPast(myPermutation);
			/*
			 * if (Main.tryAgentRememberSequence) { a.setDateAndValueHistory(date); //record
			 * in map of agent its current value and date
			 * a.combineCurrentValueWithOtherMessages(); //combine current with latest }
			 * else {
			 * 
			 * }
			 * 
			 */
			createUnsynchMsgs(a, date);
			addPermutatioToAnytimeMechanism(a, myPermutation);
		}
	}

	public void afterDecideTakeActionUnsynchNonMonotonicByValue(Set<AgentField> agentsThatChanged, int date) {
		for (AgentField a : agentsThatChanged) {

			Permutation myPermutation = a.createCurrentPermutationByValue();

			createUnsynchMsgs(a, date);
			addPermutatioToAnytimeMechanism(a, myPermutation);
		}

	}

	public void sendUnsynchNonMonotonicByValueMsgs(List<MessageNormal> msgToSend) {
		Set<Integer> integerRecieved = new HashSet<Integer>();


		Collections.sort(msgToSend, new ComparatorMsgDate());
		Collections.reverse(msgToSend);
	
		//debugToSeeSequence(msgToSend);
		
		

		for (MessageNormal msg : msgToSend) {
			sendUnsynchNonMonotonicByValueMsg(msg);
			integerRecieved.add(msg.getReciever().getId());
		}

		if (Main.tryAllMailBox) {
			Set<AgentField> agentsRecieved = getAgents(integerRecieved);
			anytimeMechanismAfterRecieveMsgByValue(agentsRecieved);
		}

	}

	private void debugToSeeSequence(List<MessageNormal> msgToSend) {
		int minDate = 0;

		if (!msgToSend.isEmpty()) {
			MessageNormal m = Collections.min(msgToSend, new ComparatorMsgDate());
			minDate = m.getDate();
		}
		
		if (msgToSend.size() > 2 && minDate == 2) {
			for (int i = 0; i < msgToSend.size(); i++) {
				System.out.println("location: " + i + ", date:" + msgToSend.get(i).getDate());

			}
		}
		
	}

	private void anytimeMechanismAfterRecieveMsgByValue(Set<AgentField> agentsRecieved) {
		for (AgentField reciever : agentsRecieved) {
			Permutation currPermutation = reciever.createCurrentPermutationByValue();
			updateRecieverUponPermutationOneByOne(currPermutation, reciever);
		} // for msgs
	}

	public void sendUnsynchNonMonotonicMsgs(List<MessageNormal> msgToSend) {
		Set<Integer> integerRecieved = new HashSet<Integer>();

		for (MessageNormal msg : msgToSend) {
			sendUnsynchNonMonotonicMsg(msg);
			integerRecieved.add(msg.getReciever().getId());
		}

		if (Main.tryAllMailBox) {
			Set<AgentField> agentsRecieved = getAgents(integerRecieved);
			anytimeMechanismAfterRecieveMsg(agentsRecieved);
		}
	}
}// class
