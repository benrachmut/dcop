import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AgentField extends Agent {
	private int[] domain;
	private int firstValue;
	
	private Map<Integer, Set<ConstraintNeighbor>> constraint;
	private Map<Integer, Integer> neighbor; // id and value
	private AgentZero agentZero;

	// private Set<Agent>neigbors;
	// private Map <Agent, Integer> neiborsConstraint;

	public AgentField(int domainSize, int id) {
		super(id);
		// this.id = id;
		this.domain = createDomain(domainSize);
		this.firstValue = Main.getRandomInt(0, domainSize - 1);
		this.setFirstValueToValue();
		this.constraint = new HashMap<Integer, Set<ConstraintNeighbor>>();
		this.neighbor = new HashMap<Integer, Integer>();
		
		//fdf
		// neigbors = new HashSet<Agent>();
		// this.neiborsConstraint = new HashMap<Agent, Integer>();
	}

	public void setFirstValueToValue() {
		this.value = firstValue;

	}

	public int getDomainSize() {
		// TODO Auto-generated method stub
		return this.domain.length;
	}

	private int[] createDomain(int domainSize) {
		int[] ans = new int[domainSize];

		for (int i = 0; i < ans.length; i++) {
			ans[i] = i;
		}

		return ans;
	}

	public Integer getFirstValue() {
		return this.firstValue;
	}

	public int[] getDomain() {
		// TODO Auto-generated method stub
		return this.domain;
	}

	public int getCurrentCost() {
		int ans = 0;
		Set<ConstraintNeighbor> cNatCurrnetValue = this.constraint.get(this.value);

		for (Entry<Integer, Integer> n : neighbor.entrySet()) {
			int nId = n.getKey();
			int nValue = n.getValue();
			Agent aTemp = new Agent(nId, nValue);
			for (ConstraintNeighbor cN : cNatCurrnetValue) {
				if (cN.getAgent().equals(aTemp)) {
					ans += cN.getCost();
				}
			}
		}
		return ans;
	}

	public void addConstraintNeighbor(int d1, ConstraintNeighbor constraintNeighbor) {
		if (!constraint.containsKey(d1)) {
			this.constraint.put(d1, new HashSet<ConstraintNeighbor>());
		}
		Set<ConstraintNeighbor> cN = this.constraint.get(d1);
		cN.add(constraintNeighbor);

	}

	public void addNeighbor(int agentId, int value) {
		this.neighbor.put(agentId, value);

	}

	public void changeValOfAllNeighbor(int valNew) {
		for (Entry<Integer, Integer> n : neighbor.entrySet()) {
			n.setValue(valNew);
		}
		
	}

	public void setAgentZero(AgentZero az) {
		this.agentZero = az;
		
	}



}
