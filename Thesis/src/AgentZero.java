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
	private Set<Neighbors> neighbors;
	private int iteration;
	private List<Message> rMessageBox;
	private double p3;
	private double p4;
	private int delayUb;

	public AgentZero(int iteration, Set<Neighbors> neighbors) {
		this.messageBox = new ArrayList<Message>();
		rMessageBox = new ArrayList<Message>();
		this.neighbors = neighbors;
		this.iteration = iteration;
	}

	

	public void createMsgs(int currentIteration) {
		for (Neighbors n : this.neighbors) {

			AgentField a1 = (AgentField) n.getA1();
			int delay12 = n.getDelay12(currentIteration);

			AgentField a2 = (AgentField) n.getA2();
			int delay21 = n.getDelay21(currentIteration);

			
			Message msg12 = new Message(a1, a2, a1.getValue(), delay12,currentIteration);
			Message msg21 = new Message(a2, a1, a2.getValue(), delay21,currentIteration);

			

			this.messageBox.add(msg12);
			this.messageBox.add(msg21);
		}

	}

	


	private List<Message> handleDelay(List<Message>input) {
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
			n.createFluds(p3, delayUb,p4);
		}
		
	}

	public void sendRiMsgs() {
		List<Message> msgToSend = handleDelay(this.rMessageBox);
		for (Message msg : msgToSend) {
			int senderId= msg.getSender().getId();
			int senderR = msg.getSenderValue();
			AgentField reciver = msg.getReciever();
			
			reciver.reciveRMsg(senderId,senderR,msg.getDate());
			
		}
		
	}
	
	
	public void sendMsgs() {

		List<Message> msgToSend = handleDelay(this.messageBox);
		for (Message msg : msgToSend) {
			int senderId= msg.getSender().getId();
			int senderValue = msg.getSenderValue();
			AgentField reciver = msg.getReciever();
			
			reciver.reciveMsg(senderId,senderValue,msg.getDate());
			
		}
	

	}

	

	public void createRiMsgs(int currentIteration) {
		for (Neighbors n : this.neighbors) {

			AgentField a1 = (AgentField) n.getA1();
			int delay12 = n.getDelay12(currentIteration);

			AgentField a2 = (AgentField) n.getA2();
			int delay21 = n.getDelay21(currentIteration);

			
			Message msg12 = new Message(a1, a2, a1.getR(), delay12,currentIteration);
			Message msg21 = new Message(a2, a1, a2.getR(), delay21,currentIteration);

			

			this.rMessageBox.add(msg12);
			this.rMessageBox.add(msg21);
		}

	}

	public void emptyRMessageBox() {
		this.rMessageBox.clear();
		
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
	
	
	
	

}
