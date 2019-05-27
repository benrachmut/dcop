public class MGMub extends MGM {

	private int ub;

	public MGMub(Dcop dcop, AgentField[] agents, AgentZero aZ, int meanRun) {
		super(dcop, agents, aZ, meanRun);
		this.algo = "mgmUb";

		if (Main.currentP3== 0) {
			this.ub = 0;
		} else {
			this.ub = Main.currentUb;
		}
		// TODO Auto-generated constructor stub
	}

	public void solve() {
		boolean first = true;
		int counter = 0;
		for (int i = 0; i < this.itiration; i++) {
			if (!first) {
				sendAndRecieve(i);
				first = true;
			} else {
				agentsSetR();
				sendAndRecieveRi(i);
				if (ub == 0) {
					agentDecide();

				}else {
					if (counter == ub) {
						agentDecide();
						counter = 0;
					}
					counter++;
				}
				first = false;
			}
			addCostToList();
		}

	}

}