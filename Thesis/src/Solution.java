import java.util.ArrayList;
import java.util.List;

public abstract class Solution {

	protected int itiration;
	protected int meanRun;
	protected Dcop dcop;
	protected AgentField[] agents;
	protected int cost;
	protected AgentZero agentZero;
	protected List<Integer> realCost;
	protected List<Integer> agentThinkCost;
	protected int currentItiration;
	protected String algo;

	public Solution(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		this.meanRun = meanRun + 1;
		this.dcop = dcop;
		this.itiration = Main.iterations;
		this.agents = agents;
		this.cost = 0;
		this.agentZero = aZ;
		this.algo = "";
		this.realCost = new ArrayList<Integer>();
		this.agentThinkCost = new ArrayList<Integer>();
		addCostToList();

	}

	public int calRealCost() {
		return dcop.calRealCost();
	}

	public void addCostToList() {

		this.realCost.add(dcop.calRealCost());
		
		// try cal self cost, for debbuging
		//trySelfCost();
		
		
		
	}

	private void trySelfCost() {
		
		int ans = 0;
		for (AgentField a : this.agents) {
			ans = ans+ a.calSelfCost();
		}
		ans = ans/2;
		this.realCost.add(ans);
		
	}

	public abstract void solve();

	public abstract void agentDecide();

	@Override
	public String toString() {
		

		return algo + "," + Main.currentP1 + "," + Main.currentP2 + "," + meanRun;
	}

	public List<Integer> getRealCost() {
		return realCost;
	}

	public List<Integer> getAgentThinkCost() {
		return agentThinkCost;
	}

	public void sendAndRecieve(int i) {
		this.agentZero.createMsgs(i);
		this.agentZero.sendMsgs();
	}

}
