import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {
	// -- dcop entities 
	static int iterations=50; 
	static int A=50; 
	static int D=10; 
	// -- shape different problems
	static double[] p1s= {0.2}; // prob for agents to be neighbors
	static double[] p2s= {1}; // prob of domain selection to have a cost
	static int costMax=100; // the max value of cost
	// -- communication protocol
	static double[] p3s= {0,0.5,1}; // prob of communiction to have delay
	static int[] delayUpperBounds= {5,10};//{5,10,25,50,100};
	// -- general
	static List<String> solutions=new ArrayList<String>();
	static Random r;
	static int meanReps=2; // number of reps for every solve process
	// -- characters
	static AgentField[] agents;
	static AgentZero agentZero;

	public static void main(String[] args) {
		initVariables();
		runExperiment();
		printDcops();
	}

	private static void printDcops() {
		BufferedWriter out = null;
		try {
			FileWriter s = new FileWriter("dcops.csv");
			out = new BufferedWriter(s);
			String header = "algo,p1,p2,mean_run,p3,upper_bound,itiration,real_cost";
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

		for (Double p1 : p1s) {
			for (Double p2 : p2s) {
				for (int i = 0; i < meanReps; i++) {
					Dcop dcop = createDcop(p1,p2);

					for (Double p3 : p3s) {
						for (Integer dUpperBound : delayUpperBounds) {
							agentZero.changeCommunicationProtocol(p3,dUpperBound);
							String protocolString = p3+","+	dUpperBound;
							
							
							long start = System.currentTimeMillis( );
							Solution dsa3 = new DSA(dcop, agents, agentZero, i, 0.3);
							restartBetweenAlgo(dsa3,protocolString);
							long finish = System.currentTimeMillis( );
							System.out.println(protocolString+","+dsa3);
							System.out.println("time: "+((finish-start)*0.001));

							Solution dsa6 = new DSA(dcop, agents, agentZero, i, 0.6);
							
							restartBetweenAlgo(dsa6,protocolString);
							System.out.println(protocolString+","+dsa6);
							System.out.println("time: "+((finish-start)*0.001));
							
							Solution dsa9 = new DSA(dcop, agents, agentZero, i, 0.9);
							
							restartBetweenAlgo(dsa9,protocolString);
							System.out.println(protocolString+","+dsa9);
							System.out.println("time: "+((finish-start)*0.001));
						}
						
					}
					
					
				}
			}
		}
		/*
		while (p1 <= p1Max) {
			
			
			
			
			
			while (p2 <= p2Max) {
				for (int i = 0; i < meanReps; i++) {
					if (i!=0) {
						p3 =p3Init;
					}
					Dcop dcop = createDcop();

					while (p3 <= p3Max) {

						agentZero.changeCommunicationProtocol(p3);

						long start = System.currentTimeMillis( );
						Solution dsa3 = new DSA(dcop, agents, agentZero, i, 0.3);
						restartBetweenAlgo(dsa3,p3);
						long finish = System.currentTimeMillis( );
						System.out.println(dsa3+","+p3);
						System.out.println("time: "+((finish-start)*0.001));

						Solution dsa6 = new DSA(dcop, agents, agentZero, i, 0.6);
						
						restartBetweenAlgo(dsa6,p3);
						System.out.println(dsa6+","+p3);
						System.out.println("time: "+((finish-start)*0.001));
						
						Solution dsa9 = new DSA(dcop, agents, agentZero, i, 0.9);
						
						restartBetweenAlgo(dsa9,p3);
						System.out.println(dsa9+","+p3);
						System.out.println("time: "+((finish-start)*0.001));
						


						p3 += p3Gap;
						//dcop = createDcsp(dcop);
						//dcop.changeCommunicationProtocol(p3);
					}
				}
				p2 += p2Gap;
			}
			p1 += p1Gap; 
		}
		*/
		//return dcops;
	}

	private static void addToSolutionString(Solution sol, String protocol) {
		for (int i = 0; i < iterations; i++) {
			String o = new String(sol.toString()+","+protocol+","+i+","+sol.getRealCost().get(i));
			solutions.add(o);
		}
		
	}
/*
	private static Dcop createDcsp(Dcop dcsp) {
		agents = initAgentsFieldArray();
		Dcop dcsp1 = new Dcop(dcsp, p3);
		agentZero = new AgentZero(itirationGap, dcsp.getNeighbors());
		agentFieldMeetAgentZero();
		return dcsp1;
	}
	*/
	

	private static Dcop createDcop(Double p1, Double p2) {
		agents = initAgentsFieldArray();
		Dcop dcop = new Dcop(agents, D, p1, p2,  iterations);
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
			agents[i].initValForAllNeighbors();
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
		
		return r.nextInt(max - min + 1) + min;
	}

	private static void initVariables() {

//		
		// create random object
	    r = new Random();      
	    r.setSeed(1);
	    


	}

}
