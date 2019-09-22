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
	protected List<Integer> topAnytimeCost;

	protected List<Integer> agentThinkCost;
	protected int currentItiration;
	protected String algo;
	public static Dcop dcopS;
	
	public static int counterCentralisticChanges;
	protected List<Integer> counterChanges;



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
		topAnytimeCost = new ArrayList<Integer>();
		this.agentThinkCost = new ArrayList<Integer>();
		counterChanges = new ArrayList<Integer>();
		counterCentralisticChanges=0;
		addCostToList(0);
		


	}

	public int calRealCost() {
		return dcop.calCost(true);
	}

	public void addCostToList(int i) {		
		int currentCost = dcop.calCost(true);
		this.realCost.add(currentCost);
		
		if (i == 0) {
			counterCentralisticChanges = 0;
		}else {

			if (currentCost!=this.realCost.get(i-1)) {
				counterCentralisticChanges = counterCentralisticChanges+1;
			}		
		}	
		counterChanges.add(counterCentralisticChanges);
	}
	
	public void addAnytimeCost() {
		this.anytimeCost.add(dcop.calCost(false));
	}

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
			return algo + "," + Main.currentHub+ "," + Main.currentP2ScaleFree + "," + meanRun;

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

	public int getTopCost(int i) {
		// TODO Auto-generated method stub
		return this.topAnytimeCost.get(i);
	}

	public List<Integer> getRealCosts() {
		// TODO Auto-generated method stub
		return this.realCost;
	}

	public int getCounterChanges(int i) {
		// TODO Auto-generated method stub
		return this.counterChanges.get(i);
	}



}
