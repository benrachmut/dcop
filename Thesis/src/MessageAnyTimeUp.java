import java.util.HashSet;
import java.util.Set;

public class MessageAnyTimeUp extends Message<Permutation> {
	//private Set<Permutation> pastPermutations;
	//private Permutation currentPermutation;
	
	public MessageAnyTimeUp(AgentField sender, AgentField reciever,Permutation p, int delay,
			int date) {
		super(sender, reciever, p, delay, date);
		
	}
	
	

	/*
	public Permutation getCurrentPermutation() {
		// TODO Auto-generated method stub
		return this.currentPermutation;
	}
	*/

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
