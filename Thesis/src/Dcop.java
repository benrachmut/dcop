import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.xml.bind.PrintConversionEvent;

public class Dcop {

	private AgentField[] agentsF;
	// private Set<Constraint> constraints;
	private Map<Neighbors, Set<Constraint>> constraints;
	private Set<Neighbors> neighbors;
	private int iterations;

	// 1= Uniformly random DCOPs, 2= Graph coloring problems, 3= Scale-free
	public Dcop(AgentField[] agents, int d, int iterations) {
		this.agentsF = agents;
		this.iterations = iterations;
		this.neighbors = new TreeSet<Neighbors>();
		this.constraints = new TreeMap<Neighbors, Set<Constraint>>();
		initGivenDcopVersion();
		//printConst();

	}

	public void printConst() {
		for (Entry<Neighbors, Set<Constraint>> e : constraints.entrySet()) {
			System.out.println(e.getKey()+":");
			for (Constraint c : e.getValue()) {
				System.out.print(c.getNeighbors()+":"+c.getCost()+",");
			}
			System.out.println();
		}
		
	}

	@Override
	public String toString() {
		// 1= Uniformly random DCOPs, 2= Graph coloring problems, 3= Scale-free
		// network problems.
		if (Main.dcopVersion == 1) {
			return "Uniformly random DCOPs";
		}
		if (Main.dcopVersion == 2) {
			return "Graph coloring problems";
		}
		if (Main.dcopVersion == 3) {
			return "Scale-free network problems";
		}
		return super.toString();
	}

	private void initGivenDcopVersion() {
		if (Main.dcopVersion == 1) {
			createNeighborsGivenP1(Main.currentP1Uniform, Main.rP1Uniform);
			initConstMap();
			createConstraintsGivenP2(Main.currentP2Uniform, Main.rP2Uniform);
		}
		if (Main.dcopVersion == 2) {
			createNeighborsGivenP1(Main.currentP1Color, Main.rP1Color);
			initConstMap();
			createConstraintsGraphColor();
		}

		if (Main.dcopVersion == 3) {
			createNeighborsGivenFreeScale();
			initConstMap();
			createConstraintsGivenP2(Main.currentP2ScaleFree, Main.rP2ScaleFree);
		}
	}

	private void createNeighborsGivenFreeScale() {
		Map<Integer, Boolean> marked = initColored();
		createHubs(marked);
		findNeighborsToOtherAgentsWhoAreNotHubs(marked);
	}

	private void initConstMap() {
		for (Neighbors n : this.neighbors) {
			this.constraints.put(n, new TreeSet<Constraint>());
		}

	}

	private void createNeighborsGivenP1(Double currentP1, Random rP1) {
		// Set<Neighbors> ans = new HashSet<Neighbors>();
		for (int i = 0; i < agentsF.length; i++) {
			for (int j = i + 1; j < agentsF.length; j++) {
				double p1Max = rP1.nextDouble();
				if (p1Max < currentP1) {
					AgentField af1 = agentsF[i];
					AgentField af2 = agentsF[j];
					this.neighbors.add(new Neighbors(af1, af2));
				} // if neighbors
			} // for j
		} // for i
	}

	private void findNeighborsToOtherAgentsWhoAreNotHubs(Map<Integer, Boolean> marked) {
		for (Entry<Integer, Boolean> e : marked.entrySet()) {
			Integer id = e.getKey();
			Boolean isMarked = e.getValue();
			if (!isMarked) {
				AgentField af = getAgentField(id);
				findNeighborsToSingleAgentNotHub(af);
				marked.put(af.getId(), true);
			}
		}
		checkIfMarkedMakeSense(marked);
	}

	private void checkIfMarkedMakeSense(Map<Integer, Boolean> marked) {
		for (Boolean b : marked.values()) {
			if (!b) {
				System.err.println("something is wrong with iterating over all the unmarked ");
			}
		}

	}

