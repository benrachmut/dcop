import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class Dcsp {

	private AgentField[] agentsF;
	private double p1;// prob for neighbors
	private double p2;// prob for domain of neigbors to have cost
	private double p3;// prob of connection between neighbors to have delay;
	private int itirationGap;//
	private Set<Constraint> constraints;
	// private Map<AgentField, Set<AgentField>> neighbors;
	// private AgentZero agentZero
	private Set<Neighbors> neighbors;
	//private AgentZero agentZero;

	public Dcsp(AgentField[] agents, int d, double p1, double p2, double p3) {
		this.agentsF = agents;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		//this.itirationGap = itirationGap;
		this.neighbors = new HashSet<Neighbors>();
		this.constraints = createConstraints();
		createConnectionFlud();
	
		// this.agentZero = az;
		// this.neighbors = new HashMap<AgentField, Set<AgentField>>();
	}

	
	
	private void createConnectionFlud() {
		double rnd;
		for (Neighbors n : this.neighbors) {
			rnd = Math.random();
			if (rnd <p3  ) {
				n.setDelay12(true);
			}
			rnd = Math.random();
			if ( rnd < p3) {
				n.setDelay21(true);
			}
		}

	}

	private Set<Constraint> createConstraints() {
		Set<Constraint> ans = new HashSet<Constraint>();
		for (int i = 0; i < agentsF.length; i++) {
			for (int j = i + 1; j < agentsF.length; j++) {
				double p1Max = Math.random();
				if (p1Max <  this.p1 ) {
					AgentField af1 = agentsF[i];
					AgentField af2 = agentsF[j];

					for (int k = 0; k < af1.getDomainSize(); k++) {
						int d1 = af1.getDomain()[k];
						for (int k2 = 0; k2 < af2.getDomainSize(); k2++) {
							int d2 = af2.getDomain()[k2];
							double p2Max = Math.random();
							if (p2Max < this.p2  ) {

								Agent a1 = new Agent(i, d1);
								Agent a2 = new Agent(j, d2);
								int cost = Main.getRandomInt(1, Main.costMax);
								informFieldAgentOnConstraint(d1, d2, a1, a2, af1, af2, i, j, cost);

								Constraint c = new Constraint(new Neighbors(a1, a2), cost);
								ans.add(c);
							}
						}
					}
				}
			}
		}
		return ans;
	}

	private void informFieldAgentOnConstraint(int d1, int d2, Agent a1, Agent a2, AgentField af1, AgentField af2, int i,
			int j, int cost) {
		af1.addConstraintNeighbor(d1, new ConstraintNeighbor(a2, cost));
		af2.addConstraintNeighbor(d2, new ConstraintNeighbor(a1, cost));
		af1.addNeighbor(j, -1);
		af2.addNeighbor(i, -1);
		
		boolean flag =false;
		
		int id1 = af1.getId();
		int id2 = af2.getId();
		
		for (Neighbors n : neighbors) {
			if (id1==n.getA1().getId() && id2==n.getA2().getId()) {
				flag=true;
				break;
			}
		}
				
		if (!flag) {
			this.neighbors.add(new Neighbors(af1, af2));
		}		
		
	}

	/*
	 * public void addToNeighbors(AgentField af1, AgentField af2) {
	 * 
	 * addOneWay(af1, af2); addOneWay(af2, af1);
	 * 
	 * }
	 */
	/*
	 * private void addOneWay(AgentField af1, AgentField af2) { if
	 * (!neighbors.containsKey(af1)) { this.neighbors.put(af1, new
	 * HashSet<AgentField>()); } Set<AgentField> n = this.neighbors.get(af1);
	 * n.add(af2);
	 * 
	 * }
	 */
	public int calRealCost() {
		int ans = 0;
		
		for (Neighbors n : neighbors) {
			for (Constraint c : constraints) {
				ans+=c.getCostForNeighbors(n);
			}
		}
		
		return ans;
	}



	public Set<Neighbors> getNeighbors() {
		// TODO Auto-generated method stub
		return this.neighbors;
	}



	public void changeCommunicationProtocol(double p) {
		restartDelayFalse();
		this.p3=p;
		createConnectionFlud();
		
	}



	private void restartDelayFalse() {
		for (Neighbors n : this.neighbors) {
			n.setDelay12(false);
			n.setDelay21(false);
		}
		
	}






}
