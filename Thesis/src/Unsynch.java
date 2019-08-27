import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Unsynch extends Solution {
	protected List<AgentField> whoCanDecide;
	public static int iter;
	protected List<Permutation> permutations;
	protected List<AgentField> fathers;

	public Unsynch(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.whoCanDecide = new ArrayList<AgentField>();
		this.permutations = new ArrayList<Permutation>();
		this.fathers = new ArrayList<AgentField>();
	}

	@Override
	public void solve() {

		findHeadOfTree();
		for (int i = 0; i < this.iteration; i++) {
			iter = i;
			if (i % 50 == 0) {
				System.out.println("---start iteration: " + i + "---");
			}
			updateWhoCanDecide(i); // abstract
			agentDecide(i); // abstract
			afterDecideTakeAction(i); // abstract
			List<MessageNormal> msgToSend = agentZero.handleDelay();
			agentsSendMsgs(msgToSend); // abstract
			createAnytimeUp(i); // abstract
			createAnytimeDown(i);
			addCostToTables();

			// ---- for debug
			// printAgents();
			// printDecisionCounter(i);

			// printPersonalPermutations(i);

			// printCreatedAnytimeMsgUp(i);

			// System.out.println("---finish iteration: "+i+"---");
			/*
			 * if (i==8) { System.out.println(); }
			 */
		}
	}

	private void findHeadOfTree() {
		List<AgentField> ans = new ArrayList<AgentField>();
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

	private void addCostToTables() {
		addCostToList();
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
		/*
		 * boolean x0, x1, x2, x3, x4, x5, x6, x7, x8, x9;
		 * 
		 * if (Main.printSelfN) {
		 * 
		 * x0 = agents[0].getValue() == 0; x1 = agents[1].getValue() == 7; x2 =
		 * agents[2].getValue() == 8; x3 = agents[3].getValue() == 9; x4 =
		 * agents[4].getValue() == 9; x5 = agents[5].getValue() == 8; x6 =
		 * agents[6].getValue() == 4; x7 = agents[7].getValue() == 7; x8 =
		 * agents[8].getValue() == 5; x9 = agents[9].getValue() == 4; if (x0 && x1 && x2
		 * && x3 && x4 && x5 && x6 && x7 && x8 && x9) { Main.foundPermutationDebug =
		 * true; } }
		 */
		int cost = dcop.calCost(true);
		/*
		 * if (Main.printSelfN) { Main.foundPermutationDebug = false; }
		 */

		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		for (AgentField a : agents) {
			int aId = a.getId();
			int aValue = a.getValue();
			m.put(aId, aValue);
		}
		Permutation p = new Permutation(m, cost);
		this.permutations.add(p);
		if (Main.printCentralPOVPermutations) {
			System.out.println(p);
		}

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
