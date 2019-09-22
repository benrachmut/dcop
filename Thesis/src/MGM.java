
public class MGM extends Solution {

	public MGM(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.algo = "mgm";
		// solve();
	}

	@Override
	public void solve() {
		boolean first = true;
		for (int i = 0; i < this.iteration; i++) {
			if (!first) {
				agentsSetR();
				sendAndRecieveRi(i);
				agentDecide(i);
				first = true;
			} else {

				sendAndRecieve(i);
				first = false;

			}
			addCostToList(i);
		}

	}

	protected void sendAndRecieveRi(int i) {
		this.agentZero.createRiMsgs(i);
		this.agentZero.sendRiMsgs();

	}

	protected void agentsSetR() {
		for (AgentField a : agents) {
			a.setR();
		}

	}

	@Override
	public void agentDecide(int i) {
		for (AgentField a : agents) {
			a.mgmDecide();
		}

	}

}
