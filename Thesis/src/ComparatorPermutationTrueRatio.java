import java.util.Comparator;

public class ComparatorPermutationTrueRatio implements Comparator<Permutation> {


	@Override
	public String toString() {
		return "ComparatorPermutationTrueRatio";
	}

	@Override
	public int compare(Permutation p1, Permutation p2) {

		if (p1.trueRatio() > p2.trueRatio()) {
			return 1;
		}
		if (p1.trueRatio() < p2.trueRatio()) {
			return -1;
		}
		return 0;
	}

}
