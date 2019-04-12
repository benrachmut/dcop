
public class Neighbors {
	private Agent a1;
	private Agent a2;
	private boolean delay12;
	private boolean delay21;

	public Neighbors(Agent a1, Agent a2) {
		super();
		this.a1 = a1;
		this.a2 = a2;
		this.delay12 = false;
		this.delay21 = false;
	}

@Override
public boolean equals(Object obj) {
	/*
	if (!(obj instanceof Neighbors)) {
		return false;
	}
	*/
	
	Neighbors n = (Neighbors)obj;
	Agent na1 = n.getA1();
	Agent na2 = n.getA2();
	
	return na1.getId()==a1.getId() && na2.getId()==a2.getId();

	//return false;
}
	
	public boolean isDelay12() {
		return delay12;
	}


	public void setDelay12(boolean delay12) {
		this.delay12 = delay12;
	}


	public boolean isDelay21() {
		return delay21;
	}


	public void setDelay21(boolean delay21) {
		this.delay21 = delay21;
	}


	public Agent getA1() {
		return a1;
	}

	public Agent getA2() {
		return a2;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		return "{"+this.a1+","+this.a2+"}";
		//return "{A"+this.a1.getId()+",A"+this.a2.getId()+"}";
	}

}
