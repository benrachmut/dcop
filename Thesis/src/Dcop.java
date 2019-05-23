import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class Dcop {

	private AgentField[] agentsF;
	//private double p1;// prob for neighbors
	//private double p2;// prob for domain of neigbors to have cost
	//private double p3;// prob of connection between neighbors to have delay;
	//private int itirationGap;//
	private Set<Constraint> constraints;
	// private Map<AgentField, Set<AgentField>> neighbors;
	// private AgentZero agentZero
	private Set<Neighbors> neighbors;
	private int iterations;
	//private AgentZero agentZero;
	//private int delayUpperBound;

	public Dcop(AgentField[] agents, int d, int iterations) {
		this.agentsF = agents;
		//this.p1 = p1;
		//this.p2 = p2;
		//this.p3 = p3;
		//this.itirationGap = itirationGap;
		//this.delayUpperBound = upperBound;

		this.neighbors = new HashSet<Neighbors>();
		this.iterations = iterations;
		this.constraints = createConstraints();
		//createConnectionFlud();
	
		// this.agentZero = az;
		// this.neighbors = new HashMap<AgentField, Set<AgentField>>();
	}
	/*
	public Dcop(Dcop dcop, double p3) {
		this.agentsF= dcop.getAgentsF();
		this.p1 =dcop.getP1();
		this.p2 = dcop.getP2();
		this.p3 = p3;
		//this.itirationGap = itirationGap;
		this.neighbors = dcop.getNeighbors();
		
		setAllNeighborFludToFalse();
		this.constraints = dcop.getConstraints();
		//createConnectionFlud();
	}
	*/

	
	/*
	private void setAllNeighborFludToFalse() {
		for (Neighbors n : neighbors) {
			n.setDelay12(false);
			n.setDelay21(false);


		}
	}
	*/
/*
	private void createConnectionFlud() {
		double rnd;
		for (Neighbors n : this.neighbors) {
			n.createFlud();
			
			rnd = Main.r.nextDouble();
			if (rnd <p3  ) {
				n.setDelay12(true);
			}
			rnd = Main.r.nextDouble();
			if ( rnd < p3) {
				n.setDelay21(true);
			}
		}

	}
*/

	private Set<Constraint> createConstraints() {
		Set<Constraint> ans = new HashSet<Constraint>();
		for (int i = 0; i < agentsF.length; i++) {
			for (int j = i + 1; j < agentsF.length; j++) {
				double p1Max = Main.rP1.nextDouble();
				if (p1Max <  Main.currentP1 ) {
					AgentField af1 = agentsF[i];
					AgentField af2 = agentsF[j];

					for (int k = 0; k < af1.getDomainSize(); k++) {
						int d1 = af1.getDomain()[k];
						for (int k2 = 0; k2 < af2.getDomainSize(); k2++) {
							int d2 = af2.getDomain()[k2];
							double p2Max = Main.rP2.nextDouble();
							if (p2Max < Main.currentP2  ) {

								Agent a1 = new Agent(i, d1);
								Agent a2 = new Agent(j, d2);
								int cost = Main.getRandomInt(Main.rCost,1, Main.costMax);
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
		addToMapsAgents(af1,j);
		
		

		
		
		
		af2.addConstraintNeighbor(d2, new ConstraintNeighbor(a1, cost));
		addToMapsAgents(af2,i);

		
	
		
		
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
			this.neighbors.add(new Neighbors(af1, af2,this.iterations));
		}		
		
	}

	private void addToMapsAgents(AgentField agentInput, int idOther) {
		agentInput.addNeighbor(idOther);
		//agentInput.addReciveveAll(idOther);
		agentInput.addNeighborR(idOther);
		//agentInput.addReciveveAllR(idOther);
		
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
			
			Agent an1 = n.getA1();
			Agent an2 = n.getA2();
			
			for (Constraint c : constraints) {
				
				Agent ac1 = c.getNeighbors().getA1();
				Agent ac2 = c.getNeighbors().getA2();
				boolean sameId = an1.getId()==ac1.getId() &&an2.getId()==ac2.getId();
				boolean sameValue = an1.getValue()==ac1.getValue() &&an2.getValue()==ac2.getValue();

				if (sameValue&&sameId ) {
					ans+=c.getCost();

				}
			}
		}
		
		return ans;
	}



	public Set<Neighbors> getNeighbors() {
		// TODO Auto-generated method stub
		return this.neighbors;
	}


/*
	public void changeCommunicationProtocol(double p) {
		restartDelayFalse();
		this.p3=p;
		createConnectionFlud();
		
	}
	*/


/*
	private void restartDelayFalse() {
		for (Neighbors n : this.neighbors) {
			n.setDelay12(false);
			n.setDelay21(false);
		}
		
	}
	*/



	public AgentField[] getAgentsF() {
		return agentsF;
	}



	



	



	public Set<Constraint> getConstraints() {
		return constraints;
	}
	
	






}
