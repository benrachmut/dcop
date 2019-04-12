
public class Message implements Comparable<Message> {
private Agent send;
private Agent recieve;
private int senderValue;
private int itirationCreated;
private int delay;
public Message(Agent send, Agent recieve,int itiration, int senderValue) {
	super();
	this.send = send;
	this.recieve = recieve;
	this.senderValue = senderValue;
	this.itirationCreated = itiration;
	this.delay = 0;
}
@Override
public int compareTo(Message o) {
	return this.delay-o.getDelay();
}
private int getDelay() {
	return this.delay;
}

}
