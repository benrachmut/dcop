import java.util.ArrayList;
import java.util.List;

public class DSA extends Solution {

	private double stochastic;

	public DSA(Dcop dcsp, AgentField[] agents, AgentZero aZ,  int meanRun, double stochastic) {
		super(dcsp, agents, aZ,meanRun);
		this.stochastic = stochastic;
		this.algo = "dsa"+stochastic;
		this.solve();

	}

	@Override
	public void solve() {

		for (int i = 0; i < this.itiration; i++) {
			this.agentZero.createMsgs();
			this.agentZero.sendMsgs();
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
