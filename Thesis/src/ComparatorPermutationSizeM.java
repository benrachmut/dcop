import java.util.Comparator;

public class ComparatorPermutationSizeM implements Comparator<Permutation> {



	@Override
	public int compare(Permutation p1, Permutation p2) {
		int size1 =p1.getM().size();
		int size2= p2.getM().size();
		
		if (size1>size2) {
			return 1;
		}
		if (size1<size2) {
			return -1;
		}
		
		
		return 0;
	}

}
