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
	private int itirationGap;

	public AgentZero(int itirationGap, Set<Neighbors> neighbors) {
		this.messagesBox = new ArrayList<Message>();
		this.neighbors = neighbors;
		this.itirationGap = itirationGap;
	}

	public void emptyMessageBox() {
		this.messagesBox.clear();
	}

	public void createMsgs() {
		for (Neighbors n : this.neighbors) {
			AgentField a1 = (AgentField) n.getA1();
			AgentField a2 = (AgentField) n.getA2();
			Message msg12 = createMsg(a1, a2, a1.getValue(), n.isDelay12());
			Message msg21 = createMsg(a2, a1, a2.getValue(), n.isDelay21());

			this.messagesBox.add(msg12);
			this.messagesBox.add(msg21);
		}

	}

	private Message createMsg(AgentField a1, AgentField a2, int value, boolean isDelay) {

		Message msg;
		if (isDelay) {
			msg = new Message(a1, a2, value, this.itirationGap);
		} else {
			msg = new Message(a1, a2, value);
		}
		return msg;
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

	public int getItirationGap() {
		return itirationGap;
	}
	
	

}
