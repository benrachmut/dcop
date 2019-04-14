
public class PotentialCost implements Comparable<PotentialCost>{
private int value;
private int cost;
public PotentialCost(int value, int cost) {
	super();
	this.value = value;
	this.cost = cost;
}
public int getValue() {
	return value;
}
public int getCost() {
	return cost;
}
@Override
public int compareTo(PotentialCost o) {
	if (this.cost>o.getCost()) {
		return 1;
	}
	if (this.cost<o.getCost()) {
		return -1;
	}
	return 0;
}



}
