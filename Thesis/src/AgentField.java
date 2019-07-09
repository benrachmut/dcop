import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AgentField extends Agent implements Comparable<AgentField> {
	private int[] domain;
	private int firstValue;

	private Map<Integer, Set<ConstraintNeighbor>> constraint;
	private Map<Integer, MessageRecieve> neighbor; // id and value
	private Map<Integer, MessageRecieve> neighborR;

	// private Set<Permutation>permutations;
	// private List<Integer>numOfInterationForChange;
	// private int numOfInterationForChangeCounter;
	private PotentialCost minPC;
	private int r;

	// ---tree stuff
	private AgentField father;
	private List<AgentField> sons;
	private Map<Integer, Integer> aboveMap;
	private Map<Integer, Integer> belowMap;

	private int anytimeValue;
	private int anytimeFirstValue;
	private boolean iHaveAnytimeNews;

	private int decisonCounter;
	private MessageNormal msgDown;
	private MessageNormal msgUp;
	// private Set<Permutation> permutationsBelow;
	private Set<Permutation> permutationsPast;
	private Set<Permutation> permutationsToSend;
	private Set<Permutation> sonsAnytimePermutations;
	private Map<Integer, Integer> counterAndValue;
	private Permutation bestPermuation;

	public AgentField(int domainSize, int id) {
		super(id);
		this.domain = createDomain(domainSize);

		if (Main.synch) {
			this.firstValue = Main.getRandomInt(Main.rFirstValue, 0, domainSize - 1);
			this.anytimeFirstValue =firstValue;
		} else {
			this.firstValue = -1;
			this.anytimeFirstValue = -1;
		}
		decisonCounter = 0;
		this.setFirstValueToValue();
		this.constraint = new HashMap<Integer, Set<ConstraintNeighbor>>();
		this.neighbor = new HashMap<Integer, MessageRecieve>();
		this.neighborR = new HashMap<Integer, MessageRecieve>();
		this.sons = new ArrayList<AgentField>();
		// this.permutations = new HashSet<Permutation>();
		// --- tree stuff
		this.father = null;
		aboveMap = new HashMap<Integer, Integer>();
		belowMap = new HashMap<Integer, Integer>();
		msgDown = null;
		msgUp = null;
		this.bestPermuation = null;
		// resetNumOfInterationForChange();
		// numOfInterationForChangeCounter = 0;

		setR();

		initSonsAnytimeMessages();
		this.permutationsPast = new HashSet<Permutation>();
		this.permutationsToSend = new HashSet<Permutation>();
		this.counterAndValue = new HashMap<Integer, Integer>();
		iHaveAnytimeNews = false;
	}

	public void initSonsAnytimeMessages() {
		this.sonsAnytimePermutations = new HashSet<Permutation>();
		
	}

	
	public void setFather(AgentField father) {
		this.father = father;
	}

	public Permutation getBestPermutation() {
		return this.bestPermuation;
	}
	public void resetBestPermutation() {
		this.permutationsPast = null;
	}
	public void resettopHasAnytimeNews() {
		this.iHaveAnytimeNews = false;
	}
	public boolean isTopHasAnytimeNews() {
		return this.iHaveAnytimeNews;
	}

	public void addSon(AgentField son) {
		sons.add(son);
	}

	public void setFirstValueToValue() {
		this.value = firstValue;
		this.anytimeValue = this.anytimeFirstValue;

	}

	public int getDomainSize() {
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
		// used for unsynch
		if (this.value == -1) {
			shouldChange = true;
		}

		maybeChange(shouldChange, minPotentialCost, stochastic);

	}

	public void unsynchDecide() {

		List<PotentialCost> pCosts = findPotentialCost();
		int currentPersonalCost = findCurrentCost(pCosts);

		PotentialCost minPotentialCost = Collections.min(pCosts);
		int minCost = minPotentialCost.getCost();

		boolean shouldChange = false;
		if (minCost <= currentPersonalCost) {
			shouldChange = true;
		}
		// used for unsynch
		if (this.value == -1) {
			shouldChange = true;
		}

		if (shouldChange) {
			this.value = minPotentialCost.getValue();
		}

	}

	private void maybeChange(boolean shouldChange, PotentialCost minPotentialCost, double stochastic) {
		if (shouldChange) {
			double rnd = Main.rDsa.nextDouble();

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

	}

	public void addNeighborR(int idOther) {
		this.neighborR.put(idOther, new MessageRecieve(-1, -1));

	}

	public void addNeighbor(int agentId) {
		this.neighbor.put(agentId, new MessageRecieve(-1, -1));

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

	@Override
	public int compareTo(AgentField other) {

		return this.id - other.getId();
	}

	public int getNieghborSize() {
		// TODO Auto-generated method stub
		return this.neighbor.keySet().size();
	}

	public Set<Integer> getNSetId() {
		return this.neighbor.keySet();
	}

	public List<AgentField> getSons() {
		return sons;
	}

	public AgentField getFather() {
		return this.father;
	}

	public Set<Integer> getNeighborIds() {
		return this.neighbor.keySet();

	}

	public int sonsSize() {
		return this.sons.size();
	}

	public void addBelow() {
		List<Integer> temp = new ArrayList<Integer>();
		for (int n : this.neighbor.keySet()) {
			Set<Integer> isAbove = this.aboveMap.keySet();

			boolean isAlreadyInMap = isAbove.contains(n);
			if (!isAlreadyInMap) {
				temp.add(n);
			}

		}
		for (Integer idTemp : temp) {
			this.putInBelowMap(idTemp, 0);
		}

	}

	public int getDecisonCounter() {
		return this.decisonCounter;
	}

	public void setDecisionCounter(int i) {
		this.decisonCounter = i;
		

		Permutation myPermutation = this.createCurrentPermutation();
		this.permutationsPast.add(myPermutation);

	}

	public void putInAboveMap(Integer agentId, Integer counter) {
		this.aboveMap.put(agentId, counter);
	}

	public void putInBelowMap(Integer agentId, Integer counter) {
		this.belowMap.put(agentId, counter);
	}

	public void setAllAboveMap(int input) {
		for (Entry<Integer, Integer> e : aboveMap.entrySet()) {
			e.setValue(input);
		}
	}

	public void setAllBelowMap(int input) {
		for (Entry<Integer, Integer> e : belowMap.entrySet()) {
			e.setValue(input);
		}
	}

	public void reciveUnsynchMsg(int senderId, int senderValue, int date) {
		this.reciveMsg(senderId, senderValue, date);// TODO Auto-generated method stub
		boolean isAbove = this.aboveMap.containsKey(senderId);
		int currentCounter;
		if (isAbove) {
			currentCounter = aboveMap.get(senderId);
			aboveMap.put(senderId, currentCounter + 1);
		} else {
			currentCounter = belowMap.get(senderId);
			belowMap.put(senderId, currentCounter + 1);
		}
		// if (this.id == 9) {
		// System.out.println("blah");
		// }

		Permutation myPermutation = this.createCurrentPermutation();
		this.permutationsPast.add(myPermutation);

	}

	public boolean unsynchAbilityToDecide() {
		boolean aboveOneMoreThenMe = checkAllOneAboveMe();
		boolean belowLikeMe = checkbelowLikeMe();
		if (belowLikeMe && aboveOneMoreThenMe) {
			return true;
		}
		return false;
	}

	private boolean checkbelowLikeMe() {

		if (belowMap.keySet().size() == 0) {
			return true;
		}

		for (int counterBelow : belowMap.values()) {
			if (counterBelow != this.decisonCounter) {
				return false;
			}
		}
		return true;
	}

	private boolean checkAllOneAboveMe() {

		if (aboveMap.keySet().size() == 0) {
			return true;
		}

		for (int counterAbove : aboveMap.values()) {
			if (counterAbove != this.decisonCounter + 1) {
				return false;
			}
		}
		return true;
	}

	public void setValue(int randomInt) {
		this.value = randomInt;

	}

	public boolean isFatherOfInput(AgentField input) {

		return this.father.getId() == input.getId();
	}

	public boolean isTop() {
		return this.father == null;
	}

	public boolean isLeaf() {
		return this.sons.size() == 0;
	}

	public void resetMsgUpAndDown() {
		this.msgDown = null;
		this.msgUp = null;

	}

	public boolean hasUpMessage() {
		return this.msgUp != null;
	}

	public boolean hasDownMessage() {
		return this.msgDown != null;
	}

	public int calSelfCost() {
		if (this.value == -1 || neighborIsMinusOne()) {
			return Integer.MAX_VALUE;
		}
		List<Neighbors> myNeighbors = Main.dcop.getHisNeighbors(this);
		int ans = 0;
		for (Neighbors n : myNeighbors) {
			ans = ans + Main.dcop.calRealCostPerNeighbor(n);
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
		

	}

	public boolean neighborIsMinusOne() {
		for (MessageRecieve i : this.neighbor.values()) {
			if (i.getValue() == -1) {
				return true;
			}
		}
		return false;
	}

	public Permutation createCurrentPermutation() {
		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		for (Entry<Integer, Integer> b : this.belowMap.entrySet()) {
			m.put(b.getKey(), b.getValue());
		}

		m.put(this.id, this.decisonCounter);

		for (Entry<Integer, Integer> a : this.aboveMap.entrySet()) {
			m.put(a.getKey(), a.getValue());
		}
		int selfCost = this.calSelfCost();
		return new Permutation(m, selfCost);
	}

	private static Permutation combinePermutations(Permutation p1, Permutation p2) {
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

	public void leafAddAnytimeUp() {
		Permutation p = createCurrentPermutation();
		this.permutationsPast.add(p);
		this.permutationsToSend.add(p);
	}

	public void resetPermutationsToSend() {
		this.permutationsToSend = null;
	}

	public void resetPermutationsPast() {
		this.permutationsPast = new HashSet<Permutation>();

	}

	/**
	 * case 2- called when messaged recieved is anyTimeUp from agent zero
	 * 
	 * @return
	 */
	public void createPermutataionsDueToMessage(MessageAnyTimeUp msg) {

		Permutation p = msg.getCurrentPermutation();
		
		Set<Permutation> belowCombinedWithMessage = updateSonAnytimePerm(p);
		Set<Permutation> pastCoherentWithMessage = pastCoherent(p);

		if (belowCombinedWithMessage.isEmpty() || pastCoherentWithMessage .isEmpty()) {
			return ;
		}
		for (Permutation belowP : belowCombinedWithMessage) {
			for (Permutation aboveP : pastCoherentWithMessage) {
				if (belowP.isCoherent(aboveP)) {
					Permutation pToSend = combinePermutations(belowP, aboveP);	
					handlePToSend(pToSend);
					

				}
			}
		}

	}

	private void handlePToSend(Permutation pToSend) {
		if (this.isTop()) {
			this.iHaveAnytimeNews = fatherCheckForPermutationDown(pToSend);
		}else {
			this.permutationsToSend.add(pToSend);
		}
		
	}

	private boolean fatherCheckForPermutationDown(Permutation pToSend) {
		boolean noBestPermutation = bestPermuation == null;
		boolean costIsNotInf = pToSend.getCost() < Integer.MAX_VALUE-10000;
		if (noBestPermutation && costIsNotInf) {
			doPermutationToSend(pToSend);
			return true;
		}
		else {
			int bestPermutationCost = this.bestPermuation.getCost();
			int pToSendCost = pToSend.getCost();
			if (pToSendCost < bestPermutationCost) {
				doPermutationToSend(pToSend);
				return true;
			}
		}
		return false;
	}

	private void doPermutationToSend(Permutation pToSend) {
		bestPermuation = pToSend;
		int bestCounter = bestPermuation.getM().get(id);
		this.anytimeValue = counterAndValue.get(bestCounter);
		this.counterAndValue = new HashMap<Integer,Integer>();
		// it is questionalbe!!!!
	}

	private Set<Permutation> pastCoherent(Permutation permutationFromMessage) {
		Set<Permutation> ans = new HashSet<Permutation>();
		for (Permutation pastPermutation : permutationsPast) {
			if (pastPermutation.isCoherent(permutationFromMessage)) {
				ans.add(pastPermutation);
			}
		}
		return ans;
	}



	public boolean hasAnytimeUpToSend() {
		return !this.permutationsToSend.isEmpty();
	}

	public Set<Permutation> getPermutationsToSend() {
		return this.permutationsToSend;
	}

	public void removeAllPermutationToSend() {

		this.permutationsToSend = new HashSet<Permutation>();

	}

	public Set<Permutation> updateSonAnytimePerm(Permutation msgPermutation) {	
		boolean flag = false;
		Set<Permutation> pToAdd = new HashSet<Permutation>();
		Set<Permutation> pToRemove = new HashSet<Permutation>();
		for (Permutation sonsPermutation : sonsAnytimePermutations) {
			if (msgPermutation.isCoherent(sonsPermutation)) {
				flag = true;
				pToAdd.add(combinePermutations(sonsPermutation, msgPermutation));
				pToRemove.add(sonsPermutation); // the un
			}
		}

		if (!flag) {
			this.sonsAnytimePermutations.add(msgPermutation);
			pToAdd.add(msgPermutation);
			return pToAdd;

		} else {
			this.sonsAnytimePermutations.removeAll(pToRemove);
			this.sonsAnytimePermutations.addAll(pToAdd);
		}

		
		//pToAdd will contain only permutations that are ready to be sent
		Iterator<Permutation> it = pToAdd.iterator();
		while (it.hasNext()) {
			Permutation itPermutation = it.next();
			if (!permutationContainAllSon(itPermutation)) {
				it.remove();
			}

		}
		return pToAdd;
	}

	private boolean permutationContainAllSon(Permutation itPermutation) {
		for (AgentField son : sons) {
			int sonId = son.getId();
			if (!itPermutation.containsId(sonId)) {
				return false;
			}

		}
		return true;
	}

	public void createPermutataionsDueChangeInCounter() {
		Permutation myPermutation = this.createCurrentPermutation();

		for (Permutation sonPermutation : this.sonsAnytimePermutations) {
		
			if (sonPermutation.isCoherent(myPermutation)) {
				Permutation pToSend = combinePermutations(sonPermutation, myPermutation);
				handlePToSend(pToSend);
			}
		}

	}

	public void resetCounterAndValue() {
		this.counterAndValue = new HashMap<Integer, Integer>();
	}

	public void setCounterAndValueHistory() {
		this.counterAndValue.put(decisonCounter, value);

	}

}
