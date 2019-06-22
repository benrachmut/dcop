
public class MessageNormal implements Comparable<MessageNormal> {
	private AgentField sender;
	private AgentField reciever;
	private int senderValue;
	private int delay;
	private int date;

	public MessageNormal(AgentField sender, AgentField reciever, int senderValue, int delay, int currentIteration) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.senderValue = senderValue;
		this.delay = delay;
		this.date = currentIteration;
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

	public MessageNormal(MessageNormal m) {
		super();
		this.sender = m.getSender();
		this.reciever = m.getReciever();
		this.senderValue = m.getSenderValue();
		this.delay = m.getDelay();
		this.date = m.getDate();
	}

	public int getDate() {
		return this.date;
	}
	@Override
	public int compareTo(MessageNormal o) {
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
