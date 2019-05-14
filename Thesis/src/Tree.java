import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Tree {

	private List<AgentField> afs;
	private Map<AgentField, List<AgentField>> neighborsMap;
	private Map<AgentField, Boolean> visited;

	public Tree(AgentField[] aFieldInput) {
		this.afs = createAfList(aFieldInput);
		this.visited = initColorMap();
		// this.neighborsMap = createNeighborsMap();
	}

	private Map<AgentField, Boolean> initColorMap() {
		Map<AgentField, Boolean> ans = new HashMap<AgentField, Boolean>();
		for (AgentField agentField : afs) {
			ans.put(agentField, false);
		}

		return ans;
	}

	// adds only uncolored neighbors

	private List<AgentField> getNeighborsOfAgentField(Set<Integer> nSetId) {
		List<AgentField> aFNeighbors = new ArrayList<AgentField>();
		for (Integer i : nSetId) {
			for (AgentField neighbor : afs) {
				if (i == neighbor.getId() && !this.visited.get(neighbor)) {
					aFNeighbors.add(neighbor);
					break;
				}
			}
		}
		return aFNeighbors;
	}

	private List<AgentField> createAfList(AgentField[] aFieldInput) {
		List<AgentField> ans = new ArrayList<AgentField>();

		for (AgentField temp : aFieldInput) {
			ans.add(temp);
		}
		Collections.sort(ans, new AgentNeighborComp());
		Collections.reverse(ans);
		return ans;
	}

	public void dfs() {
		while (someOneIsNotColored()) {
			AgentField firstNotVisited = findFirstNotVisited();
			dfs( firstNotVisited);
	}}

	private AgentField findFirstNotVisited() {
		for (AgentField agentField : afs) {
			if (!visited.get(agentField)) {
				return agentField;
			}
		}
		return null;
	}

	private void dfs(AgentField currntA) {

		// AgentField currntA= this.afs.get(0);
		this.visited.put(currntA, true);
		
		List<AgentField> sons = getSons(currntA);

		for (AgentField agentFieldSon : sons) {
			if (!visited.get(agentFieldSon)) {
				agentFieldSon.setFather(currntA);
				currntA.addSon(agentFieldSon);
				dfs(agentFieldSon);
			}
		}

		/*
		 * stack.add(firstNotColored()); AgentField neighborInStack =
		 * stack.get(stack.size() - 1); stack.remove(o) stack =
		 * addToStack(neighborInStack, stack);
		 * 
		 */

		/*
		 * //Iterator<AgentField> it = stack.iterator();
		 * 
		 * // stack.add(afs.get(0)); // stack = addToStack(afs.get(0), stack,it); while
		 * (someOneIsNotColored()) { stack.add(firstNotColored()); while (it.hasNext())
		 * { AgentField neighborInStack = stack.get(stack.size() - 1);
		 * addToStack(neighborInStack, stack);
		 * 
		 * } }
		 */

	}

	private boolean someOneIsNotColored() {
		Collection<Boolean> colors = this.visited.values();
		for (Boolean c : colors) {
			if (!c) {
				return true;
			}
		}
		return false;
	}
	/*
	 * private AgentField firstNotColored() { for (AgentField agentField : afs) { if
	 * (!colorMap.get(agentField)) { return agentField; } } return null; }
	 * 
	 * private boolean someOneIsNotColored() { Collection<Boolean>colors =
	 * this.colorMap.values(); for (Boolean c : colors) { if (!c) { return true; } }
	 * return false; } /* private List<AgentField> addToStack(AgentField colored,
	 * List<AgentField> stack) { this.colorMap.put(colored, true); //it.remove();
	 * Set<Integer> nSetId = colored.getNSetId(); List<AgentField> sons =
	 * getNeighborsOfAgentField(nSetId); Collections.sort(sons, new
	 * AgentNeighborComp()); Collections.reverse(sons);
	 * 
	 * AgentField father = colored;
	 * 
	 * if (!sons.isEmpty()) { setFatherAndSons(father, sons); } stack.addAll(sons);
	 * return stack; }
	 * 
	 * private void setFatherAndSons(AgentField father, List<AgentField> sons) { for
	 * (AgentField son : sons) { father.addSon(son); son.setFather(father); }
	 * 
	 * }
	 */

	private List<AgentField> getSons(AgentField currntA) {
		Set<Integer> nSetId = currntA.getNSetId();
		List<AgentField> sons = getNeighborsOfAgentField(nSetId);
		Collections.sort(sons, new AgentNeighborComp());
		Collections.reverse(sons);
		return sons;
	}
}
