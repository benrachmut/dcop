import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Permutation {
	// private Set<Map<Integer, Integer>> pastPermutation;
	private Map<Integer, Integer> m;
	private int cost;
	private Map<Integer, Boolean> included;
	private int myIndex;
	public static int index = 0;
	private List<Permutation> combinedWith;
	private AgentField creator;
	private int iterationCreated;

	Permutation(Map<Integer, Integer> m, int cost) {
		this.m = new HashMap<Integer, Integer>();
		for (Entry<Integer, Integer> e : m.entrySet()) {
			this.m.put(e.getKey(), e.getValue());
		}
		this.cost = cost;
		index = index + 1;
		this.myIndex = index;

		this.combinedWith = new ArrayList<Permutation>();
		this.creator = new AgentField(10, -1);
		this.included = new HashMap<Integer, Boolean>();

	}

	Permutation(Map<Integer, Integer> m, int cost, AgentField a) {
		this(m, cost);
		included = new HashMap<Integer, Boolean>();
		List<Integer> sonsId = getSonsId(a);
		for (Integer nId : sonsId) {
			included.put(nId, false);
		}
		included.put(a.getId(), true);
		this.creator = a;

	}

	public Permutation(Map<Integer, Integer> m, int cost, Map<Integer, Boolean> included, List<Permutation> comWith,
			AgentField creator) {

		this(m, cost);
		this.iterationCreated = Unsynch.iter;
		this.creator = creator;
		this.combinedWith = comWith;
		/*
		if (this.myIndex ==1365) {
			for (Permutation p : comWith) {
				System.out.println(p);
			}
			System.out.println();
		}
		*/
		this.included = included;
	}

	private List<Integer> getSonsId(AgentField a) {
		List<Integer> ans = new ArrayList<Integer>();
		List<AgentField> sons = a.getAnytimeSons();
		for (AgentField agentField : sons) {
			ans.add(agentField.getId());
		}
		return ans;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Permutation) {
			Permutation input = (Permutation) obj;
			boolean sameValueInMap = checkSameValuesInMap(input);
			boolean sameCost = input.getCost() == this.getCost();
			boolean sameInclude = checkIfSameInclude(input);

			if (sameCost && sameValueInMap && sameInclude) {
				return true;
			}
			/*
			 * if (sameCost && sameValueInMap) { return true; }
			 */

		} // instance of
		return false;
	}

	private boolean checkIfSameInclude(Permutation input) {
		Map<Integer, Boolean> inputInclude = input.getIncluded();
		if (inputInclude.isEmpty() && this.included.isEmpty()) {
			return true;
		}

		if (inputInclude.size() != this.included.size()) {
			return false;
		}
		Set<Integer> similarKey = similarKeySetInclude(inputInclude);
		if (similarKey.size() != this.included.size()) {
			return false;
		}

		for (Integer i : similarKey) {
			if (this.included.get(i) != inputInclude.get(i)) {
				return false;
			}
		}

		return true;
	}

	private boolean checkSameValuesInMap(Permutation input) {
		Map<Integer, Integer> otherMap = input.getM();
		for (Entry<Integer, Integer> e : this.m.entrySet()) {
			if (otherMap.get(e.getKey()) != e.getValue()) {
				return false;
			}
		} // for map
		return true;
	}

	Map<Integer, Integer> getM() {
		// TODO Auto-generated method stub
		return this.m;
	}

	public boolean isCoherent(Permutation input) {

		Set<Integer> similarKeys = similarKeySet(input);
		for (Integer i : similarKeys) {
			Integer inputVal = input.getM().get(i);
			Integer myVal = this.m.get(i);
			if (inputVal != myVal) {
				return false;
			}
		}

		return true;
	}

	public Set<Integer> similarKeySet(Permutation input) {
		Set<Integer> ans = new HashSet<Integer>();
		for (Integer myKey : this.m.keySet()) {
			if (input.getM().containsKey(myKey)) {
				ans.add(myKey);
			}
		}
		return ans;
	}

	public int getCost() {
		// TODO Auto-generated method stub
		return this.cost;
	}

	@Override
	public String toString() {

		return "pIndex:" + this.myIndex + "| map:" + this.m + "| creator:" + this.creator + "| cost:" + this.cost
				+ "| include:" + this.included;

		// +"| combined with:"+this.combinedWith;
	}

	public boolean containsId(int sonId) {
		// TODO Auto-generated method stub
		return this.m.containsKey(sonId);
	}

	public static Permutation combinePermutations(Permutation p1, Permutation p2, AgentField creator) {

		Map<Integer, Integer> m = combineMaps(p1, p2);
		int cost = combineCost(p1, p2);

		Map<Integer, Boolean> toAddIncluded = combineIncluded(p1, p2);

		List<Permutation> combineWith = createCombineWith(p1, p2);
		/*
		 * if (creator.getId() == 9 && cost == 528) { System.out.println(); }
		 */

		return new Permutation(m, cost, toAddIncluded, combineWith, creator);
	}

	private static List<Permutation> createCombineWith(Permutation p1, Permutation p2) {
		List<Permutation> ans = new ArrayList<Permutation>();

		if (!p1.getCombineWith().isEmpty()) {
			ans.addAll(p1.getCombineWith());
		}

		if (!p2.getCombineWith().isEmpty()) {
			ans.addAll(p2.getCombineWith());
		}
		ans.add(p1);
		ans.add(p2);

		return ans;
	}

	public List<Permutation> getCombineWith() {
		return this.combinedWith;
	}
	/*
	 * private static List<Permutation> combineCombinedWith(Permutation p1,
	 * Permutation p2) { List<Permutation> ans = new ArrayList<Permutation>();
	 * List<Permutation> unexplored = new ArrayList<Permutation>();
	 * unexplored.add(p1); unexplored.add(p2);
	 * 
	 * 
	 * Iterator<Permutation> it = unexplored.iterator(); Permutation current = null;
	 * while (it.hasNext()) { current = it.next(); List<Permutation> toAdd
	 * =current.getCombineWith(); boolean flag = false; for (Permutation pInAns :
	 * ans) { if (current.equals(pInAns)) { flag = true; } } if (!flag) {
	 * ans.add(current); } it.remove(); unexplored.addAll(toAdd); it =
	 * unexplored.iterator(); }
	 * 
	 * 
	 * return ans; }
	 * 
	 */

	private static int combineCost(Permutation p1, Permutation p2) {
		int ans = 0;
		if (p1.getCost() == Integer.MAX_VALUE || (p2.getCost() == Integer.MAX_VALUE)) {
			ans = Integer.MAX_VALUE;
		} else {
			ans = p1.getCost() + p2.getCost();
		}
		return ans;
	}

	private static Map<Integer, Integer> combineMaps(Permutation p1, Permutation p2) {
		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		for (Entry<Integer, Integer> e : p1.getM().entrySet()) {
			m.put(e.getKey(), e.getValue());
		}

		for (Entry<Integer, Integer> e : p2.getM().entrySet()) {
			m.put(e.getKey(), e.getValue());
		}
		return m;
	}

	public boolean getFlagReady() {
		for (Boolean b : this.included.values()) {
			if (!b) {
				return false;
			}
		}
		return true;
	}

	public Permutation canAdd(AgentField creator, Permutation msgP) {
		if (this.isCoherent(msgP) && this.differentAgentsInPermutation(msgP)
				&& this.differentComposedPermutations(msgP)) {

			Permutation combineP = combinePermutations(this, msgP, creator);

			return combineP;
		}
		return null;
	}

	private boolean differentComposedPermutations(Permutation msgP) {
		// check if in the list of msgP combined with permutation we have similar
		// permutation with this permutation.
		List<Permutation> c1 = this.combinedWith;
		List<Permutation> c2 = msgP.getCombineWith();

		boolean flag = false;
		for (Permutation p1 : c1) {
			for (Permutation p2 : c2) {
				if (p1.equals(p2)) {
					flag = true;
				}
			}
		}
		for (Permutation p1 : c1) {
			if (p1.equals(msgP)) {
				flag = true;
			}
		}
		for (Permutation p2 : c2) {
			if (p2.equals(this)) {
				flag = true;
			}
		}

		boolean ans = !flag;

		return ans;
	}
	/*
	 * private int toAddIncludeCounter(Map<Integer, Boolean> toAddIncluded) { int
	 * counter=0; for (Boolean b : toAddIncluded.values()) { if (b) { counter++; } }
	 * return counter; }
	 */
	/*
	 * private void setIncluded(Map<Integer, Boolean> toAddIncluded) { this.included
	 * = toAddIncluded; }
	 */

	private static Map<Integer, Boolean> combineIncluded(Permutation p1, Permutation p2) {
		Map<Integer, Boolean> ans = new HashMap<Integer, Boolean>();
		Map<Integer, Boolean> includeP1 = p1.getIncluded();
		Map<Integer, Boolean> includeP2 = p2.getIncluded();

		for (Integer i1 : includeP1.keySet()) {
			if (!includeP2.containsKey(i1)) {
				ans.put(i1, includeP1.get(i1));
			} else if (!includeP1.get(i1) && !includeP2.get(i1)) {
				ans.put(i1, false);
			} else {
				ans.put(i1, true);
			}
		}

		for (Integer i2 : includeP2.keySet()) {
			if (!includeP1.containsKey(i2)) {
				ans.put(i2, includeP2.get(i2));
			}

		}

		return ans;
	}

	private boolean differentAgentsInPermutation(Permutation msgP) {
		Set<Integer> sKeys = similarKeySetInclude(msgP.getIncluded());

		for (Integer i : sKeys) {
			if (this.included.get(i) != msgP.getIncluded().get(i)) {
				return true;
			}
		}
		return false;
	}

	private Set<Integer> similarKeySetInclude(Map<Integer, Boolean> otherInclude) {
		Set<Integer> ans = new HashSet<Integer>();
		for (Integer i : this.included.keySet()) {
			if (otherInclude.containsKey(i)) {
				ans.add(i);
			}
		}
		return ans;
	}

	public Map<Integer, Boolean> getIncluded() {
		return this.included;
	}

	public void createdIncluded(AgentField sender) {
		this.included = new HashMap<Integer, Boolean>();
		this.included.put(sender.getId(), true);

	}

	public static Permutation combinePermutations(Permutation p1, Permutation p2) {
		int cost;
		if (p1.getCost() == Integer.MAX_VALUE || (p2.getCost() == Integer.MAX_VALUE)) {
			cost = Integer.MAX_VALUE;
		} else {
			cost = p1.getCost() + p2.getCost();
		}
		Map<Integer, Integer> m = combineMaps(p1, p2);

		return new Permutation(m, cost);
	}

	public AgentField getCreator() {
		return this.creator;
	}

	public int getIndex() {
		// TODO Auto-generated method stub
		return this.myIndex;
	}

}
