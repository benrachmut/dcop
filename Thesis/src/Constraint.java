
public class Constraint {

	private Neighbors neighbors;
	private int cost;

	public Constraint(Neighbors neighbors, int cost) {
		this.neighbors = neighbors;
		this.cost = cost;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Constraint)) {
			return false;
		}
		Constraint c = (Constraint) obj;
		return this.neighbors.equals(c.getNeighbors()) && c.getCost() == cost;
	}

	public Neighbors getNeighbors() {
		return neighbors;
	}

	public int getCost() {
		return cost;
	}

	public int getCostForNeighbors(Neighbors n) {
		if (this.neighbors.equals(n)) {
			return this.cost;
		}
		else {
			return 0;
		}
	}
	@Override
	public String toString() {
		Agent a1 = this.neighbors.getA1();
		Agent a2 = this.neighbors.getA2();

		
		return "[{"+a1+","+a2+"}, "+this.cost+"]";
	}

}
