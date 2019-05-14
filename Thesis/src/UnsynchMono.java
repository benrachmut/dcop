
public class UnsynchMono extends Solution {

	public UnsynchMono(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void solve() {
		boolean first = true;
		for (int i = 0; i < this.itiration; i++) {
			
			sendAndRecieve(i);
			sendAndRecieveTimeStemp(i);
			agentDecide();
			
			/*
			if (first) {
				sendAndRecieve(i);
				first = false;
			}//first 
			else {
				sendAndRecieveRi(i);
				if (ub == 0) {
					agentDecide();

				} else {
					if (whenAgentDecide()) {
						agentDecide();
						counter = 0;
					}
					counter++;
				}
				first = true;
			}//second
			
			*/
			addCostToList();
		} //iteration
	}

	private void sendAndRecieveTimeStemp(int i) {
		this.agentZero.createTimeStempMsgs(i);
		this.agentZero.sendTimeStempMsgs();
		
	}

	@Override
	public void agentDecide() {
		for (AgentField a : agents) {
			a.unsynchMono();
		}
	}

}
