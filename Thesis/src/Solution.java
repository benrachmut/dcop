import java.util.ArrayList;
import java.util.List;

public abstract class Solution {

	protected int iteration;
	protected int meanRun;
	protected Dcop dcop;
	protected AgentField[] agents;
	protected int cost;
	protected AgentZero agentZero;
	protected List<Integer> realCost;
	protected List<Integer> fatherCost;
	protected List<Integer> anytimeCost;
	protected List<Integer> agentThinkCost;
	protected int currentItiration;
	protected String algo;
	public static Dcop dcopS;

	public Solution(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		this.meanRun = meanRun + 1;
		this.dcop = dcop;
		dcopS = dcop;
		this.iteration = Main.iterations;
		this.agents = agents;
		this.cost = 0;
		this.agentZero = aZ;
		this.algo = "";	
		this.realCost = new ArrayList<Integer>();
		this.fatherCost = new ArrayList<Integer>();
		this.anytimeCost = new ArrayList<Integer>();
		this.agentThinkCost = new ArrayList<Integer>();
		addCostToList();
		


	}

	public int calRealCost() {
		return dcop.calCost(true);
	}

	public void addCostToList() {

		this.realCost.add(dcop.calCost(true));
	}
	
	public void addAnytimeCost() {
		this.anytimeCost.add(dcop.calCost(false));
	}
/*
	private void trySelfCost() {
		
		int ans = 0;
		for (AgentField a : this.agents) {
			ans = ans+ a.calSelfCost();
		}
		ans = ans/2;
		this.realCost.add(ans);
		
	}
	*/

	public abstract void solve();

	public abstract void agentDecide(int i);

	@Override
	public String toString() {
		
		if (Main.dcopVersion ==1) {
			return algo + "," + Main.currentP1Uniform + "," + Main.currentP2Uniform + "," + meanRun;

		}
		if (Main.dcopVersion ==2) {
			return algo + "," + Main.currentP1Color + ",-," + meanRun;

		}
		if (Main.dcopVersion ==3) {
			---
		}
		return "";
	}

	

	public List<Integer> getAgentThinkCost() {
		return agentThinkCost;
	}

	public void sendAndRecieve(int i) {
		this.agentZero.createMsgs(i);
		this.agentZero.sendMsgs();
	}

	public int getFatherCost(int i ) {
		return this.fatherCost.get(i);
	}
	
	public int getRealCost(int i ) {
		return realCost.get(i);
	}

	public void addAnytimeCostToList() {
		this.anytimeCost.add(dcop.calCost(false));
		
	}

	public int getAnytimeCost(int i) {
		// TODO Auto-generated method stub
		return this.anytimeCost.get(i);
	}



}
