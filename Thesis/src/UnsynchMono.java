import java.util.ArrayList;
import java.util.List;

public class UnsynchMono extends Unsynch {


	public UnsynchMono(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.algo = "unsynchMono";

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


	public void afterDecideTakeAction(int i) {
	
		agentZero.afterDecideTakeActionUnsynchMonotonic(this.whoCanDecide, i);
		

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



	public void agentsSendMsgs(List<MessageNormal> msgToSend) {
		agentZero.sendUnsynchMonotonicMsgs(msgToSend);
		//-------------------

	}

	public void createAnytimeUp() {
		agentZero.createAnyTimeUpUnsynchMono();
	}

	public void createAnytimeDown(List<AgentField> fathers, int date) {
		agentZero.createAnyTimeDownUnsynchMono(fathers, date);	

	}

	
	

}
