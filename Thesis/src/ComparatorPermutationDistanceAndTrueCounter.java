import java.util.Comparator;

public class ComparatorPermutationDistanceAndTrueCounter implements Comparator<Permutation> {

	private Permutation currentP;
	public ComparatorPermutationDistanceAndTrueCounter (Permutation currentP) {
		this.currentP = currentP;
	}

	@Override
	public String toString() {
		return "ComparatorDistanceAndTrueCounter";
	}
	
	@Override
	public int compare(Permutation p1, Permutation p2) {
		int p1SimilartyCounter = currentP.getSimilartyCounterTo(p1);
		int p2SimilartyCounter = currentP.getSimilartyCounterTo(p2);
		
		if (p1SimilartyCounter>p2SimilartyCounter) {
			return 1;
		}
		if (p1SimilartyCounter<p2SimilartyCounter) {
			return -1;
		}
		else {
			
			if (p1.trueCounter()>p2.trueCounter()){
				return 1;
			}
			if (p1.trueCounter()<p2.trueCounter()){
				return -1;
			}
			return 0;
		}

		
	}

}
