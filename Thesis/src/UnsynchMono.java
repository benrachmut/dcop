import java.util.ArrayList;
import java.util.List;

public class UnsynchMono extends Solution {

	private List<AgentField> whoCanDecide;

	public UnsynchMono(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.algo = "Unsynch" ;
		this.whoCanDecide = new ArrayList<AgentField>();

	}

	@Override
	public void solve() {
		List<AgentField> fathers = findDfsHeadOfTree();
		for (int i = 0; i < this.itiration; i++) {
			agentsChangeValue(i);
			agentZero.sendUnsynchMsgs();
			if (Main.anytimeDfs) {
				agentZero.createAnyTimeUp();
				agentZero.createAnyTimeDown(fathers, i);
			}
			addCostToList();
			// addFatherCost(fathers);
			addAnytimeCostToList();
		}
	}

	private void agentsChangeValue(int i) {
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
	/*
	 * private void addFatherCost(List<AgentField> fathers) { int ans = 0; for
	 * (AgentField f : fathers) { Permutation p = f.getBestPermutation(); if (p ==
	 * null) { this.fatherCost.add(Integer.MAX_VALUE); return; } else { if (ans +
	 * p.getCost() >Integer.MAX_VALUE-200) { this.fatherCost.add(Integer.MAX_VALUE);
	 * return; } ans = (ans + p.getCost())/2; } } if (ans == 0) {
	 * this.fatherCost.add(Integer.MAX_VALUE); return; }
	 * 
	 * this.fatherCost.add(ans); }
	 */

	private void updateWhoCanDecide(int i) {
		List<AgentField> temp = new ArrayList<AgentField>();
		if (i == 0) {
			temp = findDfsHeadOfTree();
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

	private List<AgentField> findDfsHeadOfTree() {
		List<AgentField> ans = new ArrayList<AgentField>();
		for (AgentField a : agents) {
			if (a.getDfsFather() == null) {
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
}