	private void findNeighborsToSingleAgentNotHub(AgentField af) {
		List<Integer> idsOfANeighbor = selectNToNotHubs(af);
		if (idsOfANeighbor.size() != Main.currentNumOfNToNotHub) {
			System.err.println("something in selectNToNotHubs in dcop is wrong");
		}
		declareNeighborsOfIteratedAgentField(idsOfANeighbor, af);
	}

	private void declareNeighborsOfIteratedAgentField(List<Integer> idsOfANeighbor, AgentField af) {
		for (Integer id : idsOfANeighbor) {
			AgentField afNeighbor = getAgentField(id);
			Agent a1;
			Agent a2;
			if (id < af.getId()) {
				a1 = afNeighbor;
				a2 = af;
			} else {
				a1 = af;
				a2 = afNeighbor;
			}
			Neighbors n = new Neighbors(a1, a2);
			this.neighbors.add(n);

			afNeighbor.addNeighbor(af.getId());
			af.addNeighbor(afNeighbor.getId());
		}

	}

	private List<Integer> selectNToNotHubs(AgentField af) {
		List<Integer> ans = new ArrayList<Integer>();
		Map<Integer, Boolean> markedHere = initColored();
		markedHere.put(af.getId(), true);
		Map<Integer, Double> probs = initProbs(af);

		int counter = 0;
		while (counter < Main.currentNumOfNToNotHub) {
			int idOfNeighborShuffled = getFromProbsShuffledNeighbor(af, probs);
			if (idOfNeighborShuffled == -1) {
				System.err.println("logical bug in creating prob map");
			}
			if (!markedHere.get(idOfNeighborShuffled)) {
				counter++;
				markedHere.put(idOfNeighborShuffled, true);
				ans.add(idOfNeighborShuffled);
			}
		}
		return ans;

	}

	private int getFromProbsShuffledNeighbor(AgentField af, Map<Integer, Double> probs) {
		double rnd = Main.rNotHub.nextDouble();
		for (int i = 0; i < agentsF.length; i++) {
			if (rnd < probs.get(i)) {
				return i;
			}
		}

		return -1;
	}

	private Map<Integer, Double> initProbs(AgentField af) {
		Map<Integer, Double> ans = new TreeMap<Integer, Double>();

		double sigma = 0;

		for (AgentField a : agentsF) {
			sigma += a.getNieghborSize();
		}

		for (int i = 0; i < agentsF.length; i++) {
			AgentField a = agentsF[i];
			int id = a.getId();
			double aProb = a.getNieghborSize() / sigma;
			if (i == 0) {
				ans.put(id, aProb);
			} else {
				double probAbove = aProb + ans.get(i - 1);
				ans.put(id, probAbove);
			}

		}

		return ans;
	}

	private AgentField getAgentField(Integer id) {
		for (AgentField a : agentsF) {
			if (a.getId() == id) {
				return a;
			}
		}
		return null;
	}

	private void createHubs(Map<Integer, Boolean> marked) {
		List<AgentField> hubs = getRandomElement(Main.currentHub, Main.rHub);
		hubNeighborsToOneAnother(hubs);
		for (AgentField a : hubs) {
			marked.put(a.getId(), true);
		}

	}

	private void hubNeighborsToOneAnother(List<AgentField> hubs) {
		List<Neighbors> ans = new ArrayList<Neighbors>();
		for (int i = 0; i < hubs.size(); i++) {
			for (int j = i + 1; j < hubs.size(); j++) {
				informDcopAndAgentsUponNeighborhood(hubs, i, j);

			}
		}

	}

	private void informDcopAndAgentsUponNeighborhood(List<AgentField> hubs, int i, int j) {
		// add to neighbors in dcop
		Agent ai = hubs.get(i);
		Agent aj = hubs.get(j);
		Agent a1;
		Agent a2;
		if (ai.getId() < aj.getId()) {
			a1 = ai;
			a2 = aj;
		} else {
			a1 = aj;
			a2 = ai;
		}

		Neighbors n = new Neighbors(a1, a2);
		this.neighbors.add(n);

		// inform agents about friendship
		AgentField af1 = hubs.get(i);
		AgentField af2 = hubs.get(j);

		af1.addNeighbor(af2.getId());
		af2.addNeighbor(af1.getId());
	}

