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
	// private Map<AgentField, Integer> levelInTree;
	private Map<AgentField, Boolean> visited;
	// private List<AgentField> firstInTree;

	public Tree(AgentField[] aFieldInput) {
		this.afs = createAfList(aFieldInput);
		this.visited = initColorMap();
		// this.firstInTree = new ArrayList<AgentField>();
		// this.levelInTree = new HashMap<AgentField, Integer>();

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
			// firstInTree.add(firstNotVisited);
			dfs(firstNotVisited);

		}

	}

	private AgentField findFirstNotVisited() {

		for (AgentField agentField : afs) {
			if (!visited.get(agentField)) {
				return agentField;
			}
		}
		return null;
	}

	private void dfs(AgentField currentA) {
		this.visited.put(currentA, true);
		List<AgentField> sons = getSons(currentA);
		for (AgentField agentFieldSon : sons) {
			if (!visited.get(agentFieldSon)) {
				agentFieldSon.setFather(currentA);
				currentA.addSon(agentFieldSon);
				// setLevelInTreeForCurrentAgent(currentA);
				// currentA.setLevelInTree(counter++);
				dfs(agentFieldSon);
			}
		}

	}
	/*
	 * private void setLevelInTreeForCurrentAgent(AgentField currentA) { if
	 * (currentA.getFather()==null) { currentA.setLevelInTree(0); } else { int
	 * fatherLevel = currentA.getFather().getLevelInTree();
	 * currentA.setLevelInTree(fatherLevel+1); }
	 * 
	 * }
	 */

	private boolean someOneIsNotColored() {
		Collection<Boolean> colors = this.visited.values();
		for (Boolean c : colors) {
			if (!c) {
				return true;
			}
		}
		return false;
	}

	private List<AgentField> getSons(AgentField currntA) {
		Set<Integer> nSetId = currntA.getNSetId();
		List<AgentField> sons = getNeighborsOfAgentField(nSetId);
		Collections.sort(sons, new AgentNeighborComp());
		Collections.reverse(sons);
		return sons;
	}
	/*
	 * public void setIsAboveMe() { for (AgentField a : afs) { Set<Integer>
	 * neighborIds = a.getNeighborIds(); for (Integer nId : neighborIds) { int
	 * neighborLevel = getLevelInTree(nId); int aLevel = a.getLevelInTree(); if
	 * (aLevel < neighborLevel) { a.isNeighborAboveMe(nId, false); } if (aLevel >
	 * neighborLevel) { a.isNeighborAboveMe(nId, true); } else {
	 * System.err.println("bug, problem with creation of psaduo tree"); }
	 * 
	 * }
	 * 
	 * } }
	 * 
	 */
	/*
	 * private int lookForAgentInTree(AgentField a) { for (AgentField first :
	 * firstInTree) { if (first.equals(a)) { return 0; } if (first.) {
	 * 
	 * } } return 0; }
	 */
	/*
	 * private int getLevelInTree(Integer nId) { for (AgentField aN : afs) { if (nId
	 * == aN.getId()) { return aN.getLevelInTree(); } }
	 * System.err.println("logical bug from setBelowAndAbove in Tree"); return -1; }
	 */

	public void setIsAboveBelow() {
		setAbove();
		setBelow();

	}

	private void setBelow() {
		for (AgentField a : afs) {
			a.addBelow();
		}
		
	}

	private void setAbove() {
		Map<AgentField, Boolean> color = new HashMap<AgentField, Boolean>();
		for (AgentField agentField : afs) {
			color.put(agentField, false);
		}
		
		List<AgentField> breathingArray = getAllLeaves();

		while (nonColored(color)) {
			breathingArray = setIsAboveBelowPerBreathing(breathingArray, color);
		}
		
	}

	private List<AgentField> setIsAboveBelowPerBreathing(List<AgentField> breathingArray,
			Map<AgentField, Boolean> color) {
		List<AgentField> temp = new ArrayList<AgentField>();
		
		
		for (AgentField a : breathingArray) {
			AgentField father = a.getFather();
			if (father!=null) {
				if (!temp.contains(father) && !color.get(father)) {
					temp.add(father);
				}
			}
			color.put(a, true);
			while (father !=null) {
				a.isNeighborAboveMe(father.getId(),true);
				father = father.getFather();
			}
		}
				
		return temp;
	}

	private List<AgentField> getAllLeaves() {
		List<AgentField>ans = new ArrayList<AgentField>();
		for (AgentField a : afs) {
			if (a.sonsSize()==0) {
				ans.add(a);
			}
		}
		return ans;
	}

	private boolean nonColored(Map<AgentField, Boolean> color) {
		for (Boolean colored : color.values()) {
			if (!colored) {
				return true;
			}
		}
		return false;
	}
}
