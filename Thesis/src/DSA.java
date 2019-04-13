import java.util.ArrayList;
import java.util.List;

public class DSA extends Dcop {

	private double stochastic;

	public DSA(Dcsp dcsp, AgentField[] agents, AgentZero aZ, double stochastic) {
		super(dcsp, agents, aZ);
		this.stochastic = stochastic;
	}

	@Override
	public void solve() {

		for (int i = 0; i < this.itiration; i++) {
			this.agentZero.createMsgs();
			this.agentZero.sendMsgs();
			agentsDecide();
			addCostToList();

		}
	}

	public void agentsDecide() {
		for (AgentField a : agents) {
			a.dsaDecide(this.stochastic);
		}
	}



}
