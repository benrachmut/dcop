import java.util.Comparator;

public class ComparatorMsgDate implements Comparator<MessageNormal> {

	@Override
	public int compare(MessageNormal m1, MessageNormal m2) {
		if (m1.getDate() < m2.getDate()) {
			return 1;
		}
		if (m1.getDate() > m2.getDate()) {
			return -1;
		}
		return 0;
	}

}
