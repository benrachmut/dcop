import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class UnsynchMono extends Unsynch {


	public UnsynchMono(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.algo = "monotonic";

	}
	public void updateWhoCanDecide(int i) {
		SortedSet<AgentField> temp = new TreeSet<AgentField>();
		if (i == 0) {
			temp = findHeadOfTree();
		} else {
			temp = iterateAgentsWhoCan();
		}
		this.whoCanDecide = temp;
	}
	@Override
	public void agentDecide(int i) {
		for (AgentField a : this.whoCanDecide) {
			if (a.getValue() == -1) {
				a.unsynchDecide();

			} else {
				a.dsaDecide(1);
			}
		}

	}


	public void afterDecideTakeAction(int i) {
	
		agentZero.afterDecideTakeActionUnsynchMonotonic(this.whoCanDecide, i);
		

	}

	
	
	

	
	
	

	

	private SortedSet<AgentField> iterateAgentsWhoCan() {
		SortedSet<AgentField> ans = new TreeSet<AgentField>();
		for (AgentField a : agents) {
			if (a.unsynchAbilityToDecide()) {
				ans.add(a);
			}
		}
		return ans;
	}



	public void agentsSendMsgs(List<MessageNormal> msgToSend) {
		agentZero.sendUnsynchMonotonicMsgs(msgToSend);

	}

	public void createAnytimeUp(int i) {
		agentZero.createAnyTimeUpUnsynchMono(i);
	}

	

	public SortedSet<AgentField> findHeadOfTree() {
		SortedSet<AgentField> ans = new TreeSet<AgentField>();
		for (AgentField a : agents) {
			if (a.getDfsFather() == null) {
				ans.add(a);
			}
		}
		return ans;
	}
	

	
	

}
