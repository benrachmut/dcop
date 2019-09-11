import java.util.Comparator;

public class ComparatorPermutationTrueCounter implements Comparator<Permutation> {


	@Override
	public String toString() {
		return "ComparatorPermutationTrueCounter";
	}

	@Override
	public int compare(Permutation p1, Permutation p2) {

		if (p1.trueCounter() > p2.trueCounter()) {
			return 1;
		}
		if (p1.trueCounter() < p2.trueCounter()) {
			return -1;
		}
		return 0;
	}

}


