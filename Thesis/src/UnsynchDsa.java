import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class UnsynchDsa extends Unsynch {
	private double stochastic;
	private SortedSet<AgentField> didDecide;

	protected List<Double> ratioCounterTopCounterChanges;
	protected List<Integer> counterTopChanges;
	public  List<Integer> costOfAllTops;
	public static int counterPermutationAtTop;

	public UnsynchDsa(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun, double stochastic) {
		super(dcop, agents, aZ, meanRun);

		this.stochastic = stochastic;
		this.didDecide = new TreeSet<AgentField>();
		Main.rDsa.setSeed(meanRun);
		this.algo = "DSA" + stochastic + "asynch";
		counterPermutationAtTop = 0;

		ratioCounterTopCounterChanges = new ArrayList<Double>();
		counterTopChanges = new ArrayList<Integer>();
		costOfAllTops = new ArrayList<Integer>();
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
		//dsaDecide(i);
		this.didDecide = new TreeSet<AgentField>();
		for (AgentField a : whoCanDecide) {
			if (i != 0) {			
				boolean didChange = differentDecision(a);
				if (didChange) {
					this.didDecide.add(a);
				}
			} else {
				int value = a.createRandFirstValue();
				a.setValue(value);
				didDecide.add(a);
			}
		}
		changeAgentDecisionCounter(i);
		setFlagToFalse();
	}

	private boolean differentDecision(AgentField a) {
		return a.dsaDecide(stochastic);
	}

	private void changeAgentDecisionCounter(int i) {
		for (AgentField af : didDecide) {
			af.increaseDecisionCounterByOne(i);
		}
	}

	private void setFlagToFalse() {
		for (AgentField a : whoCanDecide) {
			a.setUnsynchFlag(false);
		}
	}

	// ---- 3

	@Override
	protected void afterDecideTakeAction(int i) {
		this.agentZero.afterDecideTakeActionUnsynchNonMonotonic(this.didDecide, i);
		this.whoCanDecide = new TreeSet<AgentField>();
		this.didDecide = new TreeSet<AgentField>();
	}

	// ---- 4

	@Override
	public void agentsSendMsgs(List<Message> msgToSend, int i ) {
		agentZero.sendUnsynchNonMonotonicMsgs(msgToSend, i);
		changeFlagForAgentsRecieveMsg(msgToSend);
	}

	private void changeFlagForAgentsRecieveMsg(List<Message> messageSent) {
		SortedSet<AgentField> changeFlag = new TreeSet<AgentField>();
		for (Message m : messageSent) {
			if (!(m instanceof MessageAnyTimeDown) && !(m instanceof MessageAnyTimeUp)) {
				changeFlag.add(m.getReciever());
			}
		}
		for (AgentField a : changeFlag) {
			a.setUnsynchFlag(true);
		}
	}

	@Override
	public void createAnytimeUp(int i) {
		agentZero.createAnyTimeUpUnsynchNonMonotonic(i);
	}

	@Override
	protected void addTopCountersChanges(int i) {
		this.costOfAllTops.add(topCost); 
		if (i>1) {
			int before =this.costOfAllTops.get(i-1);
			int current = this.costOfAllTops.get(i);
			if ( before!= current) {
				counterPermutationAtTop = counterPermutationAtTop+1;
			}
		}
		this.counterTopChanges.add(counterPermutationAtTop);
		int currentCentralistic = Solution.counterCentralisticChanges;
		if (currentCentralistic == 0) {
			ratioCounterTopCounterChanges.add(0.0);
		} else {
			double ratio = (double)counterPermutationAtTop / currentCentralistic;
			ratioCounterTopCounterChanges.add(ratio);
		}

	}

	@Override
	public double getCounterRatio(int i) {
		return this.ratioCounterTopCounterChanges.get(i);
	}

	@Override
	protected int getCounterTop(int i) {
		return counterTopChanges.get(i);
	}

	@Override
	protected int getTopCostNotBest(int i) {
		return costOfAllTops.get(i);
	}

}
