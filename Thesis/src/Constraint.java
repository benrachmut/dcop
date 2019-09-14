
public class Constraint implements Comparable<Constraint> {

	private Neighbors neighbors;
	private int cost;

	public Constraint(Neighbors neighbors, int cost) {
		this.neighbors = neighbors;
		this.cost = cost;
	}

	@Override
	public boolean equals(Object obj) {
		//return super.equals(obj);
		
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


	@Override
	public String toString() {
		Agent a1 = this.neighbors.getA1();
		Agent a2 = this.neighbors.getA2();

		
		return "[{"+a1+","+a2+"}, "+this.cost+"]";
	}

	@Override
	public int compareTo(Constraint o) {
		if (this.neighbors.getA1().getValue()<o.neighbors.getA1().getValue()) {
			return 1;
		}
		if (this.neighbors.getA1().getValue()>o.neighbors.getA1().getValue()) {
			return -1;
		}
		else {
			if (this.neighbors.getA2().getValue()<o.neighbors.getA2().getValue()) {
				return 1;
			}
			if (this.neighbors.getA2().getValue()>o.neighbors.getA2().getValue()) {
				return -1;
			}
		}
		return 1;
	}

}
