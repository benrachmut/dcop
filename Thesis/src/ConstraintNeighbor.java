
public class ConstraintNeighbor {
	private Agent a;

	private int cost;

	public ConstraintNeighbor(Agent a,  int cost) {
		this.a = a;
		this.cost = cost;		
	}

	public Agent getAgent() {
		return new Agent(a);
	}

	public int getCost() {
		return cost;
	}
	
	@Override
	public String toString() {
		
		return "["+a+" cost:"+cost+"]";
	}

}
