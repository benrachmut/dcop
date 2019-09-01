import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Main {

	// -- variables of dcop problem
	static int A = 50;//50;// 50; // 50 number of agents
	static int D = 10; // 10 size of domain for each agent
	static int costMax = 100; // 100 the max value of cost

	
	// -- Experiment time
	static int meanReps = 30; // number of reps for every solve process
	static int iterations = 3000;//300;//700;//4000;
	static Dcop dcop;
	
	
	// versions
	static String algo = "dsaUnsynch7"; // "dsaUnsynch7";//"unsynchMono";//"mgmUb";//"unsynch0";
	static int[] dcopVersions = {1}; // 1= Uniformly random DCOPs, 2= Graph coloring problems, 3= Scale-free
										// network problems.//
	static int dcopVersion;

	// -- memory
	static int[] memoryVersions = { 2 }; // 1=exp, 2= constant, 3= reasonable
	static int memoryVersion;
	static double[] constantsPower = {3,4};
	static long memoryMaxConstant;
	static double[] similarRatios = { 0.5, 0.8, 0.9, 0.95, 1 };
	static double memorySimilartyRatio; // given memory version = 3

	// -- synch
	static boolean synch = false;
	static boolean anytimeDfs = true;
	static boolean anytimeBfs = false;
	static String date = "AAAI2020_agents_"+A+"Dcop_v"+dcopVersions[0]+"tryDfs!!!"; //"memoryMaxConstantTrail_bfs_changeComparator"; //"AAAI2020_agents_"+A+"Dcop_v"+dcopVersions[0]+"tryDfs_comparatorDel";//"memoryMaxConstantTrail_dfs_changeComparator";//"mgm_synch_perfect_comm_v"+dcopVersions[0];//"AAAI2020_agents_"+A+"Dcop_v"+dcopVersions[0]+"tryDfs,WithMemoryLimit";//"dsa_synch_perfect_comm_v"+dcopVersions[0];//"AAAI2020_agents_"+A+"Dcop_v"+dcopVersions[0]+"tryDfs,WithMemoryLimit";// "memoryMaxConstantTrail";
	// debug

	// static boolean debug = false;
	// static boolean debugCombineWith = true;
	static boolean printCompletePermutationOfTop = true;
	static boolean printCentralPOVPermutations = false;
	static boolean printSelfN = false;
	static boolean foundPermutationDebug = false;

	// different versions
	public static boolean tryAllMailBox = false;
	public static boolean trySendValueAsPermutation = true;
	// public static boolean tryAllMailBoxImproved = false;
	// public static boolean tryAgentRememberSequence = true;
	public static boolean trySendSelfCounter = false;

	// -- uniformly random dcop
	static double[] p1sUniform = { 0.1 }; //{0.2};//{ 0.1,0.6 }; //{0.2};//
	static double[] p2sUniform = {0.7};//{1};//{ 0.7 }; 
	static Double currentP1Uniform;
	static Double currentP2Uniform;
	static Random rP1Uniform = new Random();
	static Random rP2Uniform = new Random();

	// -- color dcop
	static double[] p1sColor = {0.1, 0.6};//{ 0.1,0.6 };
	static Double currentP1Color;
	static Random rP1Color = new Random();
	static int x;

	// -- scale free AB
	static int[] hubs = { 10 };
	static int[] numOfNToNotHubs = { 3 };
	static double[] p2sScaleFree= {1}; 
	static double currentP2ScaleFree;
	static int hub;
	static int numOfNToNotHub;
	static Random rHub = new Random();
	static Random rNotHub = new Random();
	static Random rP2ScaleFree = new Random();
	

	// -- communication protocol
	static double[] p3s = {0,1};//{ 0,1 }; 
	static boolean[] dateKnowns = { true };
	static int[] delayUBs = {5,10,25};//{5,10,25};//{5};//
	static double[] p4s = { 0 };
	
	

	static Random rP3 = new Random();
	static Random rP4 = new Random();
	static Random rDelay = new Random();
	static boolean dateKnown;
	static Double currentP3;
	static Double currentP4;
	static int currentUb;



	// -- characters
	static AgentField[] agents;
	static AgentZero agentZero;

	// -- other
	static List<String> solutions = new ArrayList<String>();
	static List<String> fatherSolutions = new ArrayList<String>();

	// -- random variables
	
	static Random rFirstValue = new Random();
	static Random rCost = new Random();
	static Random rDsa = new Random();




	public static void main(String[] args) {
		System.out.println(date);
		for (int i : dcopVersions) {
			dcopVersion = i;

			if (algo == "dsaUnsynch7") {

				for (int j : memoryVersions) {
					memoryVersion = j;
					runDifferentMemoryVersions();
				}
			} else {
				runDifferentDcop();
			}
			printDcops();

		}
	}

	private static void runDifferentMemoryVersions() {
		if (memoryVersion == 1) {
			runDifferentDcop();
		}

		if (memoryVersion == 2) {
			for (double i : constantsPower) { // for parameter tuning
				memoryMaxConstant = (long) Math.pow(10, i);
				runDifferentDcop();
			}
		}
		if (memoryVersion == 3) {
			for (double i : similarRatios) { // for parameter tuning
				memorySimilartyRatio = i;
				runDifferentDcop();
			}
		}

	}

	private static void runDifferentDcop() {
		if (dcopVersion == 1) {
			D = 10;
			costMax = 100;
			runUniformlyRandomDcop();
		}
		if (dcopVersion == 2) {
			D = 3;
			costMax = 10;
			runColorDcop();
		}
		if (dcopVersion == 3) {
			D = 10;
			costMax = 100;
			runScaleFreeDcop();
		}

	}

	private static void runScaleFreeDcop() {
		
	
		for (int i : hubs) {
			hub = i;
			for (int j : numOfNToNotHubs) {
				numOfNToNotHub = j;
				for (double k : p2sScaleFree) {
					currentP2ScaleFree = k;
					
					for (int meanRun = 0; meanRun < meanReps; meanRun++) {
						dcopSeeds(meanRun);
						dcop = createDcop();
						differentCommunicationProtocols(dcop, meanRun);

					} // means run
				}
			}
		}
	}
	/*
	 * private static void setSynchBoolean() { boolean unsynchMono =
	 * algo.equals("unsynchMono");
	 * 
	 * if (unsynchMono) { synch = false; } else { synch = true; }
	 * 
	 * }
	 */

	private static void printDcops() {
		BufferedWriter out = null;
		try {
			FileWriter s = new FileWriter(algo + date + ".csv");
			out = new BufferedWriter(s);
			String header = "dcop,p3,date_known,ub,p4,algo,p1,p2,mean_run,iteration,real_cost,";
			if (!synch) {
				header = header + "anytime_cost,top_cost,memory_style,hyper_parametr";
			}
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

	private static void runColorDcop() {

		for (Double p1 : p1sColor) {
			currentP1Color = p1;

			for (int meanRun = 0; meanRun < meanReps; meanRun++) {
				// only here change the tree
				dcopSeeds(meanRun);
				dcop = createDcop();
				differentCommunicationProtocols(dcop, meanRun);

			} // means run
		} // p1

	}

	private static void runUniformlyRandomDcop() {

		for (Double p1 : p1sUniform) {
			currentP1Uniform = p1;
			for (Double p2 : p2sUniform) {
				currentP2Uniform = p2;

				for (int meanRun = 0; meanRun < meanReps; meanRun++) {
					dcopSeeds(meanRun);
					dcop = createDcop();
					differentCommunicationProtocols(dcop, meanRun);

				} // means run
			} // p2
		} // p1

	}

	private static void dcopSeeds(int meanRun) {
		rP1Uniform.setSeed(meanRun);
		rP2Uniform.setSeed(meanRun);
		rP1Color.setSeed(meanRun);
		rP2ScaleFree.setSeed(meanRun);
		rHub.setSeed(meanRun);
		rNotHub.setSeed(meanRun);
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
		/*
		for (Integer i: algo.getRealCosts()){
			System.out.println(i);
		}
		*/
		
		System.out.println(dcop+","+protocol + "," + algo);
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
		rDsa.setSeed(input);

	}

	private static Solution selectedAlgo(Dcop dcop, int meanRun) {
		Solution ans = null;

		boolean dsa7 = algo.equals("dsa7");
		boolean dsaUnsynch7 = algo.equals("dsaUnsynch7");
		boolean mgm = algo.equals("mgm");
		boolean mgmUb = algo.equals("mgmUb");
		boolean unsynchMono = algo.equals("unsynchMono");

		if (unsynchMono) {
			ans = new UnsynchMono(dcop, agents, agentZero, meanRun);
		}

		if (dsa7) {
			ans = new DSA(dcop, agents, agentZero, meanRun, 0.7);

		}
		if (dsaUnsynch7) {
			ans = new UnsynchDsa(dcop, agents, agentZero, meanRun, 0.7);
		}

		if (mgm) {
			ans = new MGM(dcop, agents, agentZero, meanRun);

		}
		if (mgmUb) {
			ans = new MGMub(dcop, agents, agentZero, meanRun);

		}

		ans.solve();
		/*
		 * if (Main.printCompletePermutationOf9) {
		 * 
		 * Set<Permutation> perms = ans.agents[9].getPermutationsToSend(); for
		 * (Permutation p : perms) { int realCost =
		 * Solution.dcopS.calRealSolForDebug(p.getM()); if (p.getCost() == realCost) {
		 * System.out.println(p); } else { System.err.println("cost should be: " +
		 * realCost + " |" + p);
		 * 
		 * } } }
		 */
		return ans;

	}

	private static int countTrue(Collection<Boolean> values) {
		int ans = 0;
		for (Boolean b : values) {
			if (b) {
				ans++;
			}
		}
		return ans;
	}

	private static void addToSolutionString(Solution sol, String protocol) {
		for (int i = 0; i < iterations; i++) {
			String s = dcop.toString() + "," + protocol + "," + sol.toString() + "," + i + "," + sol.getRealCost(i)+ "," ;
			
			if (!synch) {

				s = s + new String(sol.getAnytimeCost(i) + "," + sol.getTopCost(i) + "," + memoryVersion) + ",";
				if (memoryVersion == 1) {
					s = s + 0;
				}
				if (memoryVersion == 2) {
					s = s + memoryMaxConstant;
				}
				if (memoryVersion == 3) {
					s = s + memorySimilartyRatio;
				}

			}
			solutions.add(s);
		}
	}

	private static Dcop createDcop() {
		agents = initAgentsFieldArray();
		Dcop dcop = new Dcop(agents, D, iterations);
		for (AgentField a : agents) {
			a.restartNeighborCounter();
		}
		agentZero = new AgentZero(iterations, dcop.getNeighbors(), agents);

		if (algo.equals("unsynchMono")) {
			Tree psaduoTree = new Tree(agents);
			psaduoTree.dfs();
			psaduoTree.setIsAboveBelow();

			for (AgentField a : agents) {
				a.setAnytimeFather(a.getDfsFather());
				a.setAnytimeSons(a.getDfsSons());
			}

		}

		else if (anytimeBfs) {
			Tree bfs = new Tree(agents);
			bfs.bfs();
		}
		else if (anytimeDfs) {
			Tree psaduoTree = new Tree(agents);
			psaduoTree.dfs();
			//psaduoTree.setIsAboveBelow();

			for (AgentField a : agents) {
				a.setAnytimeFather(a.getDfsFather());
				a.setAnytimeSons(a.getDfsSons());
			}
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
			// agents[i].resetNumOfInterationForChange();
			agents[i].setAllBelowMap(0);
			agents[i].setAllAboveMap(0);
			agents[i].resetMsgUpAndDown();
			agents[i].setDecisionCounterNonMonotonic(0);
			agents[i].setDecisionCounterMonotonic(0);
			agents[i].initSonsAnytimeMessages();
			agents[i].resetCounterAndValue();
			agents[i].resetBestPermutation();
			agents[i].resettopHasAnytimeNews();
			agents[i].addFirstCoupleToCounterAndVal();
			agents[i].setUnsynchFlag(false);
			agents[i].restartNeighborCounter();
			agents[i].restartAnytimeUpRecieved();
			agents[i].restartPermutationsPast();
			agents[i].restartAnytimeToSend();
			agents[i].restartAnytimeValue();
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
