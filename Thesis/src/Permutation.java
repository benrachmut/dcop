import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class Permutation implements Comparable<Permutation>{
	// private Set<Map<Integer, Integer>> pastPermutation;
	private SortedMap<Integer, Integer> m;
	private int cost;
	private SortedMap<Integer, Boolean> included;
	private int myIndex;
	public static int index = 0;
	private List<Permutation> combinedWith;
	private AgentField creator;
	private int iterationCreated;
	private int date;
	private SortedMap<Integer, Integer> counterMap;
	private SortedMap<Integer, Integer> dateMap;

	public Permutation(Map<Integer, Integer> m, int cost) {
		this.m = new TreeMap<Integer, Integer>();
		for (Entry<Integer, Integer> e : m.entrySet()) {
			this.m.put(e.getKey(), e.getValue());
		}
		this.cost = cost;
		index = index + 1;
		this.myIndex = index;
		this.combinedWith = new ArrayList<Permutation>();
		this.creator = new AgentField(10, -1);
		this.included = new TreeMap<Integer, Boolean>();
		this.date =Unsynch.iter;

	}

	public Permutation(Map<Integer, Integer> m, int cost, AgentField a) {
		this(m, cost);
		included = new TreeMap<Integer, Boolean>();
		List<Integer> sonsId = getSonsId(a);
		for (Integer nId : sonsId) {
			included.put(nId, false);
		}
		included.put(a.getId(), true);
		this.creator = a;
	}

	
	
	
	public Permutation(SortedMap<Integer, Integer> m, int cost, SortedMap<Integer, Boolean> included, List<Permutation> comWith,
			AgentField creator) {

		this(m, cost);
		this.iterationCreated = Unsynch.iter;
		this.creator = creator;
		this.combinedWith = comWith;
		this.included = included;
	}


	public Permutation(SortedMap<Integer, Integer> m, int cost, SortedMap<Integer, Boolean> included, List<Permutation> comWith,
			AgentField creator, SortedMap<Integer, Integer> valueDateMap,
			SortedMap<Integer, Integer> valueCounterMap) {

		this(m, cost,included,comWith,creator);
		this.dateMap = valueDateMap;
		this.counterMap = valueCounterMap;
	}
	
	public Permutation(SortedMap<Integer, Integer> valueMap, SortedMap<Integer, Integer> valueDateMap,
			SortedMap<Integer, Integer> valueCounterMap, int cost, AgentField agentField) {
		this(valueMap,cost, agentField);
		this.dateMap = valueDateMap;
		this.counterMap = valueCounterMap;
		
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
			boolean sameCost = input.getCost() == this.getCost();
			if (!sameCost) {
				return false;
			}
			boolean sameInclude = checkIfSameInclude(input);
			if (!sameInclude) {
				return false;
			}
			boolean sameValueInMap = checkSameValuesInMap(input);
			if (!sameValueInMap) {
				return false;
			}	
			else {
				return true;
			}
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
		int cost = combineCost(p1, p2);	
		SortedMap<Integer, Integer> m = combineMaps(p1, p2);
		SortedMap<Integer, Boolean> toAddIncluded = combineIncluded(p1, p2);
		List<Permutation> combineWith = createCombineWith(p1, p2);
		
		SortedMap<Integer, Integer> dates = combineDates(p1,p2);
		SortedMap<Integer, Integer> counters = combineCounters(p1,p2);
		
		return new Permutation(m, cost, toAddIncluded, combineWith, creator,dates,counters);
	}

	private static SortedMap<Integer, Integer> combineDates(Permutation p1, Permutation p2) {
		SortedMap<Integer, Integer> ans = new TreeMap<Integer,Integer>();		
		for (Integer p1Key : p1.m.keySet()) {
			int p1Date = p1.getCounter(p1Key);
			int p2Date = 0;
			if (p2.m.keySet().contains(p1Key)) {
				// im in p1 and in p2
				p2Date = p2.getCounter(p1Key);
				if (p1Date>p2Date) {
					ans.put(p1Key, p1Date);
				}else {
					ans.put(p1Key, p2Date);

				}
			}else {
				// im in p1 but not in p2
				ans.put(p1Key, p1Date);
			}
		}	
		for (Integer p2Key : p2.m.keySet()) {
			if (!p1.m.keySet().contains(p2Key)) {
				// im in p2 but not in p1
				ans.put(p2Key, p2.getCounter(p2Key));
			}
		}	
		return ans;
	}

	private Integer getCounter(Integer pKey) {
		return this.counterMap.get(pKey);
	}

	private static SortedMap<Integer, Integer> combineCounters(Permutation p1, Permutation p2) {
		SortedMap<Integer, Integer> ans = new TreeMap<Integer,Integer>();		
		for (Integer p1Key : p1.m.keySet()) {
			int p1Date = p1.getDate(p1Key);
			int p2Date = 0;
			if (p2.m.keySet().contains(p1Key)) {
				// im in p1 and in p2
				p2Date = p2.getDate(p1Key);
				if (p1Date>p2Date) {
					ans.put(p1Key, p1Date);
				}else {
					ans.put(p1Key, p2Date);

				}
			}else {
				// im in p1 but not in p2
				ans.put(p1Key, p1Date);
			}
		}
		
		for (Integer p2Key : p2.m.keySet()) {
			if (!p1.m.keySet().contains(p2Key)) {
				// im in p2 but not in p1
				ans.put(p2Key, p2.getDate(p2Key));
			}

		}
		
		
		return ans;
	}

	private int getDate(Integer pKey) {
		return this.dateMap.get(pKey);
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

	private static SortedMap<Integer, Integer> combineMaps(Permutation p1, Permutation p2) {
		SortedMap<Integer, Integer> m = new TreeMap<Integer, Integer>();
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
			// permutation with most updated counters and dates
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

	private static SortedMap<Integer, Boolean> combineIncluded(Permutation p1, Permutation p2) {
		SortedMap<Integer, Boolean> ans = new TreeMap<Integer, Boolean>();
		SortedMap<Integer, Boolean> includeP1 = p1.getIncluded();
		SortedMap<Integer, Boolean> includeP2 = p2.getIncluded();

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

	public SortedMap<Integer, Boolean> getIncluded() {
		return this.included;
	}

	public void createdIncluded(AgentField sender) {
		this.included = new TreeMap<Integer, Boolean>();
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
		return this.myIndex;
	}

	public int getDateCreated() {
		return this.date;
	}

	public double checkSimilarty(Permutation p) {
		int similarCounter = 0;
			/*
			Set<Integer>similar =similarKeySet(p);
			for (Integer i : similar) {
				if (this.m.get(i)==p.getM().get(i)) {
					similarCounter=similarCounter+1;
				}
			}
			
			return similarCounter/similar.size();
		}
		*/
			for (Entry<Integer, Integer> e : this.m.entrySet()) {

			if (!p.getM().containsKey(e.getKey())) {
				similarCounter++;
			}else {
				if (p.getM().get(e.getKey()) != e.getValue()) {
					similarCounter++;

				}
			}
		}
		double diffrenceRatio = similarCounter/m.size();
		return 1-diffrenceRatio;
		
	}

	public int getSimilartyCounterTo(Permutation input) {
		int counter = 0;
		Map<Integer,Integer> inputM=input.getM();
		Set<Integer> simInts = this.similarKeySet(input);
		for (Integer i : simInts) {
			if (this.m.get(i)==inputM.get(i)) {
				counter =counter+1;
			}
		}
		return counter;
	}

	public int trueCounter() {
		int ans = 0;
		for (Boolean b : this.included.values()) {
			if (b) {
				ans=ans+1;
			}
		}
		return ans;
	}

	public double trueRatio() {
		return this.trueCounter()/this.m.size();
	}

	@Override
	public int compareTo(Permutation o) {
		if (this.cost<o.cost) {
			return 1;
		}
		if (this.cost>o.cost) {
			return -1;
		}
		
		
		if (this.trueCounter()>o.trueCounter()) {
			return 1;
		}
		if (this.trueCounter()<o.trueCounter()) {
			return -1;
		}
		
		
		if (this.m.size()>o.m.size()) {
			return 1;
		}
		if (this.m.size()>o.m.size()) {
			return -1;
		}
		
		
		return 0;
	}
	

}
