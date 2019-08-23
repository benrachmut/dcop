import java.util.HashSet;
import java.util.Set;

public class MessageAnyTimeUp extends MessageNormal {
	//private Set<Permutation> pastPermutations;
	private Permutation currentPermutation;
	
	public MessageAnyTimeUp(AgentField sender, AgentField reciever, int delay,
			int date,Permutation p) {
		
		super(sender, reciever, -100, delay, date);
		this.currentPermutation = p;
		//this.currentPermutations.add(p);
				
		//this.pastPermutations = new HashSet<Permutation>();
		//this.pastPermutations.add(p);
		
	}
	
	

	
	public Permutation getCurrentPermutation() {
		// TODO Auto-generated method stub
		return this.currentPermutation;
	}

/*

	public Set<Permutation> getCurrentPermutations() {
		// TODO Auto-generated method stub
		return this.currentPermutations;
	}
	
	public void setCurrentPermutations(Set<Permutation> input) {
		this.currentPermutations = input;
	}
*/
}
