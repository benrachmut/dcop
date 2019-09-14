import java.util.Comparator;

public class ComparatorPermutationDistanceAndMSize implements Comparator<Permutation> {

	private boolean max;
	private Permutation currentP;
	private boolean oppositeFlag;

	public ComparatorPermutationDistanceAndMSize(Permutation currentP, boolean max, boolean flag) {
		this.currentP = currentP;
		this.max = max;
		this.oppositeFlag = flag;
	}

	@Override
	public String toString() {
		if (!oppositeFlag) {
			if (max) {
				return "minDistance maxPermutationSize";
			}else {
				return "minDistance minPermutationSize" ;
			}
		}else {
			if (max) {
				return "maxPermutationSize minDistance";
			}else {
				return "minPermutationSize minDistance" ;
			}
		}
		
	}

	@Override
	public int compare(Permutation p1, Permutation p2) {
		int p1SimilartyCounter = currentP.getSimilartyCounterTo(p1);
		int p2SimilartyCounter = currentP.getSimilartyCounterTo(p2);

		if (!oppositeFlag) {
			return compareDistanceFirst(p1, p2, p1SimilartyCounter, p2SimilartyCounter);
		} else {
			return compareMSizeFirst(p1, p2, p1SimilartyCounter, p2SimilartyCounter);
		}

	}

	private int compareMSizeFirst(Permutation p1, Permutation p2, int p1SimilartyCounter, int p2SimilartyCounter) {
		if (max) {
			if (p1.getM().size() > p2.getM().size()) {
				return 1;
			}
			if (p1.getM().size() < p2.getM().size()) {
				return -1;
			}
		} else {
			if (p1.getM().size() < p2.getM().size()) {
				return 1;
			}
			if (p1.getM().size() > p2.getM().size()) {
				return -1;
			}
		} // finish max

		if (p1SimilartyCounter > p2SimilartyCounter) {
			return 1;
		}
		if (p1SimilartyCounter < p2SimilartyCounter) {
			return -1;
		}
		return 0;
	}

	private int compareDistanceFirst(Permutation p1, Permutation p2, int p1SimilartyCounter, int p2SimilartyCounter) {
		if (p1SimilartyCounter > p2SimilartyCounter) {
			return 1;
		}
		if (p1SimilartyCounter < p2SimilartyCounter) {
			return -1;
		} else {
			if (max) {
				if (p1.getM().size() > p2.getM().size()) {
					return 1;
				}
				if (p1.getM().size() < p2.getM().size()) {
					return -1;
				}
			} else {
				if (p1.getM().size() < p2.getM().size()) {
					return 1;
				}
				if (p1.getM().size() > p2.getM().size()) {
					return -1;
				}
			}
			return 0;
		}
	}

}
