
public class MessageValue extends Message <Integer>  {
	
	private int senderValue;
	//private int senderSelfCounter;

	public MessageValue(AgentField sender, AgentField reciever, int senderValue, int delay, int currentIteration) {
		super(sender, reciever,senderValue, delay, currentIteration);
	}
/*
	public MessageNormal(AgentField sender, AgentField reciever, int senderValue, int delay, int currentIteration, int senderSelfCounter) {
		this( sender,  reciever,  senderValue,  delay,  currentIteration);
	
	}
*/

	@Override
	public String toString() {
		return "sender:"+sender+", reciver:"+reciever+", sender value:"+senderValue+", delay:"+this.delay;
	}
	
	public MessageValue(MessageValue m) {
		this( m.getSender(),  m.getReciever(),  m.getMessageInformation(),  m.getDelay(),  m.getDate());
	}

	
/*
	public int getSenderValue() {
		return senderValue;
	}
*/
	
	

}
