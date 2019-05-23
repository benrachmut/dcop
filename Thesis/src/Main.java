import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {

	// versions
	static String algo = "unsynchMono";// "unsynchMono";//"mgmUb";
	static String date = "2505";
	static boolean synch = true;
	static boolean dateKnown;

	// -- variables of dcop problem
	static int A = 25;// 50; // 50 number of agents
	static int D = 10; // 10 size of domain for each agent
	static double[] p1s = { 0.2 }; // 0.2 prob for agents to be neighbors
	static double[] p2s = { 1 }; // 1 prob of domain selection to have a cost
	static int costMax = 100; // 100 the max value of cost

	// -- communication protocol
	static double[] p3s =  { 0,0.5,1 }; // prob of communication to have delay
	static boolean[] dateKnowns = { true };// { true, false };
	static int[] delayUBs = { 5, 10, 25, 50 };// {0};//{ 5, 10, 25, 50 };// { 5, 10, 20, 40 };//{ 3, 5, 10, 25}; // { 5,
												// 10, 25, 50, 100 };
	static double[] p4s = { 0 };// {0, 0.2, 0.6, 0.9};//{ 0, 0.2, 0.5, 0.8, 0.9 }; // prob of communication to
								// have delay

	// -- Experiment time
	static int meanReps = 10;// 10; // number of reps for every solve process
	static int iterations = 2500;// 1000;

	// -- characters
	static AgentField[] agents;
	static AgentZero agentZero;

	// -- other
	static List<String> solutions = new ArrayList<String>();;

	static Random rP1 = new Random();
	static Random rP2 = new Random();
	static Random rFirstValue = new Random();
	static Random rCost = new Random();

	static Random rP3 = new Random();
	static Random rP4 = new Random();
	static Random rDelay = new Random();

	static Random rDsa = new Random();

	static Double currentP3 = 0.0;
	static Double currentP4 = 0.0;
	static int currentUb = 0;
	static Double currentP1 = 0.0;
	static Double currentP2 = 0.0;

	public static void main(String[] args) {
		// initVariables();
		setSynchBoolean();
		runExperiment();
		printDcops();
	}

	private static void setSynchBoolean() {
		boolean unsynchMono = algo.equals("unsynchMono");

		if (unsynchMono) {
			synch = false;
		} else {
			synch = true;
		}

	}

	private static void printDcops() {
		BufferedWriter out = null;
		try {
			FileWriter s = new FileWriter(algo + date + ".csv");
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
			currentP1 = p1;
			for (Double p2 : p2s) {
				currentP2 = p2;

				for (int meanRun = 0; meanRun < meanReps; meanRun++) {

					// only here change the tree
					dcopSeeds(meanRun);
					Dcop dcop = createDcop();
					differentCommunicationProtocols(dcop, meanRun);

				} // means run
			} // p2
		} // p1

	}

	private static void dcopSeeds(int meanRun) {
		rP1.setSeed(meanRun);
		rP2.setSeed(meanRun);
		rFirstValue.setSeed(meanRun);
		rCost.setSeed(meanRun);

	}

	private static void differentCommunicationProtocols(Dcop dcop, int meanRun) {

		int communicationSeed = 0;

		for (Double p3 : p3s) {
			currentP3 = p3;
			if (p3 == 0) {
				afterHavingAllPrameters(p3, true, -1, -1.0, dcop, meanRun);
			} else {
				diffCommunicationGivenP3(communicationSeed, dcop, meanRun, p3);
			}
		} // p3
		printDcops();

	}

	private static void afterHavingAllPrameters(Double p3, Boolean dK, Integer delayUB, Double p4, Dcop dcop,
			int meanRun) {
		// ---- protocol ----
		// agentZero.changeCommunicationProtocol(p3, delayUB, p4);
		String protocol = p3 + "," + dK + "," + delayUB + "," + p4;
		// ---- find solution ----
		Solution algo = selectedAlgo(dcop, meanRun);
		System.out.println(protocol + "," + algo);
		// ---- restart ----
		restartBetweenAlgo(algo, protocol);

	}

	private static void diffCommunicationGivenP3(int communicationSeed, Dcop dcop, int meanRun, Double p3) {
		for (boolean dK : dateKnowns) {
			dateKnown = dK;
			for (Integer delayUB : delayUBs) {
				currentUb = delayUB;
				for (Double p4 : p4s) {
					communicationSeed = communicationSeed + 1;
					communicationSeeds(communicationSeed);
					currentP4 = p4;
					afterHavingAllPrameters(p3, dK, delayUB, p4, dcop, meanRun);

				} // p4
			} // ub
		} // date known

	}

	private static void communicationSeeds(int input) {
		rP3.setSeed(input);
		rP4.setSeed(input);
		rDelay.setSeed(input);

	}

	private static Solution selectedAlgo(Dcop dcop, int meanRun) {
		Solution ans = null;

		boolean dsa7 = algo.equals("dsa7");
		boolean mgm = algo.equals("mgm");
		boolean mgmUb = algo.equals("mgmUb");

		boolean unsynchMono = algo.equals("unsynchMono");

		if (unsynchMono) {
			ans = new UnsynchMono(dcop, agents, agentZero, meanRun);

		}
		if (dsa7) {
			ans = new DSA(dcop, agents, agentZero, meanRun, 0.7);

		}
		if (mgm) {
			ans = new MGM(dcop, agents, agentZero, meanRun);

		}
		if (mgmUb) {
			ans = new MGMub(dcop, agents, agentZero, meanRun);

		}
		ans.solve();

		return ans;
	}

	private static void addToSolutionString(Solution sol, String protocol) {
		for (int i = 0; i < iterations; i++) {
			String o = new String(protocol + "," + sol.toString() + "," + i + "," + sol.getRealCost().get(i));
			solutions.add(o);
		}

	}

	private static Dcop createDcop() {
		agents = initAgentsFieldArray();
		Dcop dcop = new Dcop(agents, D, iterations);
		agentZero = new AgentZero(iterations, dcop.getNeighbors(), agents);

		if (algo.equals("unsynchMono")) {
			Tree pT = new Tree(agents);
			pT.dfs();
			pT.setIsAboveBelow();
		}

		return dcop;
	}

	/*
	 * private static void agentFieldMeetAgentZero() { for (AgentField aF : agents)
	 * { aF.setAgentZero(agentZero); }
	 * 
	 * }
	 */

	private static void restartBetweenAlgo(Solution sol, String protocol) {
		addToSolutionString(sol, protocol);
		restartOther();

	}

	private static void restartOther() {
		restartAgent();
		agentZero.emptyMessageBox();
		agentZero.emptyRMessageBox();
		agentZero.emptyTimeStempBoxMessage();

	}

	private static void restartAgent() {
		for (int i = 0; i < agents.length; i++) {
			agents[i].changeValOfAllNeighbor();
			agents[i].changeValR();
			agents[i].setFirstValueToValue();
			// agents[i].setReciveAll(false);
			// agents[i].setTimeStemp(0);
			agents[i].resetNumOfInterationForChange();
			agents[i].setAllBelowMap(0);
			agents[i].setAllAboveMap(0);
			agents[i].setDecisionCounter(0);
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
