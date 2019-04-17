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
	private List<Message> messagesBox;
	private Set<Neighbors> neighbors;
	private int iteration;

	public AgentZero(int iteration, Set<Neighbors> neighbors) {
		this.messagesBox = new ArrayList<Message>();
		this.neighbors = neighbors;
		this.iteration = iteration;
	}

	public void emptyMessageBox() {
		this.messagesBox.clear();
	}

	public void createMsgs(int currentIteration) {
		for (Neighbors n : this.neighbors) {
			AgentField a1 = (AgentField) n.getA1();
			int delay12 = n.getDelay12(currentIteration);

			AgentField a2 = (AgentField) n.getA2();
			int delay21 = n.getDelay21(currentIteration);

			
			Message msg12 = new Message(a1, a2, a1.getValue(), delay12, currentIteration);
			this.messagesBox.add(msg12);

			
			Message msg21 = new Message(a2, a1, a2.getValue(), delay21, currentIteration);

	  
			this.messagesBox.add(msg21);
		}

	}


	
	public void sendMsgs() {

		List<Message> msgToSend = handleDelay();
		for (Message msg : msgToSend) {
			int senderId= msg.getSender().getId();
			int senderValue = msg.getSenderValue();
			AgentField reciver = msg.getReciever();
			
			reciver.reciveMsg(senderId,senderValue);
		}
		/*
		for (Message m : this.messagesBox) {
			int delay=m.getDelay()-1;
			m.setDelay(delay);
		}
		*/

	}

	private List<Message> handleDelay() {
		Collections.sort(this.messagesBox);
		List<Message> msgToSend = new ArrayList<Message>();

		Iterator<Message> it = messagesBox.iterator();

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



	public void changeCommunicationProtocol(double p3) {
		for (Neighbors n : this.neighbors) {
			n.createFluds(p3);
		}
		
	}
	
	

}
