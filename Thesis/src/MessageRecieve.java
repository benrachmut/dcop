
public class MessageRecieve {
private int iterationCreated;
private int variable;
public MessageRecieve( int value,int iterationCreated) {
	super();
	this.iterationCreated = iterationCreated;
	this.variable = value;
}
public int getIterationCreated() {
	return iterationCreated;
}
public void setIterationCreated(int iterationCreated) {
	this.iterationCreated = iterationCreated;
}
public int getVariable() {
	return variable;
}
public void setValue(int variable) {
	this.variable = variable;
}


}
