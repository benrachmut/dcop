import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AgentField extends Agent {
	private int[] domain;
	private int firstValue;

	private Map<Integer, Set<ConstraintNeighbor>> constraint;
	private Map<Integer, MessageRecieve> neighbor; // id and value
	private Map<Integer, MessageRecieve> neighborR;

	// private AgentZero agentZero;
	private Map<Integer, Boolean> allRecieve;
	private Map<Integer, Boolean> allRecieveR;

	private PotentialCost minPC;
	private int r;

	// private Set<Agent>neigbors;
	// private Map <Agent, Integer> neiborsConstraint;

	public AgentField(int domainSize, int id) {
		super(id);
		// this.id = id;
		this.domain = createDomain(domainSize);
		this.firstValue = Main.getRandomIntUniform(Main.rProblem, 0, domainSize - 1);
		this.setFirstValueToValue();
		this.constraint = new HashMap<Integer, Set<ConstraintNeighbor>>();
		this.neighbor = new HashMap<Integer, MessageRecieve>();
		this.neighborR = new HashMap<Integer, MessageRecieve>();
		this.allRecieve = new HashMap<Integer, Boolean>();
		this.allRecieveR = new HashMap<Integer, Boolean>();
		setR();
		// fdf
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

	public int getCurrentThinkCost() {
		int ans = 0;
		if (this.constraint.get(this.value) == null) {
			return 0;
		}
		Set<ConstraintNeighbor> cNatCurrnetValue = this.constraint.get(this.value);

		for (Entry<Integer, MessageRecieve> n : neighbor.entrySet()) {
			int nId = n.getKey();
			int nValue = n.getValue().getValue();
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

	public void changeValOfAllNeighbor() {
		for (Entry<Integer, MessageRecieve> n : neighbor.entrySet()) {
			n.setValue(new MessageRecieve(-1, -1));
		}

	}

	/*
	 * public void setAgentZero(AgentZero az) { this.agentZero = az;
	 * 
	 * }
	 */
	public void dsaDecide(double stochastic) {

		List<PotentialCost> pCosts = findPotentialCost();
		int currentPersonalCost = findCurrentCost(pCosts);

		PotentialCost minPotentialCost = Collections.min(pCosts);
		int minCost = minPotentialCost.getCost();

		boolean shouldChange = false;
		if (minCost < currentPersonalCost) {
			shouldChange = true;
		}

		maybeChange(shouldChange, minPotentialCost, stochastic);

	}

	private boolean checkIfAllNeighborsReported() {
		boolean allReport = !this.allRecieve.values().contains(false);
		return allReport;
	}

	private void maybeChange(boolean shouldChange, PotentialCost minPotentialCost, double stochastic) {
		if (shouldChange) {
			double rnd = Main.rAlgo.nextDouble();

			if (rnd < stochastic) {
				this.value = minPotentialCost.getValue();
			}
		}

	}

	private int findCurrentCost(List<PotentialCost> pCosts) {
		for (PotentialCost pC : pCosts) {
			if (pC.getValue() == this.value) {
				return pC.getCost();
			}
		}
		return -1;
	}

	private List<PotentialCost> findPotentialCost() {
		List<PotentialCost> pCosts = new ArrayList<PotentialCost>();
		for (int i = 0; i < domain.length; i++) {
			Set<ConstraintNeighbor> neighborsAtDomain = this.constraint.get(i);
			int costPerValue = calCostPerValue(neighborsAtDomain);
			PotentialCost pC = new PotentialCost(domain[i], costPerValue);
			pCosts.add(pC);
		}
		return pCosts;
	}

	private int calCostPerValue(Set<ConstraintNeighbor> neighborsAtDomain) {
		int ans = 0;

		if (neighborsAtDomain == null) {
			return 0;
		}
		for (ConstraintNeighbor cN : neighborsAtDomain) {
			Agent a = cN.getAgent();
			int aId = a.getId();

			int aCheckedValue = a.getValue();
			int aNeighborKnownValue = this.neighbor.get(aId).getValue();

			if (aCheckedValue == aNeighborKnownValue) {
				int costFromNeighbor = cN.getCost();
				ans += costFromNeighbor;
			}
		}
		return ans;
	}

	public void reciveMsg(int senderId, int senderValue, int dateOfOther) {

		if (Main.dateKnown) {
			int currentDate = this.neighbor.get(senderId).getDate();
			if (dateOfOther > currentDate) {
				this.neighbor.put(senderId, new MessageRecieve(senderValue, dateOfOther));
			}
		} else {
			this.neighbor.put(senderId, new MessageRecieve(senderValue, dateOfOther));
		}

		this.allRecieve.put(senderId, true);
	}

	public void setReciveAll(boolean b) {
		for (Entry<Integer, Boolean> aR : allRecieve.entrySet()) {
			aR.setValue(b);
		}

	}

	public void setR() {
		List<PotentialCost> pCosts = findPotentialCost();
		PotentialCost minPotentialCost = Collections.min(pCosts);

		int currentCost = findCurrentCost(pCosts);
		int minCost = minPotentialCost.getCost();

		this.minPC = minPotentialCost;
		if (currentCost <= minCost) {
			this.r = 0;
		}

		if (currentCost > minCost) {
			this.r = currentCost - minCost;
		}

	}

	public int getR() {
		// TODO Auto-generated method stub
		return this.r;
	}

	public void reciveRMsg(int senderId, int senderR, int dateOfOther) {
		if (Main.dateKnown) {
			int currentDate = this.neighborR.get(senderId).getDate();
			if (dateOfOther > currentDate) {
				this.neighborR.put(senderId, new MessageRecieve(senderR, dateOfOther));
			}
		} else {
			this.neighborR.put(senderId, new MessageRecieve(senderR, dateOfOther));
		}

		this.allRecieveR.put(senderId, true);

	}

	public void addNeighborR(int idOther) {
		this.neighborR.put(idOther, new MessageRecieve(-1, -1));

	}

	public void addNeighbor(int agentId) {
		this.neighbor.put(agentId, new MessageRecieve(-1, -1));

	}

	public void addReciveveAllR(int idOther) {
		this.allRecieveR.put(idOther, false);

	}

	public void addReciveveAll(int j) {
		this.allRecieve.put(j, false);

	}

	public void mgmDecide() {
		Entry<Integer, MessageRecieve> maxRInMap = getMaxRFromNeighbors();

		if (maxRInMap == null) {
			this.value = this.domain[0];
			return;
		}
		int maxRVal = maxRInMap.getValue().getValue();
		if (this.r > maxRVal) {
			this.value = this.minPC.getValue();
		}
		int maxRId = maxRInMap.getKey();
		if (this.r == maxRVal && this.id < maxRId) {
			this.value = this.minPC.getValue();
		}

	}

	private Entry<Integer, MessageRecieve> getMaxRFromNeighbors() {
		Entry<Integer, MessageRecieve> max = null;
		boolean flag = false;
		for (Entry<Integer, MessageRecieve> nr : neighborR.entrySet()) {
			if (!flag) {
				max = nr;
				flag = true;
			}
			int maxR = max.getValue().getValue();
			int nrR = nr.getValue().getValue();
			if (nrR > maxR) {
				max = nr;
			}
			int idMax = max.getKey();
			int idNr = nr.getKey();
			if (nrR == maxR && idMax > idNr) {
				max = nr;
			}
		}
		return max;
	}

	public void changeValR() {
		for (Entry<Integer, MessageRecieve> n : neighborR.entrySet()) {
			n.setValue(new MessageRecieve(-1, -1));
		}
		
	}

}
