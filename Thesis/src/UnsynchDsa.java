import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnsynchDsa extends Unsynch {
	private double stochastic;
	private Set<AgentField> didDecide;
	public UnsynchDsa(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun, double stochastic) {
		super(dcop, agents, aZ, meanRun);
		
		this.stochastic = stochastic;
		this.didDecide = new HashSet<AgentField>(); 
	}

	@Override
	protected void updateWhoCanDecide(int i) {

		for (AgentField a : this.agents) {
			if (i == 0) {
				this.whoCanDecide.add(a);
			} else if (a.getUnsynchFlag()) {
				this.whoCanDecide.add(a);
			}

		}

	}

	@Override
	public void agentDecide(int i) {
		dsaDecide(i);
		setFlagToFalse();
	}

	private void setFlagToFalse() {
		for (AgentField a : whoCanDecide) {
			a.setUnsynchFlag(false);
		}

	}

	private void dsaDecide(int i) {
		
		this.didDecide = new HashSet<AgentField>();
		for (AgentField a : whoCanDecide) {	
			if (i != 0) {
				boolean didChange = a.dsaDecide(stochastic);
				if (didChange) {
					this.didDecide.add(a);
				}
			}else {
				int value = a.createRandFirstValue();
				a.setValue(value);
				didDecide.add(a);
			}
			
		}

	}

	@Override
	protected void afterDecideTakeAction(int i) {
		agentZero.afterDecideTakeActionUnsynch(this.didDecide, i);
		this.whoCanDecide = new ArrayList<AgentField>();
		this.didDecide = new HashSet<AgentField>();
	}

	@Override
	public void agentsSendMsgs(List<MessageNormal> msgToSend) {
		agentZero.sendUnsynchNonMonotonicMsgs(msgToSend);
		changeFlag(msgToSend);
	}

	private void changeFlag(List<MessageNormal> messageSent) {
		Set<AgentField> changeFlag = new <AgentField> HashSet();
		for (MessageNormal m : messageSent) {
			changeFlag.add(m.getReciever());
		}

		for (AgentField a : changeFlag) {
			a.setUnsynchFlag(true);
		}
		
	}
	
	
	

	/*
	 * updateWhoCanDecide(i); agentDecide(); afterDecideTakeAction(i);
	 * agentsSendMsgs(); createAnytime(fathers, i); addCostToTables();
	 */
}
