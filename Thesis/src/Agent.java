import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Agent {
	protected int id;
	protected int value;
	
	public Agent(int id) {
		super();
		this.id = id;
	} 
	
	public Agent(int id, int value) {
		this(id);
		this.value = value;
	} 
	
	public Agent(Agent a) {
		this.id = a.getId();
		this.value = a.getValue();
	} 
	
	public int getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	public int getValue() {
		// TODO Auto-generated method stub
		return this.value;
	}
	@Override
	public boolean equals(Object o) {
		Agent a = (Agent)o;
		return a.getId()==this.id && a.getValue()==this.value;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "A"+this.id+"="+this.value;
	}
	
}
