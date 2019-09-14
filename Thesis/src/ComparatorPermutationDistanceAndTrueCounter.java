import java.util.Comparator;

public class ComparatorPermutationDistanceAndTrueCounter implements Comparator<Permutation> {

	private Permutation currentP;
	private boolean oppositeFlag;
	public ComparatorPermutationDistanceAndTrueCounter (Permutation currentP, boolean flag) {
		this.currentP = currentP;
		this.oppositeFlag = flag;
	}

	@Override
	public String toString() {
		if (!oppositeFlag) {
			return "minDistance maxTrueCounter";
		}else {
			return "maxTrueCounter minDistance";

		}
		
	}
	
	@Override
	public int compare(Permutation p1, Permutation p2) {
		int p1SimilartyCounter = currentP.getSimilartyCounterTo(p1);
		int p2SimilartyCounter = currentP.getSimilartyCounterTo(p2);
		
		if (!oppositeFlag) {
			return compareDistanceFirst(p1, p2, p1SimilartyCounter,p2SimilartyCounter);
		}
		else {
			return compareTrueCounterFirst(p1, p2, p1SimilartyCounter,p2SimilartyCounter);
		}

		
	}

	private int compareTrueCounterFirst(Permutation p1, Permutation p2, int p1SimilartyCounter,
			int p2SimilartyCounter) {
		
		if (p1.trueCounter()>p2.trueCounter()){
			return 1;
		}
		if (p1.trueCounter()<p2.trueCounter()){
			return -1;
		}
		else {
			if (p1SimilartyCounter>p2SimilartyCounter) {
				return 1;
			}
			if (p1SimilartyCounter<p2SimilartyCounter) {
				return -1;
			}
		}
		return 0;
	}

	private int compareDistanceFirst(Permutation p1, Permutation p2, int p1SimilartyCounter, int p2SimilartyCounter) {
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
