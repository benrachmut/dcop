import java.util.Comparator;

public class ComparatorMsgDate implements Comparator<Message> {

	@Override
	public int compare(Message m1, Message m2) {
		if (m1.getDate() < m2.getDate()) {
			return 1;
		}
		if (m1.getDate() > m2.getDate()) {
			return -1;
		}
		return 0;
	}

}
