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
		for (int i = 0; i < this.itiration; i++) {
			agentsChangeValue(i);
			agentsSendMsgs();
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
	protected abstract void agentsSendMsgs();
	protected abstract void agentsChangeValue(int i);
	protected abstract void createAnytime(List<AgentField> fathers, int i);

	
	
}
