import java.util.ArrayList;
import java.util.List;

public class Unsynch extends Solution {

	private List<AgentField> whoCanDecide;
	private double stochastic;

	public Unsynch(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun, double stoch) {
		super(dcop, agents, aZ, meanRun);
		this.algo = "Unsynch" + stoch;
		this.whoCanDecide = new ArrayList<AgentField>();
		this.stochastic = stoch;

	}

	@Override
	public void solve() {
		List<AgentField> fathers = findHeadOfTree();
		for (int i = 0; i < this.itiration; i++) {
			updateWhoCanDecide(i);
			agentDecide();
			agentZero.iterateOverWhoCanDecide(this.whoCanDecide, i);
			agentZero.sendUnsynchMsgs();
			if (Main.anyTime) {
				agentZero.createAnyTimeUp();
				agentZero.createAnyTimeDown(fathers, i);
			}
			addCostToList();
			// addFatherCost(fathers);
			addAnytimeCostToList();
		}
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
				a.dsaDecide(stochastic);
			}
		}
	}
}