	private Map<Integer, Boolean> initColored() {
		Map<Integer, Boolean> ans = new HashMap<Integer, Boolean>();
		for (AgentField a : agentsF) {
			ans.put(a.getId(), false);
		}
		return ans;
	}

	private void createConstraintsGraphColor() {

		for (Entry<Neighbors, Set<Constraint>> e : this.constraints.entrySet()) {
			int id1 = e.getKey().getA1().getId();
			AgentField af1 = getAgentField(id1);
			int id2 = e.getKey().getA2().getId();
			AgentField af2 = getAgentField(id2);
			for (int k = 0; k < af1.getDomainSize(); k++) {
				int d1 = af1.getDomain()[k];
				for (int k2 = 0; k2 < af2.getDomainSize(); k2++) {
					int d2 = af2.getDomain()[k2];
					if (d1 == d2) {
						Agent a1 = new Agent(id1, d1);
						Agent a2 = new Agent(id2, d2);
						int cost = Main.costMax;
						informFieldAgentOnConstraint(d1, d2, a1, a2, af1, af2, id1, id2, cost);
						e.getValue().add(new Constraint(new Neighbors(a1, a2), cost));
					} // if domain is equal
				} // for k2
			} // for k
		} // for map
	}

	private void createConstraintsGivenP2(double currentP2, Random r) {

		for (Entry<Neighbors, Set<Constraint>> e : this.constraints.entrySet()) {
			int id1 = e.getKey().getA1().getId();
			AgentField af1 = getAgentField(id1);
			int id2 = e.getKey().getA2().getId();
			AgentField af2 = getAgentField(id2);

			for (int k = 0; k < af1.getDomainSize(); k++) {
				int d1 = af1.getDomain()[k];
				for (int k2 = 0; k2 < af2.getDomainSize(); k2++) {
					int d2 = af2.getDomain()[k2];
					double p2Max = r.nextDouble();
					if (p2Max < currentP2) {
						Agent a1 = new Agent(id1, d1);
						Agent a2 = new Agent(id2, d2);
						int cost = Main.getRandomInt(Main.rCost, 1, Main.costMax);
						informFieldAgentOnConstraint(d1, d2, a1, a2, af1, af2, id1, id2, cost);
						e.getValue().add(new Constraint(new Neighbors(a1, a2), cost));
					} // p2
				} // for domain k2
			} // for domain k
		} // for map constraints
	}

	private void informFieldAgentOnConstraint(int d1, int d2, Agent a1, Agent a2, AgentField af1, AgentField af2, int i,
			int j, int cost) {
		af1.addConstraintNeighbor(d1, new ConstraintNeighbor(a2, cost));
		addToMapsAgents(af1, j);

		af2.addConstraintNeighbor(d2, new ConstraintNeighbor(a1, cost));
		addToMapsAgents(af2, i);
		/*
		 * //boolean flag = false;
		 * 
		 * 
		 * int id1 = af1.getId(); int id2 = af2.getId();
		 * 
		 * for (Neighbors n : neighbors) { if (id1 == n.getA1().getId() && id2 ==
		 * n.getA2().getId()) { //flag = true; break; } }
		 * 
		 * if (!flag) { this.neighbors.add(new Neighbors(af1, af2, this.iterations)); }
		 */
	}

	private void addToMapsAgents(AgentField agentInput, int idOther) {
		agentInput.addNeighbor(idOther);
		agentInput.addNeighborR(idOther);

	}

	public Set<Neighbors> getNeighbors() {
		return this.neighbors;
	}

	public AgentField[] getAgentsF() {
		return agentsF;
	}

	public Map<Neighbors, Set<Constraint>> getConstraints() {
		return constraints;
	}

	public List<Neighbors> getHisNeighbors(AgentField input) {
		List<Neighbors> ans = new ArrayList<Neighbors>();
		for (Neighbors n : this.neighbors) {
			Agent n1 = n.getA1();
			Agent n2 = n.getA2();
			boolean isInputInN = n1.getId() == input.getId() || n2.getId() == input.getId();
			if (isInputInN) {
				ans.add(n);
			}

		}
		return ans;
	}

