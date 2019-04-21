import java.util.ArrayList;
import java.util.List;

public class DSA extends Solution {

	private double stochastic;
	private boolean agentWait;

	public DSA(Dcop dcop, AgentField[] agents, AgentZero aZ,  int meanRun, double stochastic, boolean agentWait) {
		super(dcop, agents, aZ,meanRun);
		this.stochastic = stochastic;
		this.agentWait= agentWait;
		
		if (agentWait) {
			this.algo = "dsaW"+stochastic;
		}else {
			this.algo = "dsaD"+stochastic;
		}
		this.solve();

	}

	@Override
	public void solve() {

		for (int i = 0; i < this.itiration; i++) {
			this.agentZero.createMsgs(i);
			this.agentZero.sendMsgs();
			agentDecide();
			addCostToList();

		}
	}
	@Override

	public void agentDecide() {
		for (AgentField a : agents) {
			a.dsaDecide(this.stochastic,this.agentWait);
		}
	}

	



}
