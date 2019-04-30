import java.util.ArrayList;
import java.util.List;

public class DSA extends Solution {

	private double stochastic;

	public DSA(Dcop dcop, AgentField[] agents, AgentZero aZ,  int meanRun, double stochastic) {
		super(dcop, agents, aZ,meanRun);
		this.stochastic = stochastic;				
		this.algo = "dsa";
		
		//this.solve();

	}

	@Override
	public void solve() {

		for (int i = 0; i < this.itiration; i++) {
			this.sendAndRecieve(i);
			
			agentDecide();
			addCostToList();

		}
	}
	@Override

	public void agentDecide() {
		for (AgentField a : agents) {
			a.dsaDecide(this.stochastic);
		}
	}

	



}
