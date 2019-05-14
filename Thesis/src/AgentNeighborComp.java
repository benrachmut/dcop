import java.util.Comparator;

public class AgentNeighborComp implements Comparator<AgentField> {

	@Override
	public int compare(AgentField a1, AgentField a2) {
		return a1.getNieghborSize()-a2.getNieghborSize();
	}

}
