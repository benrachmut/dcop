
public class MessageValue extends Message <Integer>  {

	private int decisionCounter;
	public MessageValue(AgentField sender, AgentField reciever, int senderValue, int delay, int currentIteration) {
		super(sender, reciever,senderValue, delay, currentIteration);
	}
	public MessageValue(MessageValue m) {
		this( m.getSender(),  m.getReciever(),  m.getMessageInformation(),  m.getDelay(),  m.getDate());
	}
	public MessageValue(AgentField reciever ) {
		this( null,  reciever,  -1,  0,  -1);
	}
	public MessageValue(AgentField sender, AgentField reciever, int senderValue, int delay, int currentIteration,
			int senderDecisionCounter) {
		super(sender, reciever,senderValue, delay, currentIteration);
		this.decisionCounter = senderDecisionCounter;
	}
	public int getDecisionCounter() {
		return this.decisionCounter;
	}
	
	
}
