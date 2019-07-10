
public class MessageAnyTimeDown extends MessageNormal {

	private Permutation permutationSent;
	public MessageAnyTimeDown(AgentField sender, AgentField reciever, int senderValue, int delay,
			int currentIteration, Permutation bestPermutation) {
		super(sender, reciever, -100, delay, currentIteration);
		this.permutationSent = bestPermutation;
	}
	
	public Permutation getPermutationSent() {
		return this.permutationSent;
	}

}
