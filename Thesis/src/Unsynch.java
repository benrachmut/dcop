import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Unsynch  extends Solution {
	protected List<AgentField> whoCanDecide;

	public Unsynch(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.whoCanDecide = new ArrayList<AgentField>();
	}

	@Override
	public void solve() {		
		
		List<AgentField> fathers = findHeadOfTree();
		for (int i = 0; i < this.iteration; i++) {	
			System.out.println("---start iteration: "+i+"---");

			updateWhoCanDecide(i);			
			agentDecide(i);		
			afterDecideTakeAction(i);			
			List<MessageNormal> msgToSend = agentZero.handleDelay();
			agentsSendMsgs(msgToSend);
			createAnytimeUp();
			createAnytimeDown(fathers, i);			
			addCostToTables();
			
			
			//---- for debug
			
			//printDecisionCounter(i);
			
			//printPersonalPermutations(i);
			
			//printCreatedAnytimeMsgUp(i);

			//System.out.println("---finish iteration: "+i+"---");
/*
			if (i==8) {
				System.out.println();
			}
*/		
		}
	}
	
	
	private void printCreatedAnytimeMsgUp(int i) {
		List<MessageAnyTimeUp> atu = new ArrayList<MessageAnyTimeUp>();

		for (MessageNormal m : agentZero.getMsgBox()) {
			if (m instanceof MessageAnyTimeUp) {
				atu.add((MessageAnyTimeUp)m);
			}
		}
		//System.out.println("iteration, from, to, permutation, cost");

		for (MessageAnyTimeUp m : atu) {
		
			System.out.println("iteration: "+i+", from: a"+m.getSender().getId()+", to: a"+m.getReciever().getId()+", "+m.getCurrentPermutation() );
		}
		
	}

	private void printPersonalPermutations(int i) {
		for (AgentField a : agents) {
			System.out.println("a"+a.getId()+" at iteration "+i+":");
			Permutation p =  a.createCurrentPermutationNonMonotonic();
			for (Entry<Integer, Integer> e : p.getM().entrySet()) {
				System.out.println("   a"+e.getKey()+": "+e.getValue());
			}
			
		}
		
	}

	//for debug
	private void printDecisionCounter(int i) {
		System.out.println("iteration "+i+":");
		/*
		for (AgentField a : agents) {
			System.out.print("a"+a.getId()+":"+a.getDecisonCounter()+",");
		}
		System.out.println();
		*/
		for (AgentField a : agents) {
			System.out.print(+a.getDecisonCounter()+",");
		}
		System.out.println();

		
	}


	private void addCostToTables() {
		addCostToList();
		addAnytimeCostToList();
	}

	public List<AgentField> findHeadOfTree() {
		List<AgentField> ans = new ArrayList<AgentField>();
		for (AgentField a : agents) {
			if (a.getDfsFather() == null) {
				ans.add(a);
			}
		}
		return ans;
	}
	protected abstract void updateWhoCanDecide(int i);
	// protected abstract void agentDecide();
	protected abstract void afterDecideTakeAction(int i);

	public abstract void agentsSendMsgs(List<MessageNormal> msgToSend);
	public abstract void createAnytimeUp();
	public abstract void createAnytimeDown(List<AgentField> fathers, int date);
	
	
	
	
	//protected abstract void createAnytimeUp();
	//protected abstract void createAnytimeDown(List<AgentField> fathers, int date);



	protected boolean atlistOneAgentMinusOne(boolean real) {

		for (AgentField a : agents) {

			if (real) {
				if (a.getValue() == -1) {
					return true;
				}
			} else {
				if (a.getAnytimeValue() == -1) {
					return true;
				}
			}

		}
		return false;
	}
	
	@Override
	public void addCostToList() {
		if (atlistOneAgentMinusOne(true)) {
			this.realCost.add(Integer.MAX_VALUE);
		} else {
			super.addCostToList();
		}
	}
	
	
	@Override
	public void addAnytimeCostToList() {
		if (atlistOneAgentMinusOne(false)) {
			this.anytimeCost.add(Integer.MAX_VALUE);
		} else {
			super.addAnytimeCostToList();
		}

	}
	

	
	
}
