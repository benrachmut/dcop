
public class MessageR extends Message<Integer> {

	public MessageR(AgentField sender, AgentField reciever, Integer messageInformation, int delay, int date) {
		super(sender, reciever, messageInformation, delay, date);
	}
	public MessageR(AgentField reciever) {
		this(null, reciever, -1, 0, -1);
	}

}
