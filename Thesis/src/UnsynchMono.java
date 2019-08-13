import java.util.ArrayList;
import java.util.List;

public class UnsynchMono extends Unsynch {


	public UnsynchMono(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.algo = "unsynchMono";

	}



	public void afterDecideTakeAction(int i) {
	
		agentZero.afterDecideTakeActionUnsynch(this.whoCanDecide, i);
	}

	
	public void updateWhoCanDecide(int i) {
		List<AgentField> temp = new ArrayList<AgentField>();
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
	public void agentsSendMsgs(List<MessageNormal> input) {
		agentZero.sendUnsynchMonoMsgs(input);
		//changeFlag(input);
		
	}

	


/*
	public void agentsSendMsgs() {
		agentZero.sendUnsynchMonoMsgs(true);
	}
	*/
	
	
	

}
