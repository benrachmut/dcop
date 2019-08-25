import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BoundedQueue<T> {
	private int maxSize;
	private List<T> q;
	private Comparator<T>c;

	public BoundedQueue(Comparator<T>c) {
		this.maxSize = Main.memoryConstant;
		this.q = new ArrayList<T>();
		this.c= c;
	}
	
	public void insert(T t) {
		if (q.size()>maxSize) {
			T tMax = Collections.max(q,c);
			q.remove(tMax);
		}
		q.add(t);
	}
	


}
