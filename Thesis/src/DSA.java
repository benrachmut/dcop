import java.util.ArrayList;
import java.util.List;

public class DSA extends Dcop {

	private double stochastic;
	public DSA(Dcsp dcsp, AgentField[] agents,AgentZero aZ, double stochastic) {
		super(dcsp, agents,aZ);
		this.stochastic = stochastic;
	}

	@Override
	public List<Integer> solve() {
		List<Integer>ans = new ArrayList<Integer>();
		for (int i = 0; i < this.itiration; i++) {
			sendMessageToAgentZero();
			agentZero.sendMessages();
			agentsDecide();
			if (i%10==0) {
				ans.add(this.calCost());
			}
		}
		return ans;
	}

	private void sendMessageToAgentZero() {
		for (int i = 0; i < agents.length; i++) {
			agents[i].sendMessage();
		}
		
	}

}
