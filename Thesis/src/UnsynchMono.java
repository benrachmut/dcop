import java.util.ArrayList;
import java.util.List;

public class UnsynchMono extends Unsynch {


	public UnsynchMono(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.algo = "unsynchMono";

	}



	public void agentsChangeValue(int i) {
		updateWhoCanDecide(i);
		agentDecide();
		agentZero.afterDecideTakeAction(this.whoCanDecide, i);

	}

	public void addAnytimeCostToList() {
		if (atlistOneAgentMinusOne(false)) {
			this.anytimeCost.add(Integer.MAX_VALUE);
		} else {
			super.addAnytimeCostToList();
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

	

	@Override
	public void addCostToList() {
		if (atlistOneAgentMinusOne(true)) {
			this.realCost.add(Integer.MAX_VALUE);
		} else {
			super.addCostToList();
		}
	}

	private boolean atlistOneAgentMinusOne(boolean real) {

		for (AgentField a : agents) {

			if (real) {
				if (a.getValue() == -1) {
					return true;
				}
			} else {
				if (a.getAnytimeValue() == -1) {
					return true;
				}
			}

		}
		return false;
	}

	@Override
	public void agentDecide() {
		for (AgentField a : this.whoCanDecide) {
			if (a.getValue() == -1) {
				a.unsynchDecide();

			} else {
				a.dsaDecide(1);
			}
		}
	}

	public void agentsSendMsgs() {
		agentZero.sendUnsynchMonoMsgs();
	}

	protected void createAnytime(List<AgentField> fathers, int i) {
		agentZero.createAnyTimeUpUnsynchMono();
		agentZero.createAnyTimeDownUnsynchMono(fathers, i);
		
	}
}
