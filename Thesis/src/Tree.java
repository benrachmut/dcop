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
	private Map<AgentField, Boolean> visited;

	public Tree(AgentField[] aFieldInput) {
		this.afs = createAfList(aFieldInput);
		this.visited = initColorMap();

	}

	private Map<AgentField, Boolean> initColorMap() {
		Map<AgentField, Boolean> ans = new HashMap<AgentField, Boolean>();
		for (AgentField agentField : afs) {
			ans.put(agentField, false);
		}

		return ans;
	}

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
				agentFieldSon.setDfsFather(currentA);
				currentA.addDfsSon(agentFieldSon);
				// setLevelInTreeForCurrentAgent(currentA);
				// currentA.setLevelInTree(counter++);
				dfs(agentFieldSon);
			}
		}

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

	private List<AgentField> getSons(AgentField currntA) {
		Set<Integer> nSetId = currntA.getNSetId();
		List<AgentField> sons = getNeighborsOfAgentField(nSetId);
		Collections.sort(sons, new AgentNeighborComp());
		// Collections.reverse(sons);
		return sons;
	}

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
			AgentField father = a.getDfsFather();
			if (father != null) {
				if (!temp.contains(father) && !color.get(father)) {
					temp.add(father);
				}
			}
			color.put(a, true);
			while (father != null) {
				if (a.getNeighborIds().contains(father.getId())) {
					a.putInAboveMap(father.getId(), 0);
				}

				father = father.getDfsFather();
			}
		}

		return temp;
	}

	private List<AgentField> getAllLeaves() {
		List<AgentField> ans = new ArrayList<AgentField>();
		for (AgentField a : afs) {
			if (a.sonsDfsSize() == 0) {
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

	public void bfs() {
		while (someOneIsNotColored()) {

			AgentField firstNotVisited = findFirstNotVisited();
			// firstInTree.add(firstNotVisited);
			bfs(firstNotVisited);

		}

	}

	private void bfs(AgentField firstNotVisited) {

		AgentField current = firstNotVisited;
		List<AgentField> q = getSons(current);
		// q.add(current);
		for (AgentField a : q) {
			current.addAnytimeSon(a);
			a.setAnytimeFather(current);
		}
		this.visited.put(current, true);

		Iterator<AgentField> it = q.iterator();

		while (it.hasNext()) {
			current = it.next();
			List<AgentField> temp = getSons(current);
			List<AgentField> toAdd = getSonsToQueue(temp, current, q);
			this.visited.put(current, true);
			it.remove();
			q.addAll(toAdd);
			it = q.iterator();

		}
	}

	private List<AgentField> getSonsToQueue(List<AgentField> temp, AgentField current, List<AgentField> q) {
		List<AgentField> ans = new ArrayList<AgentField>();
		for (AgentField a : temp) {
			if (!this.visited.get(a) && !q.contains(a)) {
				ans.add(a);
				current.addAnytimeSon(a);
				a.setAnytimeFather(current);
			}
		}
		return ans;

	}

}
