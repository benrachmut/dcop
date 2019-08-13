import java.util.ArrayList;
import java.util.List;

public class DSA extends Solution {

	private double stochastic;

	public DSA(Dcop dcop, AgentField[] agents, AgentZero aZ,  int meanRun, double stochastic) {
		super(dcop, agents, aZ,meanRun);
		this.stochastic = stochastic;				
		this.algo = "dsa";

		

	}

	@Override
	public void solve() {

		for (int i = 0; i < this.iteration; i++) {
			this.sendAndRecieve(i);
			
			agentDecide(i);
			addCostToList();

		}
	}
	@Override

	public void agentDecide(int i) {
		for (AgentField a : agents) {
			a.dsaDecide(this.stochastic);
		}
	}

	



}
