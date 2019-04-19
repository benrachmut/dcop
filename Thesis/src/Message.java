
public class Message implements Comparable<Message> {
	private AgentField sender;
	private AgentField reciever;
	private int senderValue;
	private int delay;

	public Message(AgentField sender, AgentField reciever, int senderValue, int delay) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.senderValue = senderValue;
		this.delay = delay;
	}
/*
	public Message(AgentField sender, AgentField reciever, int senderValue) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.senderValue = senderValue;
		this.delay = 0;
	}
	*/

	@Override
	public int compareTo(Message o) {
		return this.delay - o.getDelay();
	}

	public int getDelay() {
		return this.delay;
	}

	public void setDelay(int input) {
		this.delay = input;

	}

	public AgentField getSender() {
		return sender;
	}

	public AgentField getReciever() {
		return reciever;
	}

	public int getSenderValue() {
		return senderValue;
	}
	
	
	

}
