
public class UnsynchMono extends Solution {

	public UnsynchMono(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.algo = "UnsynchMono";

	}

	@Override
	public void solve() {
/*
		for (int i = 0; i < this.itiration; i++) {
			
			
			
			sendAndRecieve(i);
			sendAndRecieveTimeStemp(i);
			agentDecide();
			addCostToList();
		}
		*/

	} // iteration

	@Override
	public void addCostToList() {
		if (atlistOneAgentMinusOne()) {
			this.realCost.add(Integer.MAX_VALUE);
		} else {
			super.addCostToList();
		}
	}

	private boolean atlistOneAgentMinusOne() {

		for (AgentField a : agents) {
			if (a.getValue() == -1) {
				return true;
			}
		}
		return false;
	}
/*
	private void sendAndRecieveTimeStemp(int i) {
		this.agentZero.createTimeStempMsgs(i);
		this.agentZero.sendTimeStempMsgs();

	}
*/
	@Override
	public void agentDecide() {		
		/*
		for (AgentField a : agents) {
			boolean isUnsynchMonoDecide = a.canUnsynchMonoDecide();
			if (isUnsynchMonoDecide) {
				a.dsaDecide(1);
			}
		}
		*/
	}

}
