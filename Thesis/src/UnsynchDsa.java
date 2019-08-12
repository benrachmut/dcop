import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnsynchDsa extends Unsynch {
	private double stochastic;

	public UnsynchDsa(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun, double stochastic) {
		super(dcop, agents, aZ, meanRun);
		Main.rDsa.setSeed(0);
		this.stochastic = stochastic;
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
		
		
		for (AgentField a : whoCanDecide) {	
			if (i != 0) {
				a.dsaDecide(stochastic);	
			}else {
				int value = a.createRandFirstValue();
				a.setValue(value);
			}
			
		}

	}

	@Override
	protected void afterDecideTakeAction(int i) {
		agentZero.afterDecideTakeActionUnsynch(this.whoCanDecide, i);
		this.whoCanDecide = new ArrayList<AgentField>();
	}

	@Override
	public void agentsSendMsgs() {
		List<MessageNormal> messageSent = agentZero.sendUnsynchMonoMsgs(false);
		changeFlag(messageSent);
		

	}

	private void changeFlag(List<MessageNormal> messageSent) {
		Set<AgentField> changeFlag = new <AgentField>HashSet();
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
