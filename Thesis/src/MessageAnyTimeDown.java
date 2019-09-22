
public class MessageAnyTimeDown extends Message<Permutation> {

	//private Permutation permutationSent;
	public MessageAnyTimeDown(AgentField sender, AgentField reciever, Permutation bestPermutation, int delay,
			int currentIteration) {
		super(sender, reciever, bestPermutation, delay, currentIteration);
		//this.permutationSent = bestPermutation;
	}
	/*
	public Permutation getPermutationSent() {
		return this.permutationSent;
	}
*/
}
