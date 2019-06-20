import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Permutation {
	//private Set<Map<Integer, Integer>> pastPermutation;
	private Map<Integer, Integer> m;
	private int cost;

	Permutation(Map<Integer, Integer> m, int cost) {
		this.m = new HashMap<Integer, Integer>();
		for ( Entry<Integer, Integer> e : m.entrySet()) {
			this.m.put(e.getKey(), e.getValue());
		}
		this.cost = cost;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof Permutation) {
			Permutation input = (Permutation) obj ; 
			Map<Integer,Integer> otherMap = input.getM();
			for (Entry<Integer, Integer> e : this.m.entrySet()) {
				if (otherMap.get(e.getKey()) != e.getValue()) {
					return false;
				}
				
			}// for map
			return true;
		} // instance of
	
		return false;
	}

	Map<Integer, Integer> getM() {
		// TODO Auto-generated method stub
		return this.m;
	}

	public boolean isCoherent(Permutation input) {
		
		Set<Integer>similarKeys = similarKeySet(input);
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
		
		return "p: "+ this.m+" cost = "+ this.cost;
	}

	public boolean containsId(int sonId) {
		// TODO Auto-generated method stub
		return this.m.containsKey(sonId);
	}

}
