import java.util.ArrayList;
import java.util.List;

public abstract class Dcop {

	protected int itiration;
	protected Dcsp dcsp;
	protected AgentField[] agents;
	protected int cost;
	protected AgentZero agentZero;
	protected List<Integer>realtCost;
	protected List<Integer>agentThinkCost;

	public Dcop (Dcsp dcsp,AgentField[]agents,AgentZero aZ ) {
		this.dcsp = dcsp;
		this.itiration = Main.iterations;
		this.agents = agents;
		this.cost = 0;
		this.agentZero = aZ;
		
		this.realtCost = new ArrayList<Integer>();
		this.agentThinkCost = new ArrayList<Integer>();

	}

	public int calRealCost() {
		return dcsp.calRealCost();
	}
	
	public void addCostToList() {
		this.realtCost.add(dcsp.calRealCost());
		
		
		
		int temp = 0;
		for (AgentField a : this.agents) {
			temp+=a.getCurrentThinkCost();
		}
		this.agentThinkCost.add(temp);
	}
	
	public abstract void solve();
	public abstract void agentDecide();
	
}
