import java.util.ArrayList;
import java.util.List;

public abstract class Unsynch  extends Solution {
	protected List<AgentField> whoCanDecide;

	public Unsynch(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.whoCanDecide = new ArrayList<AgentField>();
	}

	@Override
	public void solve() {		
		List<AgentField> fathers = findHeadOfTree();
		for (int i = 0; i < this.iteration; i++) {

			
			updateWhoCanDecide(i);
			agentDecide(i);
			afterDecideTakeAction(i);
			List<MessageNormal> msgToSend = agentZero.handleDelay();

			agentsSendMsgs(msgToSend);
			createAnytime(fathers, i);
			addCostToTables();

		}
	}
	
	private void addCostToTables() {
		addCostToList();
		addAnytimeCostToList();
	}

	public List<AgentField> findHeadOfTree() {
		List<AgentField> ans = new ArrayList<AgentField>();
		for (AgentField a : agents) {
			if (a.getDfsFather() == null) {
				ans.add(a);
			}
		}
		return ans;
	}
	protected abstract void updateWhoCanDecide(int i);
	// protected abstract void agentDecide();
	protected abstract void afterDecideTakeAction(int i);

	public abstract void agentsSendMsgs(List<MessageNormal> msgToSend);

	protected void createAnytime(List<AgentField> fathers, int i) {
		agentZero.createAnyTimeUpUnsynchMono();
		agentZero.createAnyTimeDownUnsynchMono(fathers, i);	
	}

	protected boolean atlistOneAgentMinusOne(boolean real) {

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
	public void addCostToList() {
		if (atlistOneAgentMinusOne(true)) {
			this.realCost.add(Integer.MAX_VALUE);
		} else {
			super.addCostToList();
		}
	}
	
	
	@Override
	public void addAnytimeCostToList() {
		if (atlistOneAgentMinusOne(false)) {
			this.anytimeCost.add(Integer.MAX_VALUE);
		} else {
			super.addAnytimeCostToList();
		}

	}
	

	
	
}
