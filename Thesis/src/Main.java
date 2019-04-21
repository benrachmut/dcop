import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {

	// versions
	static String algo = "dsa4";// dsa3,dsa6,dsa9
	static boolean dateKnown;

	// -- variables of dcop problem
	static int A = 50; // 50 number of agents
	static int D = 10; // 10 size of domain for each agent
	static double[] p1s = { 0.2 }; // 0.2 prob for agents to be neighbors
	static double[] p2s = { 1 }; // 1 prob of domain selection to have a cost
	static int costMax = 100; // 100 the max value of cost

	// -- communication protocol
	static double[] p3s = { 0, 0.5, 1 }; // prob of communication to have delay
	static boolean[] dateKnowns = { true, false };
	static int[] delayUBs = { 5, 10, 25, 50 };//{ 5, 10, 25, 50, 100 };
	static double[] p4s = {0, 0.2, 0.6, 0.9};//{ 0, 0.2, 0.5, 0.8, 0.9 }; // prob of communication to have delay

	// -- Experiment time
	static int meanReps = 10; // number of reps for every solve process
	static int iterations = 1000;

	// -- characters
	static AgentField[] agents;
	static AgentZero agentZero;

	// -- other
	static List<String> solutions = new ArrayList<String>();;
	static Random rProblem = new Random();
	static Random rAlgo = new Random();

	public static void main(String[] args) {
		// initVariables();
		rProblem.setSeed(1);
		rAlgo.setSeed(1);
		runExperiment();
		printDcops();
	}

	private static void printDcops() {
		BufferedWriter out = null;
		try {
			FileWriter s = new FileWriter(algo+".csv");
			out = new BufferedWriter(s);
			String header = "p3,date_known,ub,p4,algo,p1,p2,mean_run,iteration,real_cost";
			out.write(header);
			out.newLine();

			for (String o : solutions) {
				out.write(o);
				out.newLine();
			}

			out.close();
		} catch (Exception e) {
			System.err.println("Couldn't open the file");
		}

	}

	private static void runExperiment() {

		for (Double p1 : p1s) {
			for (Double p2 : p2s) {
				for (int i = 0; i < meanReps; i++) {
					Dcop dcop = createDcop(p1, p2);
					for (Double p3 : p3s) {
						for (boolean dK : dateKnowns) {
							dateKnown = dK;
							for (Integer delayUB : delayUBs) {
								for (Double p4 : p4s) {
									// ---- protocol ----
									agentZero.changeCommunicationProtocol(p3, delayUB, p4);
									String protocol = p3 + "," + dK+","+delayUB + "," + p4;

									// ---- find solution ----
									Solution algo = selectedAlgo(dcop, i);

									System.out.println(protocol + "," + algo);

									// ---- restart ----
									restartBetweenAlgo(algo, protocol);
									if (p3 == 0)break;
								} // p4
								if (p3 == 0)break;
							} // ub
							if (p3 == 0)break;
						} // date known
					} // p3
					printDcops();
				} // means run
			} // p2
		} // p1

	}

	private static Solution selectedAlgo(Dcop dcop, int i) {
		Solution ans = null;
		boolean dsa4 = algo.equals("dsa4");
		boolean dsa7 = algo.equals("dsa7");
		boolean dsa9 = algo.equals("dsa9");

		if (dsa4) {
			ans = new DSA(dcop, agents, agentZero, i, 0.4);
		}
		if (dsa7) {
			ans = new DSA(dcop, agents, agentZero, i, 0.7);
		}
		if (dsa9) {
			ans = new DSA(dcop, agents, agentZero, i, 0.9);
		}
		return ans;
	}

	private static void addToSolutionString(Solution sol, String protocol) {
		for (int i = 0; i < iterations; i++) {
			String o = new String(protocol + "," + sol.toString() + "," + i + "," + sol.getRealCost().get(i));
			solutions.add(o);
		}

	}

	private static Dcop createDcop(double p1, double p2) {
		agents = initAgentsFieldArray();
		Dcop dcop = new Dcop(agents, D, p1, p2, iterations);
		agentZero = new AgentZero(iterations, dcop.getNeighbors());
		//agentFieldMeetAgentZero();
		return dcop;// create dcsp problem given p1 and p2
					// and fix neighbors return null;
	}
/*
	private static void agentFieldMeetAgentZero() {
		for (AgentField aF : agents) {
			aF.setAgentZero(agentZero);
		}

	}
	*/

	private static void restartBetweenAlgo(Solution sol, String protocol) {
		addToSolutionString(sol, protocol);
		restartAgent();
		agentZero.emptyMessageBox();

	}

	private static void restartAgent() {
		for (int i = 0; i < agents.length; i++) {
			agents[i].changeValOfAllNeighbor();
			agents[i].setFirstValueToValue();
		}

	}



	private static AgentField[] initAgentsFieldArray() {
		AgentField[] ans = new AgentField[A];
		for (int i = 0; i < ans.length; i++) {
			ans[i] = new AgentField(D, i);
		}
		return ans;
	}

	public static int getRandomInt(Random r, int min, int max) {

		return r.nextInt(max - min + 1) + min;
	}

}
