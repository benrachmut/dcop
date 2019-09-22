import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class Unsynch extends Solution {
	protected SortedSet<AgentField> whoCanDecide;
	public static int iter;
	public static int topCost;

	protected SortedSet<Permutation> permutations;
	protected SortedSet<AgentField> fathers;
	public static int currentPriceOfTop;



	public Unsynch(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.whoCanDecide = new TreeSet<AgentField>();
		this.permutations = new TreeSet<Permutation>();
		this.fathers = new TreeSet<AgentField>();
		topCost = Integer.MAX_VALUE;
 
	}

	@Override
	public void solve() {

		findHeadOfTree();
		for (int i = 0; i < this.iteration; i++) {
			iter = i;
			
			if (i % 500 == 0 ) {
				System.out.println("---start iteration: " + i + "---");
			}
			
			
		
			updateWhoCanDecide(i); // abstract
		
			agentDecide(i); // abstract
			afterDecideTakeAction(i); // abstract
			List<MessageNormal> msgToSend = agentZero.handleDelay();
			
			agentsSendMsgs(msgToSend); // abstract
			createAnytimeUp(i); // abstract
			createAnytimeDown(i);
			addCostToTables(i );
			addTopCountersChanges(i);
			
		}
	}

	protected abstract void addTopCountersChanges(int i) ;

	private void printWhoCanDecide() {
		System.out.println("who can decide "+iter);
		for (AgentField a : whoCanDecide) {
			System.out.print(a+",");
		}
		
	}

	private boolean noAgentsMinus1() {
		for (AgentField a : agents) {
			if (a.getValue()==-1) {
				return false;
			}
		}
		return true;
	}

	private void findHeadOfTree() {
		SortedSet<AgentField> ans = new TreeSet<AgentField>();
		for (AgentField a : agents) {
			if (a.isAnytimeTop()) {
				ans.add(a);
			}
		}
		this.fathers = ans;
	}

	private void printAgents() {
		for (AgentField a : this.agents) {
			System.out.print(a + ", ");
		}
		System.out.println();

	}

	private void printCreatedAnytimeMsgUp(int i) {
		List<MessageAnyTimeUp> atu = new ArrayList<MessageAnyTimeUp>();

		for (MessageNormal m : agentZero.getMsgBox()) {
			if (m instanceof MessageAnyTimeUp) {
				atu.add((MessageAnyTimeUp) m);
			}
		}
		// System.out.println("iteration, from, to, permutation, cost");

		for (MessageAnyTimeUp m : atu) {

			System.out.println("iteration: " + i + ", from: a" + m.getSender().getId() + ", to: a"
					+ m.getReciever().getId() + ", " + m.getCurrentPermutation());
		}

	}

	private void printPersonalPermutations(int i) {
		for (AgentField a : agents) {
			System.out.println("a" + a.getId() + " at iteration " + i + ":");
			Permutation p = a.createCurrentPermutationNonMonotonic();
			for (Entry<Integer, Integer> e : p.getM().entrySet()) {
				System.out.println("   a" + e.getKey() + ": " + e.getValue());
			}

		}

	}

	// for debug
	private void printDecisionCounter(int i) {
		System.out.println("iteration " + i + ":");
		/*
		 * for (AgentField a : agents) {
		 * System.out.print("a"+a.getId()+":"+a.getDecisonCounter()+","); }
		 * System.out.println();
		 */
		for (AgentField a : agents) {
			System.out.print(+a.getDecisonCounter() + ",");
		}
		System.out.println();

	}

	private void addCostToTables(int i) {
		addCostToList(i);
		addAnytimeCostToList();
		addTopCost();
		addToPermutationsList();
	}

	private void addTopCost() {
		int ans = 0;
		for (AgentField a: fathers) {
			if (a.getBestPermutation() == null) {
				ans = Integer.MAX_VALUE;
				break;
			}else {
			
			int cost = a.getBestPermutation().getCost();
			ans = ans+ cost;
			}
		}
		this.topAnytimeCost.add(ans);
		
	}

	private void addToPermutationsList() {

		int cost = dcop.calCost(true);
		

		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		for (AgentField a : agents) {
			int aId = a.getId();
			int aValue = a.getValue();
			m.put(aId, aValue);
		}
		Permutation p = new Permutation(m, cost);
		this.permutations.add(p);
		

	}

	// public abstract List<AgentField> findHeadOfTree() ;

	protected abstract void updateWhoCanDecide(int i);

	// protected abstract void agentDecide();
	protected abstract void afterDecideTakeAction(int i);

	public abstract void agentsSendMsgs(List<MessageNormal> msgToSend);

	public abstract void createAnytimeUp(int i);

	public void createAnytimeDown(int date) {
		agentZero.createAnyTimeDownUnsynchMono(date);
	}
	// protected abstract void createAnytimeUp();
	// protected abstract void createAnytimeDown(List<AgentField> fathers, int
	// date);

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
	public void addCostToList(int i ) {
		
		

		
		if (atlistOneAgentMinusOne(true)) {
			this.realCost.add(Integer.MAX_VALUE);
		} else {
			
		
			super.addCostToList(i);
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

	public abstract double getCounterRatio(int i);

	protected abstract int getCounterTop(int i);

	protected abstract int getTopCostNotBest(int i);

}
