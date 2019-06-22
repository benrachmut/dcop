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

	private int decisonCounter;
	private Message msgDown;
	private Message msgUp;
	// private Set<Permutation> permutationsBelow;
	private Set<Permutation> permutationsPast;
	private Set<Permutation> permutationsToSend;
	private Set<Permutation> sonsAnytimePermutations;

	public AgentField(int domainSize, int id) {
		super(id);
		this.domain = createDomain(domainSize);

		if (Main.synch) {
			this.firstValue = Main.getRandomInt(Main.rFirstValue, 0, domainSize - 1);
		} else {
			this.firstValue = -1;
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
		// resetNumOfInterationForChange();
		// numOfInterationForChangeCounter = 0;

		setR();

		initSonsAnytimeMessages();
		this.permutationsPast = new HashSet<Permutation>();
		this.permutationsToSend = new HashSet<Permutation>();
	}

	public void initSonsAnytimeMessages() {
		this.sonsAnytimePermutations = new HashSet<Permutation>();
		/*
		 * for (AgentField i : sons) { this.sonsAnytimePermutations.put(i.getId(), new
		 * HashSet<Permutation>()); }
		 */
	}

	/*
	 * public void resetNumOfInterationForChange() { this.numOfInterationForChange =
	 * new ArrayList<Integer>(); numOfInterationForChangeCounter = 0;
	 * 
	 * }
	 */
	public void setFather(AgentField father) {
		this.father = father;
	}

	public void addSon(AgentField son) {
		sons.add(son);
	}

	public void setFirstValueToValue() {
		this.value = firstValue;

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
		if (this.id == 9) {
			System.out.println("blah");
		}
		
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
		if (this.id == 9) {
			System.out.println("blah");
		}
		
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

	public void setMsgUpAndDown(Message m) {
		this.msgDown = m;
		this.msgUp = m;

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
		/*
		 * if(Main.anyTime) { whenRecieveDoAnyTime(); }
		 */

	}

	/*
	 * private void whenRecieveDoAnyTime() { if (neighborIsMinusOne()) { return; }
	 * boolean amILeaf = this.sons.size() ==0; if (amILeaf) { Permutation
	 * firstPermutation = this.createCurrentPermutation(); // create any time up
	 * with self cost // any time message will be include counter and self cost
	 * 
	 * AgentField sender, AgentField reciever, int senderValue, int delay, int
	 * currentIteration,
	 * 
	 * Message anyTimeUp = new MessageAnyTimeUp(this,this.father, ,
	 * firstPermutation); }
	 * 
	 * boolean fisible = checkCounterFisibility(); if (fisible) { Permutation p =
	 * createCurrentPermutation(); this.permutations.add(p); }
	 * 
	 * }
	 * 
	 */
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
	/*
	 * private void addPartialPermutation(Permutation p, boolean above) { //if
	 * (above) { // this.permutationsAbove.add(p); //} else {
	 * //this.permutationsBelow.add(p); //} //addPermutationToSend(p, above);
	 * 
	 * }
	 */

	/*
	 * private void addPermutationToSend(Permutation pInput, boolean above) {
	 * boolean cohirent;
	 * 
	 * Set<Permutation> iteratePermutations;
	 * 
	 * if (above) { iteratePermutations = permutationsBelow; } else {
	 * iteratePermutations = permutationsAbove; }
	 * 
	 * for (Permutation checked : iteratePermutations) { cohirent =
	 * checked.isCoherent(pInput); if (cohirent) { Permutation pToAdd =
	 * combinePermutations(checked, pInput); // if (pToAdd.feasible()) {
	 * this.permutationsToSend.add(pToAdd); // } } }
	 */
	/*
	 * if (above) { Permutation pAbove = p; for (Permutation pBelow :
	 * permutationsBelow) { cohirent = pBelow.isChoirent(pAbove); if (cohirent) {
	 * 
	 * } } } else { Permutation pBelow = p; for (Permutation pAbove :
	 * permutationsAbove) { cohirent = pBelow.isChoirent(pAbove); }
	 * 
	 * }
	 */

	private static Permutation combinePermutations(Permutation p1, Permutation p2) {
		int cost;
		if (p1.getCost() == Integer.MAX_VALUE || (p2.getCost() == Integer.MAX_VALUE)) {
			 cost = Integer.MAX_VALUE;
		}else {
			cost = p1.getCost() + p2.getCost();
		}
		Map<Integer, Integer> m = combineMaps(p1, p2);

		boolean isCohirent = p1.isCoherent(p2);
		if (!isCohirent) {
			System.out.println("we have a bug");
		}

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

	public void setPermutationsToSend(HashSet<Permutation> input) {
		this.permutationsToSend = input;
	}

	public void setPermutationsPast(HashSet<Permutation> input) {
		this.permutationsPast = input;

	}

	/**
	 * case 1- called from agentZero when message is sent, the agent immdiatly trys
	 * to create an anytime up. try current permutation with all messages that
	 * recived from below
	 */
	/*
	 * public void addAnytimeUp() { Permutation p = this.createCurrentPermutation();
	 * // if (p.feasible()) { createPossiblePermutations();
	 * //addPermutationToSend(p, above); //} this.permutationsAbove.add(p);
	 * 
	 * //this.addPartialPermutation(p, true); // }
	 * 
	 * }
	 */

	/**
	 * case 2- called when messaged recieved is anyTimeUp from agent zero
	 */
	public void createPermutataionsDueToMessage(MessageAnyTimeUp msg) {

		Permutation p = msg.getCurrentPermutation();
		if (this.id == 9) {
			System.out.println("blah");
		}
		Set<Permutation> belowCombinedWithMessage = updateSonAnytimePerm(p);
		Set<Permutation> aboveCoherentWithMessage = aboveCoherent(p);

		
		if (belowCombinedWithMessage.isEmpty() || aboveCoherentWithMessage.isEmpty()) {
			return;
		}
		for (Permutation belowP : belowCombinedWithMessage) {
			for (Permutation aboveP : aboveCoherentWithMessage) {
				if (belowP.isCoherent(aboveP)) {

					Permutation pToSend = combinePermutations(belowP, aboveP);

					this.permutationsToSend.add(pToSend);

				}
			}
		}

	}

	private Set<Permutation> aboveCoherent(Permutation permutationFromMessage) {

		Set<Permutation> ans = new HashSet<Permutation>();
		for (Permutation pastPermutation : permutationsPast) {
			if (pastPermutation.isCoherent(permutationFromMessage)) {
				ans.add(pastPermutation);
			}
		}
		return ans;
	}

	/*
	 * this.sonsAnytimePermutations.get(idSender).add(permuataionFromMsg);
	 * 
	 * 
	 * //addPermutationToSend(p, above); //this.addPartialPermutation(p, false);
	 * 
	 * boolean cohirent;
	 * 
	 * Set<Permutation> iteratePermutations;
	 * 
	 * if (above) { iteratePermutations = permutationsBelow; } else {
	 * iteratePermutations = permutationsAbove; }
	 * 
	 * 
	 * for (Permutation pAbove : permutationsAbove) { Set<Permutation>
	 * permuataionCombo = new HashSet<Permutation>(); permuataionCombo.add(pAbove);
	 * 
	 * Set<Permutation> allCombo = getAllCombo(idSender);
	 * 
	 * 
	 * for (Set<Permutation> pSet: sonsAnytimePermutations.values()) {
	 * 
	 * 
	 * 
	 * }
	 * 
	 * 
	 * 
	 * for (Integer sonId : sonsAnytimePermutations.keySet()) { if
	 * (sonsAnytimePermutations.get(sonId).isEmpty()) { return; } if (sonId
	 * !=idSender ) { for (Permutation pSon : sonsAnytimePermutations.get(sonId)) {
	 * permuataionCombo.add(pSon); } }
	 * 
	 * } }
	 * 
	 * }
	 * 
	 * permuataionCombo.add(pTemp); permuataionCombo.add(permuataionFromMsg);
	 * permuataionCombo.add(e)
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * /* cohirent = checked.isChoirent(pInput); if (cohirent) { Permutation pToAdd
	 * = combinePermutations(checked, pInput); //if (pToAdd.feasible()) {
	 * this.permutationsToSend.add(pToAdd); //} } }
	 */

	// i is used for recursion, for the initial call this should be 0
	/*
	 * private List<List<Permutation>> belowCoherent(MessageAnyTimeUp msg) {
	 * 
	 * 
	 * 
	 * 
	 * List<List<Permutation>> getBelowCoherentWithMessage =
	 * getBelowCoherentWithMessage(p, idOfSender);
	 * 
	 * 
	 * List<List<Permutation>> sonsCombo = test(getBelowCoherentWithMessage, 0);
	 * 
	 * 
	 * List<List<Permutation>> sonsComboCoherent = coherentWithSelf(sonsCombo);
	 * return sonsComboCoherent; }
	 */
	/*
	 * private List<List<Permutation>> coherentWithSelf(List<List<Permutation>>
	 * sonsCombo) { List<List<Permutation>> ans = new
	 * ArrayList<List<Permutation>>(); for (List<Permutation> list : sonsCombo) { if
	 * (comboCoherent(list)) { ans.add(list); } } return ans; }
	 */

	/*
	 * private static boolean comboCoherent(List<Permutation> list) { for (int i =
	 * 0; i < list.size(); i++) { for (int j = i + 1; j < list.size(); j++) { if
	 * (!list.get(i).isCoherent(list.get(j))) { return false; } } }
	 * 
	 * return true; }
	 */
	/*
	 * private Set<Permutation> getBelowCoherentWithMessage(Permutation
	 * pFromMessage, Integer idOfSender) { Set<Permutation> ans = new
	 * HashSet<Permutation>();
	 * 
	 * for (Permutation pFromSons : sonsAnytimePermutations) { if
	 * (pFromSons.isCoherent(pFromMessage)) {
	 * 
	 * } }
	 * 
	 * for (Entry<Integer, Set<Permutation>> e : sonsAnytimePermutations.entrySet())
	 * { List<Permutation> temp = new ArrayList<Permutation>();
	 * 
	 * if (!e.getValue().isEmpty() && e.getKey()!=idOfSender) { for (Permutation
	 * pFromSon : e.getValue()) { if (pFromSon.isCoherent(pFromMessage)) {
	 * temp.add(pFromSon); } } } ans.add(temp); }
	 * 
	 * return ans; }
	 */
	/*
	 * public void reciveMsgAnyTimeUp(Message msg) { MessageAnyTimeUp msgATU =
	 * (MessageAnyTimeUp) msg; Permutation p = msgATU.getPermutation(); int upCost =
	 * p.getCost(); }
	 */

	public boolean hasAnytimeUpToSend() {
		return !this.permutationsToSend.isEmpty();
	}

	public Set<Permutation> getPermutationsToSend() {
		return this.permutationsToSend;
	}

	public void removeAllPermutationToSend() {

		this.permutationsToSend = new HashSet<Permutation>();

	}

	/*
	 * public void headAddPermutationToSend(MessageAnyTimeUp mau) { Permutation
	 * pFromMsg = mau.getCurrentPermutation(); Permutation pMyPermutation =
	 * createCurrentPermutation();
	 * 
	 * if (pFromMsg.isCoherent(pMyPermutation)) { Permutation comb =
	 * combinePermutations(pFromMsg, pMyPermutation);
	 * this.permutationsToSend.add(comb); }
	 * 
	 * }
	 */
	/*
	 * private List<List<Permutation>> test(List<List<Permutation>> input, int i) {
	 * 
	 * // stop condition if (i == input.size()) { // return a list with an empty
	 * list List<List<Permutation>> result = new ArrayList<List<Permutation>>();
	 * result.add(new ArrayList<Permutation>()); return result; }
	 * 
	 * List<List<Permutation>> result = new ArrayList<List<Permutation>>();
	 * List<List<Permutation>> recursive = test(input, i + 1); // recursive call
	 * 
	 * // for each element of the first list of input for (int j = 0; j <
	 * input.get(i).size(); j++) { // add the element to all combinations obtained
	 * for the rest of the lists for (int k = 0; k < recursive.size(); k++) { //
	 * copy a combination from recursive List<Permutation> newList = new
	 * ArrayList<Permutation>(); for (Permutation integer : recursive.get(k)) {
	 * newList.add(integer); } // add element of the first list
	 * newList.add(input.get(i).get(j)); // add new combination to result
	 * result.add(newList); } } return result; }
	 */

	public Set<Permutation> updateSonAnytimePerm(Permutation msgPermutation) {
		// Permutation msgPermutation = msgPermutation.getCurrentPermutation();
		// this.sonsAnytimePermutations.get(msg.getSender().getId()).add(msg.getCurrentPermutation());
		boolean flag = false;

		Set<Permutation> pToAdd = new HashSet<Permutation>();
		Set<Permutation> pToRemove = new HashSet<Permutation>();

		for (Permutation sonsPermutation : sonsAnytimePermutations) {
		
			if (msgPermutation.isCoherent(sonsPermutation)) {
				flag = true;
				
				pToAdd.add(combinePermutations(sonsPermutation, msgPermutation));
				pToRemove.add(sonsPermutation);
			}
		}

		if (!flag) {
			//if (this.id == 3) {
				//System.out.println();
			//}
			this.sonsAnytimePermutations.add(msgPermutation);
			pToAdd.add(msgPermutation);
			return pToAdd;

		} else {
			this.sonsAnytimePermutations.removeAll(pToRemove);
			this.sonsAnytimePermutations.addAll(pToAdd);
		}

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
		//this.permutationsPast.add(myPermutation);
		
		
		for (Permutation sonPermutation : this.sonsAnytimePermutations) {
			//if (this.id == 3) {
				//System.out.println();
			//}
			if (sonPermutation.isCoherent(myPermutation)) {
				Permutation pToSend = combinePermutations(sonPermutation, myPermutation);
				
				
				this.permutationsToSend.add(pToSend);
			}
		}

	}

}