	public int calCost(boolean real) {
		int ans = 0;
		// neighbors are pointed to original
		for (Neighbors n : neighbors) {
			int t = calCostPerNeighbor(n, real);
			/*
			if (Unsynch.iter==5) {
				System.out.println(n+" cost: "+t);
			}
			*/
			
			ans = ans + t;
		}
		return ans * 2;
	}

	public int calRealSolForDebug(Map<Integer, Integer> m) {

		List<Agent> agents = getFieldAgentsForCalReal(m);
		List<Neighbors> neighbors = getFieldNeighborsForCalReal(agents);
		int ans = 0;
		for (Neighbors n : neighbors) {
			int costPerN = calCostPerNeighborForDebug(n);
			ans += costPerN;
		}

		return ans * 2;

	}

	private List<Neighbors> getFieldNeighborsForCalReal(List<Agent> agents) {
		List<Neighbors> ans = new ArrayList<Neighbors>();
		/*
		 * for (int i = 0; i < agents.size(); i++) { for (int j = i + 1; j <
		 * agents.size(); j++) { for (Neighbors neighbors : neighbors) {
		 * 
		 * } } }
		 */

		for (int i = 0; i < agents.size(); i++) {
			int iId = agents.get(i).getId();
			AgentField af1 = getAgentField(iId);
			for (int j = i + 1; j < agents.size(); j++) {
				int jId = agents.get(j).getId();
				if (af1.isNeighbor(jId)) {
					Neighbors n = new Neighbors(agents.get(i), agents.get(j));
					//Neighbors n = getNeighborFromSet(iId, jId);
					ans.add(n);
				}

			}
		}
		return ans;
	}

	private Neighbors getNeighborFromSet(int iId, int jId) {
		for (Neighbors n : neighbors) {
			int nId1 = n.getA1().getId();
			int nId2 = n.getA2().getId();

			if ((iId == nId1 && jId == nId2) || (iId == nId2 && jId == nId1)) {
				return n;
			}

		}
		return null;
	}

	private List<Agent> getFieldAgentsForCalReal(Map<Integer, Integer> m) {

		List<Agent> ans = new ArrayList<Agent>();

		for (Entry<Integer, Integer> e : m.entrySet()) {
			Agent a = new Agent(e.getKey(), e.getValue());
			ans.add(a);

		}

		return ans;
	}

	/*
	 * private List<Neighbors> getNeighborsForCalReal(List<Agent> agents) {
	 * List<Neighbors> ans = new ArrayList<Neighbors>();
	 * 
	 * for (int i = 0; i < agents.size(); i++) { for (int j = i + 1; j <
	 * agents.size(); j++) { if (agents.get(i).isNeighbor(j)) {
	 * 
	 * }
	 * 
	 * Neighbors n = new Neighbors(agents.get(i), agents.get(j)); ans.add(n); } }
	 * return ans; }
	 */
	private List<Agent> getAgentsForCalReal(Map<Integer, Integer> m) {

		List<Agent> ans = new ArrayList<Agent>();
		for (Entry<Integer, Integer> e : m.entrySet()) {
			Agent a = new Agent(e.getKey(), e.getValue());
			ans.add(a);
		}
		return ans;
	}

	public int calCostPerNeighborForDebug(Neighbors n) {
		return calCostPerNeighbor(n, true);

		/*
		 * Agent an1 = n.getA1(); Agent an2 = n.getA2(); for (Constraint c :
		 * constraints) {
		 * 
		 * Agent ac1 = c.getNeighbors().getA1(); Agent ac2 = c.getNeighbors().getA2();
		 * boolean sameId = an1.getId() == ac1.getId() && an2.getId() == ac2.getId();
		 * boolean sameValue;
		 * 
		 * sameValue = an1.getValue() == ac1.getValue() && an2.getValue() ==
		 * ac2.getValue();
		 * 
		 * if (sameValue && sameId) { return c.getCost(); } }
		 * 
		 * return 0;
		 */

	}

