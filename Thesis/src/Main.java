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
	static int itirationGap; // the gap of message delay
	static int costMax; // the max value of cost

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
		printTableHeader1();
		
		
		
	
		
		
		
		while (p1 <= p1Max) {
			while (p2 <= p2Max) {								 
				for (int i = 0; i < meanReps; i++) {
					Dcsp dcsp = createDcsp();
					
					while (p3 <= p3Max) {

						Dcop dsa = new DSA(dcsp, agents, agentZero, 0.7);
						restartBetweenAlgo(dcsp);
						
						
						
						p3 += p3Gap;
						dcsp.changeCommunicationProtocol(p3);

					}
				}
				p2 += p2Gap;
			}
			p1 += p1Gap;
		}
	}

	private static Dcsp createDcsp() {
		agents = initAgentsFieldArray();
		Dcsp dcsp = new Dcsp(agents, D, p1, p2, p3);
		agentZero = new AgentZero(itirationGap, dcsp.getNeighbors());
		agentFieldMeetAgentZero();
		return dcsp;// create dcsp problem given p1 and p2
					// and fix neighbors return null;
	}

	private static void agentFieldMeetAgentZero() {
		for (AgentField aF : agents) {
			aF.setAgentZero(agentZero);
		}

	}

	private static void restartBetweenAlgo(Dcsp dcsp) {
		restartAgentRandomValue();
		restartAgentNeigborValue();
		agentZero.emptyMessageBox();

	}

	private static void restartAgentNeigborValue() {
		for (int i = 0; i < agents.length; i++) {
			agents[i].changeValOfAllNeighbor(-1);
		}

	}

	private static void restartAgentRandomValue() {
		for (int i = 0; i < agents.length; i++) {
			agents[i].setFirstValueToValue();
		}

	}

	private static ArrayList<Integer> solveDSA() {

		return null;
	}

	private static void initVariables() {
		// -- variables of dcop problem
		iterations = 1000; // 1000 number of iterations
		A = 4; // 30 number of agents
		D = 2; // 10 size of domain for each agent
		p1 = 0.5; // 0.2 chance for agents to be neighbors
		p2 = 1.0; // 0.1 chance of domain selection to have a cost
		p3 = 0.0; // 0 prob of communiction to have delay
		itirationGap = 2;// 2

		costMax = 10; // 10 the max value of cost

		// -- variables for loops
		meanReps = 10; // 10 number of reps for every solve process
		p1Max = 0.5; // 0.5 limits the p1 loop include
		p1Gap = 0.3; // 0.3 change in p1 in each itiration
		p2Max = 1; // 1 limits the p2 loop include
		p2Gap = 0.1; // 0.1 change in p2 in each itiration
		p3Max = 1.0; // 0.9 limits the p2 loop include
		p3Gap = 0.2; // 0.2
	}

	private static AgentField[] initAgentsFieldArray() {
		AgentField[] ans = new AgentField[A];
		for (int i = 0; i < ans.length; i++) {
			ans[i] = new AgentField(D, i);
		}
		return ans;
	}

	private static void printTableHeader1() {
		System.out.println("Alg" + "\t" + "p1" + "\t" + "p2" + "\t" + "quality");
	}

	public static int getRandomInt(int min, int max) {
		Random rn = new Random();
		return rn.nextInt(max - min + 1) + min;
	}

}

/*

		BufferedWriter out = null;
		try {
			FileWriter s = new FileWriter("time.csv");
			out = new BufferedWriter(s);
			out.newLine();
			for (int i = 0; i < x.length; i++) {
				String o=""+x[i][0]+","+(x[i][1]/50);
				out.write(o);
				out.newLine();
			}
			
		
			out.close();
		}catch (Exception e) {
			System.err.println("Couldn't open the file");
		}

*/
