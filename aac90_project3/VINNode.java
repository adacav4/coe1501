public class VINNode{
	private VINNode child;
	private VINNode sibling;
	private char value;
	private Car car;

	// Constructor
	public VINNode(char val){
		value = val;
	}

	// Gets the value of the node
	public char getVal(){
		return value;
	}

	// Gets the child of the node
	public VINNode getChild(){
		return child;
	}

	// Gets the sibling of the node
	public VINNode getSibling(){
		return sibling;
	}

	// Gets the Car of the node
	public Car getCar(){
		return car;
	}

	// Sets the value of the node
	public void setVal(char val){
		value = val;
	}

	// Sets the child of the node
	public void setChild(VINNode c){
		child = c;
	}

	// Sets the sibling of the node
	public void setSibling(VINNode s){
		sibling = s;
	}

	// Sets the Car of the node
	public void setCar(Car newCar){
		car = newCar;
	}
}