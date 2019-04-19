import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {

	// -- variables of dcop problem
	static int A = 30; // 50 number of agents
	static int D = 10; // 10 size of domain for each agent
	static double[] p1s = { 0.5 }; // 0.2 prob for agents to be neighbors
	static double[] p2s = { 1 }; // 1 prob of domain selection to have a cost
	static int costMax = 20; // 100 the max value of cost

	// -- communication protocol
	static double[] p3s = {0.5}; // prob of communiction to have delay
	static int[] delayUBs = { 5, 10 };
	static double[] p4s = { 0, 0.5, 1 }; // prob of communiction to have delay

	// -- Experiment time
	static int meanReps = 5; // number of reps for every solve process
	static int iterations = 200;

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
			FileWriter s = new FileWriter("dcops.csv");
			out = new BufferedWriter(s);
			String header = "p3,ub,p4,algo,p1,p2,mean_run,itiration,real_cost";
			out.write(header);
			out.newLine();

			for (String o : solutions) {
				out.write(o);
				out.newLine();
			}

			/*
			 * for (Solution dcop : dcops) {
			 * 
			 * for (int i = 0; i < realCosts.size(); i++) { String o =
			 * dcop.toString()+","+i+","+realCosts.get(i); out.write(o); out.newLine(); } }
			 */

			out.close();
		} catch (Exception e) {
			System.err.println("Couldn't open the file");
		}

	}

	private static void runExperiment() {

		for (Double p1 : p1s) {
			for (Double p2 : p2s) {
				Dcop dcop = createDcop(p1, p2);

				for (int i = 0; i < meanReps; i++) {
					for (Double p3 : p3s) {
						for (Integer delayUB : delayUBs) {
							for (Double p4 : p4s) {
								agentZero.changeCommunicationProtocol(p3, delayUB, p4);
								String protocol = p3 + "," + delayUB + "," + p4;
								// long start = System.currentTimeMillis();
								// Solution dsa3 = new DSA(dcop, agents, agentZero, i, 0.3);
								// restartBetweenAlgo(dsa3,p3);
								// long finish = System.currentTimeMillis();
								// System.out.println(dsa3+","+p3);
								// System.out.println("time: "+((finish-start)*0.001));
								// start = System.currentTimeMillis();
								Solution dsa6 = new DSA(dcop, agents, agentZero, i, 0.6);
								restartBetweenAlgo(dsa6, protocol);
								System.out.println(protocol + "," + dsa6);
								// finish = System.currentTimeMillis();
								// System.out.println("time: " + ((finish - start) * 0.001));

								// Solution dsa9 = new DSA(dcop, agents, agentZero, i, 0.9);

								// restartBetweenAlgo(dsa9,p3);
								// System.out.println(dsa9+","+p3);
								// System.out.println("time: "+((finish-start)*0.001));

								if (p3 == 0) {
									break;
								}
							}//p4
							if (p3 == 0) {
								break;
							}

						}//ub
					}//p3
				}//means run
			}//p2
		}//p1

	}

	private static void addToSolutionString(Solution sol, String protocol) {
		for (int i = 0; i < iterations; i++) {
			String o = new String(protocol + "," + sol.toString() + "," + i + "," + sol.getRealCost().get(i));
			solutions.add(o);
		}

	}
	/*
	 * private static Dcop createDcsp(Dcop dcsp) { agents = initAgentsFieldArray();
	 * Dcop dcsp1 = new Dcop(dcsp, p3); agentZero = new AgentZero(itirationGap,
	 * dcsp.getNeighbors()); agentFieldMeetAgentZero(); return dcsp1; }
	 */

	private static Dcop createDcop(double p1, double p2) {
		agents = initAgentsFieldArray();
		Dcop dcop = new Dcop(agents, D, p1, p2, iterations);
		agentZero = new AgentZero(iterations, dcop.getNeighbors());
		agentFieldMeetAgentZero();
		return dcop;// create dcsp problem given p1 and p2
					// and fix neighbors return null;
	}

	private static void agentFieldMeetAgentZero() {
		for (AgentField aF : agents) {
			aF.setAgentZero(agentZero);
		}

	}

	private static void restartBetweenAlgo(Solution sol, String protocol) {
		addToSolutionString(sol, protocol);
		restartAgent();
		agentZero.emptyMessageBox();

	}

	private static void restartAgent() {
		for (int i = 0; i < agents.length; i++) {
			agents[i].changeValOfAllNeighbor(-1);
			agents[i].setFirstValueToValue();
		}

	}

	private static ArrayList<Integer> solveDSA() {

		return null;
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
