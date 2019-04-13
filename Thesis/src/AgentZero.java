import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class AgentZero {
	private SortedSet<Message> messagesBox;
	private Set<Neighbors> neighbors;
	private int itirationGap;
	
	public AgentZero(int itirationGap, Set<Neighbors> neighbors) {
		this.messagesBox = new TreeSet<Message>();
		this.neighbors=neighbors;
		this.itirationGap = itirationGap;
	}

	public void emptyMessageBox() {
		this.messagesBox.clear();
	}

	public void createMsgs() {
		for (Neighbors n : this.neighbors) {
			AgentField a1 = (AgentField) n.getA1();
			AgentField a2 = (AgentField) n.getA2();
			
			

		}
		
	}


}
