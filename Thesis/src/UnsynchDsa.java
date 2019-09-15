import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class UnsynchDsa extends Unsynch {
	private double stochastic;
	private SortedSet<AgentField> didDecide;

	public UnsynchDsa(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun, double stochastic) {
		super(dcop, agents, aZ, meanRun);

		this.stochastic = stochastic;
		this.didDecide = new TreeSet<AgentField>();
		Main.rDsa.setSeed(meanRun);
		this.algo = "DSA"+stochastic+"asynch";
	}

	// ---- 1
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

	// ---- 2

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

		this.didDecide = new TreeSet<AgentField>();
		for (AgentField a : whoCanDecide) {
			if (i != 0) {

				boolean didChange = a.dsaDecide(stochastic);
				if (didChange) {
					this.didDecide.add(a);
				}
			} else {
				int value = a.createRandFirstValue();
				a.setValue(value);
				didDecide.add(a);
			}

		}
		
		if ( i == 138) {
			System.err.println("did dedice");
			for (AgentField a : didDecide) {
				System.err.println(a+",");
			}
		System.out.println();
		}
		

	}

	// ---- 3

	@Override
	protected void afterDecideTakeAction(int i) {
		if (Main.trySendValueAsPermutation) {
			agentZero.afterDecideTakeActionUnsynchNonMonotonicByValue(this.didDecide, i);
		} else {
			agentZero.afterDecideTakeActionUnsynchNonMonotonicByCounter(this.didDecide, i);
		}

		this.whoCanDecide = new TreeSet<AgentField>();
		this.didDecide = new TreeSet<AgentField>();
	}

	// ---- 4

	@Override
	public void agentsSendMsgs(List<MessageNormal> msgToSend) {
		if (Main.trySendValueAsPermutation) {
			agentZero.sendUnsynchNonMonotonicByValueMsgs(msgToSend);
		} else {
			agentZero.sendUnsynchNonMonotonicMsgs(msgToSend);
		}
		changeFlagForAgentsRecieveMsg(msgToSend);
	}

	private void changeFlagForAgentsRecieveMsg(List<MessageNormal> messageSent) {
		Set<AgentField> changeFlag = new HashSet<AgentField>();
		for (MessageNormal m : messageSent) {
			changeFlag.add(m.getReciever());
		}

		for (AgentField a : changeFlag) {
			a.setUnsynchFlag(true);
		}

	}

	@Override
	public void createAnytimeUp(int i) {
		agentZero.createAnyTimeUpUnsynchNonMonotonic(i);
	}
/*
	@Override
	public void createAnytimeDown(int date) {
		// TODO Auto-generated method stub

	}
	/*
	@Override
	public List<AgentField> findHeadOfTree() {
		List<AgentField> ans = new ArrayList<AgentField>();
		for (AgentField a : agents) {
			if (a.getAnytimeFather() == null) {
				ans.add(a);
			}
		}
		return ans;
	}
	*/
	
}
