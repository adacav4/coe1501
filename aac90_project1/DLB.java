// Using Node.java as the node for the DLB class

public class DLB<S, T> {
	private Node<S, T> root;	// Declaring a root node for every new DLB constructed

	public DLB(S k, T v) {
		root = new Node<S, T>(); // Setting the root node to a new instance of a DLB node
		root.setKey(k);
		root.setVal(v);
	}

	// Returns the root node of the trie
	public Node<S, T> getRoot() {
		return root;
	}

	// Returns true if the trie is empty and false if it is not empty
	public boolean isEmpty() {
		if (!(getRoot().hasKey())) {
			return true;
		}
		else {
			return false;
		}
	}
}