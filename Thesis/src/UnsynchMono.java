import java.util.ArrayList;
import java.util.List;

public class UnsynchMono extends Solution {

	private List<AgentField> whoCanDecide;

	public UnsynchMono(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.algo = "UnsynchMono";
		this.whoCanDecide = new ArrayList<AgentField>();

	}

	@Override
	public void solve() {
		for (int i = 0; i < this.itiration; i++) {
			updateWhoCanDecide(i);
			agentDecide();
			if (i==0) {
				for (AgentField a: this.whoCanDecide) {
					a.setValue(Main.getRandomInt(Main.rFirstValue, 0, a.getDomainSize() - 1));
					
				}
			}
			agentZero.createUnsynchMessage(this.whoCanDecide, i);
			agentZero.sendUnsynchMsgs();
			addCostToList();
		}
	}

	private void updateWhoCanDecide(int i) {
		List<AgentField> temp = new ArrayList<AgentField>();
		if (i == 0) {
			temp = findHeadOfTree();
		} else {
			temp = iterateAgentsWhoCan();
		}
		this.whoCanDecide = temp;

	}

	private List<AgentField> iterateAgentsWhoCan() {
		List<AgentField> ans = new ArrayList<AgentField>();
		for (AgentField a : agents) {
			if (a.unsynchAbilityToDecide()) {
				ans.add(a);
			}
		}
		return ans;
	}

	private List<AgentField> findHeadOfTree() {
		List<AgentField> ans = new ArrayList<AgentField>();
		for (AgentField a : agents) {
			if (a.getFather() == null) {
				ans.add(a);
			}
		}
		return ans;
	}

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
		for (AgentField a : this.whoCanDecide) {
			a.unsynchDecide();
		}
	}
}
