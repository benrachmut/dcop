import java.util.List;

public abstract class Dcop {

	protected int itiration;
	protected Dcsp dcsp;
	protected AgentField[] agents;
	protected int cost;
	protected AgentZero agentZero;

	public Dcop (Dcsp dcsp,AgentField[]agents,AgentZero aZ ) {
		this.dcsp = dcsp;
		this.itiration = Main.iterations;
		this.agents = agents;
		this.cost = 0;
		this.agentZero = aZ;
	}

	public int calRealCost() {
		return dcsp.calRealCost();
	}
	
	public int calThoughtCost() {
		int ans = 0;
		for (int i = 0; i < agents.length; i++) {
			ans+=agents[i].getCurrentCost();
		}
		return ans; 
	}
	
	public abstract List<Integer> solve();
	
}
