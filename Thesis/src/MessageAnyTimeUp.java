import java.util.HashSet;
import java.util.Set;

public class MessageAnyTimeUp extends Message {
	private Set<Permutation> pastPermutations;
	private Set<Permutation> currentPermutations;
	
	public MessageAnyTimeUp(AgentField sender, AgentField reciever, int delay,
			Permutation p) {
		
		super(sender, reciever, -100, delay, -100);
		this.currentPermutations = new HashSet<Permutation>();
		this.currentPermutations.add(p);
				
		this.pastPermutations = new HashSet<Permutation>();
		//this.pastPermutations.add(p);
	}
	
	

	/*
	public Permutation getCurrentPermutation() {
		// TODO Auto-generated method stub
		return this.currentPermutation;
	}
*/



	public Set<Permutation> getCurrentPermutations() {
		// TODO Auto-generated method stub
		return this.currentPermutations;
	}
	
	public void setCurrentPermutations(Set<Permutation> input) {
		this.currentPermutations = input;
	}

}
