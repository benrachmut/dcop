import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Permutation {
	// private Set<Map<Integer, Integer>> pastPermutation;
	private Map<Integer, Integer> m;
	private int cost;
	private Map<Integer, Boolean> included;

	Permutation(Map<Integer, Integer> m, int cost) {
		this.m = new HashMap<Integer, Integer>();
		for (Entry<Integer, Integer> e : m.entrySet()) {
			this.m.put(e.getKey(), e.getValue());
		}
		this.cost = cost;
	}

	Permutation(Map<Integer, Integer> m, int cost, AgentField a) {
		this(m, cost);
		included = new HashMap<Integer, Boolean>();
		for (Integer nId : a.getNeighborIds()) {
			included.put(nId, false);
		}
		included.put(a.getId(), true);
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Permutation) {
			Permutation input = (Permutation) obj;
			Map<Integer, Integer> otherMap = input.getM();
			for (Entry<Integer, Integer> e : this.m.entrySet()) {
				if (otherMap.get(e.getKey()) != e.getValue()) {
					return false;
				}

			} // for map
			return true;
		} // instance of

		return false;
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

		return "p: " + this.m + " cost = " + this.cost;
	}

	public boolean containsId(int sonId) {
		// TODO Auto-generated method stub
		return this.m.containsKey(sonId);
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

	public Permutation canAdd(Permutation msgP) {
		if (this.isCoherent(msgP) && this.differentAgentsInPermutation(msgP)) {
			Permutation combineP = combinePermutations(this, msgP);
			Map<Integer, Boolean> toAddIncluded = combineIncluded(this, msgP);
			combineP.setIncluded(toAddIncluded);
		}
		return null;
	}

	private void setIncluded(Map<Integer, Boolean> toAddIncluded) {
		this.included = toAddIncluded;

	}

	private static Map<Integer, Boolean> combineIncluded(Permutation p1, Permutation p2) {
		Map<Integer, Boolean> ans = new HashMap<Integer, Boolean>();
		Map<Integer, Boolean> includeP1 = p1.getIncluded();
		Map<Integer, Boolean> includeP2 = p2.getIncluded();

		for (Entry<Integer, Boolean> e : includeP1.entrySet()) {
			ans.put(e.getKey(), e.getValue());
		}
		for (Entry<Integer, Boolean> e : includeP2.entrySet()) {
			logicalBugFromCombineIncluded(e, ans, includeP1, includeP2);
			ans.put(e.getKey(), e.getValue());
		}

		return ans;
	}

	private static void logicalBugFromCombineIncluded(Entry<Integer, Boolean> e, Map<Integer, Boolean> ans,
			Map<Integer, Boolean> includeP1, Map<Integer, Boolean> includeP2) {
		if (ans.containsKey(e.getKey())) {
			if (includeP2.get(e.getKey()) != includeP1.get(e.getKey())) {
				System.err.println("logical bug at combineIncluded ");
			}
		}

	}

	private boolean differentAgentsInPermutation(Permutation msgP) {
		Set<Integer> sKeys = similarKeySet(msgP);
		for (Integer i : sKeys) {
			if (this.included.get(i) == msgP.getIncluded().get(i)) {
				return false;
			}
		}
		return true;
	}

	public Map<Integer, Boolean> getIncluded() {
		return this.included;
	}

}
