import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class Dcop {

	private AgentField[] agentsF;
	private Set<Constraint> constraints;
	private Set<Neighbors> neighbors;
	private int iterations;
	
	public Dcop(AgentField[] agents, int d, int iterations) {
		this.agentsF = agents;
		this.neighbors = new HashSet<Neighbors>();
		this.iterations = iterations;
		this.constraints = createConstraints();
	
	}


	private Set<Constraint> createConstraints() {
		Set<Constraint> ans = new HashSet<Constraint>();
		for (int i = 0; i < agentsF.length; i++) {
			for (int j = i + 1; j < agentsF.length; j++) {
				double p1Max = Main.rP1.nextDouble();
				if (p1Max < Main.currentP1) {
					AgentField af1 = agentsF[i];
					AgentField af2 = agentsF[j];

					for (int k = 0; k < af1.getDomainSize(); k++) {
						int d1 = af1.getDomain()[k];
						for (int k2 = 0; k2 < af2.getDomainSize(); k2++) {
							int d2 = af2.getDomain()[k2];
							double p2Max = Main.rP2.nextDouble();
							if (p2Max < Main.currentP2) {

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
	

	public int calCostPerNeighbor(Neighbors n, boolean real) {
		Agent an1 = (AgentField)n.getA1();
		Agent an2 = (AgentField)n.getA2();

		for (Constraint c : constraints) {

			Agent ac1 =  c.getNeighbors().getA1();
			Agent ac2 =  c.getNeighbors().getA2();
			boolean sameId = an1.getId() == ac1.getId() && an2.getId() == ac2.getId();
			boolean sameValue;
			if (real) {
				sameValue = an1.getValue() == ac1.getValue() && an2.getValue() == ac2.getValue();
			}else {// any time
				sameValue = an1.getAnytimeValue() == ac1.getValue()&& an2.getAnytimeValue()== ac2.getValue();

			}
			

			if (sameValue && sameId) {
				return c.getCost();
			}
		}

		return 0;

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
			ans = ans + calCostPerNeighbor(n,real);
		}

		return ans;
	}



}
