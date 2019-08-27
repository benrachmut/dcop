import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class Dcop {

	private AgentField[] agentsF;
	private Set<Constraint> constraints;
	private Set<Neighbors> neighbors;
	private int iterations;
	//1= Uniformly random DCOPs, 2= Graph coloring problems, 3= Scale-free
	public Dcop(AgentField[] agents, int d, int iterations) {
		this.agentsF = agents;
		this.neighbors = new HashSet<Neighbors>();
		this.iterations = iterations;
		initConstraintGivenDcopVersion();
		

	}

	private void initConstraintGivenDcopVersion() {
		if (Main.dcopVersion == 1) {
			this.constraints = createConstraintsUniformlyRandomDCOP();
		}
		if (Main.dcopVersion == 2) {
			this.constraints = createConstraintsGraphColor();
		}	
		
		if (Main.dcopVersion == 3) {
			this.constraints = createConstraintsScaleFreeAB();
		}	
	}

	private Set<Constraint> createConstraintsScaleFreeAB() {
		---
		return null;
	}

	private Set<Constraint> createConstraintsGraphColor() {
		Set<Constraint> ans = new HashSet<Constraint>();
		for (int i = 0; i < agentsF.length; i++) {
			for (int j = i + 1; j < agentsF.length; j++) {
				double p1Max = Main.rP1Color.nextDouble();
				if (p1Max < Main.currentP1Color) {
					AgentField af1 = agentsF[i];
					AgentField af2 = agentsF[j];
					
					for (int k = 0; k < af1.getDomainSize(); k++) {
						int d1 = af1.getDomain()[k];
						for (int k2 = 0; k2 < af2.getDomainSize(); k2++) {
							int d2 = af2.getDomain()[k2];
							if (d1==d2) {
								Agent a1 = new Agent(i, d1);
								Agent a2 = new Agent(j, d2);
								int cost = Main.costMax;
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

	private Set<Constraint> createConstraintsUniformlyRandomDCOP() {
		Set<Constraint> ans = new HashSet<Constraint>();
		for (int i = 0; i < agentsF.length; i++) {
			for (int j = i + 1; j < agentsF.length; j++) {
				double p1Max = Main.rP1Uniform.nextDouble();
				if (p1Max < Main.currentP1Uniform) {
					AgentField af1 = agentsF[i];
					AgentField af2 = agentsF[j];

					for (int k = 0; k < af1.getDomainSize(); k++) {
						int d1 = af1.getDomain()[k];
						for (int k2 = 0; k2 < af2.getDomainSize(); k2++) {
							int d2 = af2.getDomain()[k2];
							double p2Max = Main.rP2Uniform.nextDouble();
							if (p2Max < Main.currentP2Uniform) {

								Agent a1 = new Agent(i, d1);
								Agent a2 = new Agent(j, d2);
								int cost = Main.getRandomInt(Main.rCost, 1, Main.costMax);
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
		addToMapsAgents(af1, j);

		af2.addConstraintNeighbor(d2, new ConstraintNeighbor(a1, cost));
		addToMapsAgents(af2, i);

		boolean flag = false;

		int id1 = af1.getId();
		int id2 = af2.getId();

		for (Neighbors n : neighbors) {
			if (id1 == n.getA1().getId() && id2 == n.getA2().getId()) {
				flag = true;
				break;
			}
		}

		if (!flag) {
			this.neighbors.add(new Neighbors(af1, af2, this.iterations));
		}

	}

	private void addToMapsAgents(AgentField agentInput, int idOther) {
		agentInput.addNeighbor(idOther);
		agentInput.addNeighborR(idOther);

	}

	public Set<Neighbors> getNeighbors() {
		return this.neighbors;
	}

	public AgentField[] getAgentsF() {
		return agentsF;
	}

	public Set<Constraint> getConstraints() {
		return constraints;
	}

	public List<Neighbors> getHisNeighbors(AgentField input) {
		List<Neighbors> ans = new ArrayList<Neighbors>();
		for (Neighbors n : this.neighbors) {
			Agent n1 = n.getA1();
			Agent n2 = n.getA2();
			boolean isInputInN = n1.getId() == input.getId() || n2.getId() == input.getId();
			if (isInputInN) {
				ans.add(n);
			}

		}
		return ans;
	}

	public int calCost(boolean real) {
		int ans = 0;

		for (Neighbors n : neighbors) {
			ans = ans + calCostPerNeighbor(n, real);
		}

		return ans * 2;
	}

	public int calRealSolForDebug(Map<Integer, Integer> m) {
		

		boolean x0, x1, x2, x3, x4, x5, x6, x7, x8, x9;

		if (Main.printSelfN) {
			x0 = m.get(0) == 0;
			x1 = m.get(1) == 7;
			x2 = m.get(2) == 8;
			x3 = m.get(3) == 9;
			x4 = m.get(4) == 9;
			x5 = m.get(5) == 8;
			x6 = m.get(6) == 4;
			x7 = m.get(7) == 7;
			x8 = m.get(8) == 5;
			x9 = m.get(9) == 4;
			
			if (x0 && x1 && x2 && x3 && x4 && x5 && x6 && x7 && x8 && x9) {
				Main.foundPermutationDebug = true;
			}
		}
		
		
		List<Agent> agents = getAgentsForCalReal(m);
		List<Neighbors> neighbors = getNeighborsForCalReal(agents);
		int ans = 0;
		if (Main.foundPermutationDebug) {
			for (AgentField a : agentsF) {
				ans = 0;
				int id = a.getId();
				if (id == 0) {
					System.out.println();
				}
				Iterator<Neighbors>it = neighbors.iterator();
				while (it.hasNext()) {
					Neighbors next =  it.next();
					int id1 = next.getA1().getId();
					int id2 = next.getA2().getId();
					if (!(id1 ==id || id2 ==id)) {
						it.remove();
					}
				}
				for (Neighbors n : neighbors) {
					int costPerN = calCostPerNeighborForDebug(n);
					System.out.println(n+"| "+costPerN);
					ans+=costPerN;
				}
				agents = getAgentsForCalReal(m);
				neighbors = getNeighborsForCalReal(agents);
				System.out.println();

			}
	
		}
		
	/*
		List<Agent> agents = getAgentsForCalReal(m);
		List<Neighbors> neighbors = getNeighborsForCalReal(agents);
		int ans = 0;
		*/
		for (Neighbors n : neighbors) {
			int costPerN = calCostPerNeighborForDebug(n);
			ans+=costPerN;
		}
		
		
		
		
		return ans*2;

	}

	private List<Neighbors> getNeighborsForCalReal(List<Agent> agents) {
		List<Neighbors> ans = new ArrayList<Neighbors>();

		for (int i = 0; i < agents.size(); i++) {
			for (int j = i + 1; j < agents.size(); j++) {
				Neighbors n = new Neighbors(agents.get(i), agents.get(j));
				ans.add(n);
			}
		}
		return ans;
	}

	private List<Agent> getAgentsForCalReal(Map<Integer, Integer> m) {

		List<Agent> ans = new ArrayList<Agent>();
		for (Entry<Integer, Integer> e : m.entrySet()) {
			Agent a = new Agent(e.getKey(), e.getValue());
			ans.add(a);
		}
		return ans;
	}

	public int calCostPerNeighborForDebug(Neighbors n) {
		Agent an1 =  n.getA1();
		Agent an2 =  n.getA2();
		for (Constraint c : constraints) {

			Agent ac1 = c.getNeighbors().getA1();
			Agent ac2 = c.getNeighbors().getA2();
			boolean sameId = an1.getId() == ac1.getId() && an2.getId() == ac2.getId();
			boolean sameValue;

			sameValue = an1.getValue() == ac1.getValue() && an2.getValue() == ac2.getValue();

			if (sameValue && sameId) {
				return c.getCost();
			}
		}

		return 0;

	}

	public int calCostPerNeighbor(Neighbors n, boolean real) {
		Agent an1 =  n.getA1();
		Agent an2 =  n.getA2();

		for (Constraint c : constraints) {

			Agent ac1 = c.getNeighbors().getA1();
			Agent ac2 = c.getNeighbors().getA2();
			boolean sameId = an1.getId() == ac1.getId() && an2.getId() == ac2.getId();
			boolean sameValue;
			if (real) {
				sameValue = an1.getValue() == ac1.getValue() && an2.getValue() == ac2.getValue();
			} else {// any time
				sameValue = an1.getAnytimeValue() == ac1.getValue() && an2.getAnytimeValue() == ac2.getValue();
			}

			if (sameValue && sameId) {
				return c.getCost();
			}
		}

		return 0;

	}
	
	// Function select an element base on index and return 
    // an element 
    public  List<AgentField> getRandomElement(int totalItems) 
    { 
        Random rand = Main.rHub;
        // create a temporary list for storing 
        // selected element 
        
        List<AgentField> = turnAgentArrayToArrayList();
        List<T> newList = new ArrayList<T>(); 
        List<T> copyListInput =  copyVector(list);
        for (int i = 0; i < totalItems; i++) { 
  
            // take a raundom index between 0 to size  
            // of given List 
            int randomIndex = rand.nextInt(list.size()); 
  
            // add element in temporary list 
            newList.add(list.get(randomIndex)); 
  
            // Remove selected element from orginal list 
            list.remove(randomIndex); 
        } 
        return newList; 
    }

	private List<AgentField> turnAgentArrayToArrayList() {
		List<AgentField> ans = new ArrayList<AgentField>();
		fore

		return null;
	}

}
