import java.util.ArrayList;
import java.util.List;

public class UnsynchMono extends Solution {

	private List<AgentField> whoSendMessages;

	public UnsynchMono(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.algo = "UnsynchMono";
		this.whoSendMessages = new ArrayList<AgentField>();

	}

	@Override
	public void solve() {
		for (int i = 0; i < this.itiration; i++) {

			if (i == 0) {
				headSelectRandomAndAddToSend();
			} else {
				agentDecide();
			}
			
			agentZero.createUnsynchMessage(this.whoSendMessages);
			agentZero.sendMsgs();
			
			
			addCostToList();
		}

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

	@Override
	public void agentDecide() {
		List<AgentField>agentDecideThisIt = new ArrayList<>();
		for (AgentField a : agents) {
			
			boolean isUnsynchMonoDecide = a.canUnsynchMonoDecide();
			
			if (isUnsynchMonoDecide) {
				a.dsaDecide(1);
				agentDecideThisIt.add(a);
			}
			
		}
		this.whoSendMessages = agentDecideThisIt;
	}
}
