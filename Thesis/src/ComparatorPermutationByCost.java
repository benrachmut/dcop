import java.util.Comparator;

public class ComparatorPermutationByCost implements Comparator<Permutation> {

	@Override
	public int compare(Permutation p1, Permutation p2) {
		if (p1.getCost()>p2.getCost()) {
			return 1;
		}
		if (p1.getCost()<p2.getCost()) {
			return -1;
		}
		return 0;
	}

}
