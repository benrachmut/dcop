import java.util.Comparator;

public class ComparatorPermutationDate implements Comparator<Permutation> {

	@Override
	public int compare(Permutation p1, Permutation p2) {
		if (p1.getDate()>p2.getDate()) {
			return 1;
		}
		if (p1.getDate()<p2.getDate()) {
			return 1;
		}
		return 0;
	}

}