	public int calCostPerNeighbor(Neighbors n, boolean real) {

		int idN1 = n.getA1().getId();
		int idN2 = n.getA2().getId();

		Neighbors nFromSet = getPointNeighbor(idN1, idN2);
		/*
		 * if (nFromSet == null) { System.out.
		 * println("there is a bug in getPointNeighbor because it returns a null"); }
		 */

		// if n from input are actually neighbors
		if (nFromSet != null) {
			Set<Constraint> cs = this.constraints.get(nFromSet);
			int valA1 = 0;
			int valA2 = 0;
			if (real) {
				valA1 = n.getA1().getValue();
				valA2 = n.getA2().getValue();
			} else {
				valA1 = n.getA1().getAnytimeValue();
				valA2 = n.getA2().getAnytimeValue();
			}
			for (Constraint c : cs) {
				Agent aN1 = c.getNeighbors().getA1();
				Agent aN2 = c.getNeighbors().getA2();
				if (idN1 == aN1.getId()) {
					if (valA1 == aN1.getValue() && valA2 == aN2.getValue()) {
						return c.getCost();
					}
				} else {
					if (valA1 == aN2.getValue() && valA2 == aN1.getValue()) {
						return c.getCost();
					}
				}
			} // for constraints
		} // if n from input are actually neighbors
		return 0;

		/*
		 * Agent an1 = n.getA1(); Agent an2 = n.getA2();
		 * 
		 * for (Constraint c : constraints) {
		 * 
		 * Agent ac1 = c.getNeighbors().getA1(); Agent ac2 = c.getNeighbors().getA2();
		 * boolean sameId = an1.getId() == ac1.getId() && an2.getId() == ac2.getId();
		 * boolean sameValue; if (real) { sameValue = an1.getValue() == ac1.getValue()
		 * && an2.getValue() == ac2.getValue(); } else {// any time sameValue =
		 * an1.getAnytimeValue() == ac1.getValue() && an2.getAnytimeValue() ==
		 * ac2.getValue(); }
		 * 
		 * if (sameValue && sameId) { return c.getCost(); } }
		 * 
		 * return 0;
		 */

	}

	private Neighbors getPointNeighbor(int idN1, int idN2) {
		for (Neighbors n : this.neighbors) {
			if (n.getA1().getId() == idN1 && n.getA2().getId() == idN2) {
				return n;
			}
			if (n.getA2().getId() == idN1 && n.getA1().getId() == idN2) {
				return n;
			}
		}
		return null;
	}

	// Function select an element base on index and return
	// an element
	public List<AgentField> getRandomElement(int totalItems, Random rand) {
		// create a temporary list for storing
		// selected element

		List<AgentField> list = turnAgentArrayToArrayList();
		List<AgentField> newList = new ArrayList<AgentField>();
		for (int i = 0; i < totalItems; i++) {

			// take a random index between 0 to size of given List
			int randomIndex = rand.nextInt(list.size());

			// add element in temporary list
			newList.add(list.get(randomIndex));

			// Remove selected element from orginal list
			list.remove(randomIndex);
		}
		return newList;
	}

	private List<AgentField> turnAgentArrayToArrayList() {
		List<AgentField> ans = new ArrayList<AgentField>();
		for (AgentField a : agentsF) {
			ans.add(a);
		}

		return ans;
	}

	private Set<Constraint> createConstraintsGraphColorWithSetConstraint() {
		Set<Constraint> ans = new HashSet<Constraint>();
		for (int i = 0; i < agentsF.length; i++) {
			for (int j = i + 1; j < agentsF.length; j++) {
				double p1Max = Main.rP1Color.nextDouble();
				if (p1Max < Main.currentP1Color) {
					AgentField af1 = agentsF[i];
					AgentField af2 = agentsF[j];
					for (int k = 0; k < af1.getDomainSize(); k++) {
						int d1 = af1.getDomain()[k];
						for (int k2 = 0; k2 < af2.getDomainSize(); k2++) {
							int d2 = af2.getDomain()[k2];
							if (d1 == d2) {
								Agent a1 = new Agent(i, d1);
								Agent a2 = new Agent(j, d2);
								int cost = Main.costMax;
								informFieldAgentOnConstraint(d1, d2, a1, a2, af1, af2, i, j, cost);
								Constraint c = new Constraint(new Neighbors(a1, a2), cost);
								ans.add(c);
							}
						}
					}
				}
			}
		}
		return ans;

	}

