import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
		List<Integer> sonsId = getSonsId(a);
		for (Integer nId : sonsId) {
			included.put(nId, false);
		}
		included.put(a.getId(), true);
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
			if (sameCost&&sameValueInMap) {
				return true;
			}
		} // instance of

		return false;
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

		return "p: " + this.m + ", cost: " + this.cost+", included: "+this.included;
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
			return combineP;
		}
		return null;
	}

	private int toAddIncludeCounter(Map<Integer, Boolean> toAddIncluded) {
		int counter=0;
		for (Boolean b : toAddIncluded.values()) {
			if (b) {
				counter++;
			}
		}
		return counter;
	}

	private void setIncluded(Map<Integer, Boolean> toAddIncluded) {
		this.included = toAddIncluded;
	}

	private static Map<Integer, Boolean> combineIncluded(Permutation p1, Permutation p2) {
		Map<Integer, Boolean> ans = new HashMap<Integer, Boolean>();
		Map<Integer, Boolean> includeP1 = p1.getIncluded();
		Map<Integer, Boolean> includeP2 = p2.getIncluded();

		for (Integer i1 : includeP1.keySet()) {
			if (!includeP2.containsKey(i1)) {
				ans.put(i1, includeP1.get(i1));
			}
			else if (!includeP1.get(i1) && !includeP2.get(i1)) {
				ans.put(i1, false);
			}
			else {
				ans.put(i1, true);
			}
		}
		
		for (Integer i2 : includeP2.keySet()) {
			if (!includeP1.containsKey(i2)) {
				ans.put(i2, includeP2.get(i2));
			}
			
		}
			/*	
		for (Entry<Integer, Boolean> e : includeP1.entrySet()) {
		
			
			ans.put(e.getKey(), e.getValue());
		}
		for (Entry<Integer, Boolean> e : includeP2.entrySet()) {
			//logicalBugFromCombineIncluded(e, ans, includeP1, includeP2);
			ans.put(e.getKey(), e.getValue());
		}
*/
		return ans;
	}
/*
	private static void logicalBugFromCombineIncluded(Entry<Integer, Boolean> e, Map<Integer, Boolean> ans,
			Map<Integer, Boolean> includeP1, Map<Integer, Boolean> includeP2) {
		if (ans.containsKey(e.getKey())) {
			if (includeP2.get(e.getKey()) != includeP1.get(e.getKey())) {
				System.err.println("logical bug at combineIncluded ");
			}
		}

	}
	*/

	private boolean differentAgentsInPermutation(Permutation msgP) {
		//Set<Integer> sKeys = similarKeySet(msgP);
		Set<Integer>sKeys = similarKeySetInclude(msgP.getIncluded());
		
		for (Integer i : sKeys) {
			if (this.included.get(i) != msgP.getIncluded().get(i)) {
				return true;
			}
		}
		return false;
	}

	private Set<Integer> similarKeySetInclude(Map<Integer, Boolean> otherInclude) {
		Set<Integer> ans = new HashSet<Integer>();
		for (Integer i: this.included.keySet()) {
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
		this.included= new HashMap<Integer, Boolean>();
		this.included.put(sender.getId(), true);
		
	}


}
