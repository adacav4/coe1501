public class Node{
	private Node child;
	private Node sibling;
	private char value;
	private CarPriorityQueue pq;

	// Constructor
	public Node(char val){
		value = val;
	}

	// Gets value of node
	public char getVal(){
		return value;
	}

	// Gets child of node
	public Node getChild(){
		return child;
	}

	// Gets sibling of node
	public Node getSibling(){
		return sibling;
	}

	// Gets Priority Queue stored in node
	public CarPriorityQueue getPQ(){
		return pq;
	}

	// Sets the value of the node
	public void setVal(char val){
		value = val;
	}

	// Sets the child of the node
	public void setChild(Node c){
		child = c;
	}

	// Sets the sibling of the node
	public void setSibling(Node s){
		sibling = s;
	}

	// Sets the Priority Queue of the node
	public void setPQ(CarPriorityQueue p){
		pq = p;
	}
}