	private Set<Constraint> createNeighborsAndConstraintsUniformlyRandomDCOP() {
		Set<Constraint> ans = new HashSet<Constraint>();

		for (int i = 0; i < agentsF.length; i++) {
			for (int j = i + 1; j < agentsF.length; j++) {
				double p1Max = Main.rP1Uniform.nextDouble();
				if (p1Max < Main.currentP1Uniform) {
					AgentField af1 = agentsF[i];
					AgentField af2 = agentsF[j];
					for (int k = 0; k < af1.getDomainSize(); k++) {
						int d1 = af1.getDomain()[k];
						for (int k2 = 0; k2 < af2.getDomainSize(); k2++) {
							int d2 = af2.getDomain()[k2];
							double p2Max = Main.rP2Uniform.nextDouble();
							if (p2Max < Main.currentP2Uniform) {

								Agent a1 = new Agent(i, d1);
								Agent a2 = new Agent(j, d2);
								int cost = Main.getRandomInt(Main.rCost, 1, Main.costMax);
								informFieldAgentOnConstraint(d1, d2, a1, a2, af1, af2, i, j, cost);

								Constraint c = new Constraint(new Neighbors(a1, a2), cost);
								ans.add(c);
							}
						}
					}

				} // p2
			} // for domain k2
		} // for domain k
		return ans;

	}

	private Set<Constraint> createConstraintsScaleFreeAB() {
		Map<Integer, Boolean> marked = initColored();
		createHubs(marked);
		findNeighborsToOtherAgentsWhoAreNotHubs(marked);

		return createConstraintsGivenNeigbors();
	}

	private Set<Constraint> createConstraintsGivenNeigbors() {
		// this.constraints.add(new Constraint(new Neighbors(a1, a2), cost));
		// af2.addConstraintNeighbor(d2, new ConstraintNeighbor(a1, cost));

		Set<Constraint> ans = new HashSet<Constraint>();
		for (Neighbors n : this.neighbors) {
			AgentField af1 = getAgentField(n.getA1().getId());
			AgentField af2 = getAgentField(n.getA2().getId());

			for (int d1 : af1.getDomain()) {
				for (int d2 : af2.getDomain()) {
					double rnd = Main.rP2ScaleFree.nextDouble();
					if (rnd < Main.currentP2ScaleFree) {
						Agent a1 = new Agent(n.getA1().getId(), d1);
						Agent a2 = new Agent(n.getA2().getId(), d2);
						int cost = Main.getRandomInt(Main.rCost, 1, Main.costMax);
						Constraint c = new Constraint(new Neighbors(a1, a2), cost);
						ans.add(c);
						af1.addConstraintNeighbor(d1, new ConstraintNeighbor(a2, cost));
						af2.addConstraintNeighbor(d2, new ConstraintNeighbor(a1, cost));
					}
				}
			}

		}

		return ans;
	}

	/*
	 * public int calCostPerNeighborConstraintsIsSet(Neighbors n, boolean real) {
	 * Agent an1 = n.getA1(); Agent an2 = n.getA2();
	 * 
	 * for (Constraint c : constraints) {
	 * 
	 * Agent ac1 = c.getNeighbors().getA1(); Agent ac2 = c.getNeighbors().getA2();
	 * boolean sameId = an1.getId() == ac1.getId() && an2.getId() == ac2.getId();
	 * boolean sameValue; if (real) { sameValue = an1.getValue() == ac1.getValue()
	 * && an2.getValue() == ac2.getValue(); } else {// any time sameValue =
	 * an1.getAnytimeValue() == ac1.getValue() && an2.getAnytimeValue() ==
	 * ac2.getValue(); }
	 * 
	 * if (sameValue && sameId) { return c.getCost(); } }
	 * 
	 * return 0;
	 * 
	 * }
	 */

}
