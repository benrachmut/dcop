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
	static int iterations; // number of iterations
	static int A; // number of agents
	static int D; // size of domain for each agent
	static double p1; // prob for agents to be neighbors
	static double p2; // prob of domain selection to have a cost
	static double p3; // prob of communiction to have delay
	static double p3Init;

	static int itirationGap; // the gap of message delay
	static int costMax; // the max value of cost
	static List<String> solutions;


	// -- variables for loops
	// static double addToP2EachIteration;
	static int meanReps; // number of reps for every solve process
	static double p1Max; // limits the p1 loop include
	static double p1Gap; // change in p1 in each itiration
	static double p2Max; // limits the p2 loop include
	static double p2Gap; // change in p2 in each itiration
	static double p3Max; // limits the p2 loop include
	static double p3Gap; // change in p2 in each itiration
	static AgentField[] agents;
	static AgentZero agentZero;

	public static void main(String[] args) {
		initVariables();
		//List<Solution> dcops = 
		runExperiment();
		printDcops();
	}

	private static void printDcops() {
		BufferedWriter out = null;
		try {
			FileWriter s = new FileWriter("dcops.csv");
			out = new BufferedWriter(s);
			String header = "algo"+","+"p1"+","+"p2"+","+"p3"+","+"gap"+","+"mean_run"+","+"itiration"+","+"real_cost";
			out.write(header);
			out.newLine();
			
			for (String o  : solutions) {
				out.write(o);
				out.newLine();
			}
			
			/*
			for (Solution dcop : dcops) {
		
				for (int i = 0; i < realCosts.size(); i++) {
					String o = dcop.toString()+","+i+","+realCosts.get(i);
					out.write(o);
					out.newLine();
				}	
			}
			*/

			out.close();
		} catch (Exception e) {
			System.err.println("Couldn't open the file");
		}

	}



	private static void runExperiment() {

		while (p1 <= p1Max) {
			
			
			
			
			
			while (p2 <= p2Max) {
				for (int i = 0; i < meanReps; i++) {
					if (i!=0) {
						p3 =p3Init;
					}
					Dcop dcop = createDcop();

					while (p3 <= p3Max) {

						
						long start = System.currentTimeMillis( );
						Solution dsa7 = new DSA(dcop, agents, agentZero, i, 0.7);
						restartBetweenAlgo(dcop,dsa7);
						long finish = System.currentTimeMillis( );
						System.out.println(dsa7);
						System.out.println("time: "+((finish-start)*0.001));

						Solution dsa3 = new DSA(dcop, agents, agentZero, i, 0.3);
						
						restartBetweenAlgo(dcop, dsa3);
						System.out.println(dsa3);
						System.out.println("time: "+((finish-start)*0.001));


						p3 += p3Gap;
						//dcop = createDcsp(dcop);

						dcop.changeCommunicationProtocol(p3);
					}
				}
				p2 += p2Gap;
			}
			p1 += p1Gap; 
		}
		//return dcops;
	}

	private static void addToSolutionString(Solution sol) {
		for (int i = 0; i < iterations; i++) {
			String o = new String(sol.toString()+","+i+","+sol.getRealCost().get(i));
			solutions.add(o);
		}
		
	}

	private static Dcop createDcsp(Dcop dcsp) {
		agents = initAgentsFieldArray();
		Dcop dcsp1 = new Dcop(dcsp, p3);
		agentZero = new AgentZero(itirationGap, dcsp.getNeighbors());
		agentFieldMeetAgentZero();
		return dcsp1;
	}
	

	private static Dcop createDcop() {
		agents = initAgentsFieldArray();
		Dcop dcop = new Dcop(agents, D, p1, p2, p3);
		agentZero = new AgentZero(itirationGap, dcop.getNeighbors());
		agentFieldMeetAgentZero();
		return dcop;// create dcsp problem given p1 and p2
					// and fix neighbors return null;
	}

	private static void agentFieldMeetAgentZero() {
		for (AgentField aF : agents) {
			aF.setAgentZero(agentZero);
		}

	}

	private static void restartBetweenAlgo(Dcop dcsp, Solution sol) {
		addToSolutionString(sol);
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

	public static int getRandomInt(int min, int max) {
		Random rn = new Random();
		return rn.nextInt(max - min + 1) + min;
	}

	private static void initVariables() {
		// -- variables of dcop problem
		iterations = 1000; // 1000 number of iterations
		A = 30; // 30 number of agents
		D = 10; // 10 size of domain for each agent
		p1 = 0.5; // 0.2 chance for agents to be neighbors
		p2 = 1.0; // 0.1 chance of domain selection to have a cost
		p3Init = 0; // 0 prob of communiction to have delay
		p3=p3Init;
		itirationGap = 3;// 2
		solutions = new ArrayList<String>();
		costMax = 20; // 10 the max value of cost

		// -- variables for loops
		meanReps = 10; // 10 number of reps for every solve process
		p1Max = 1.0; // 0.5 limits the p1 loop include
		p1Gap = 0.3; // 0.3 change in p1 in each itiration
		p2Max = 1.0; // 1 limits the p2 loop include
		p2Gap = 0.1; // 0.1 change in p2 in each itiration
		p3Max = 1.01; 
		p3Gap = 0.25; // 0.2
	}

}
