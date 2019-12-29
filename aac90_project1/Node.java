// DLB Node class

// Key = S
// Value = T

public class Node<S, T> {
	private T val;	// Arbitrary parameter for value
	private S key;	// Arbitrary parameter for key
	private Node<S, T> child;	// Reference to a new node for the child
	private Node<S, T> right;	// Reference to a new node for the right

	public Node() {
		val = null;
		key = null;
		child = null;
		right = null;
	}

	// Getters
	public T getVal() {
		return val;
	}

	public S getKey() {
		return key;
	}

	public Node<S, T> getChild() {
		return child;
	}

	public Node<S, T> getRight() {
		return right;
	}

	// Setters
	public void setVal(T tVal) {
		val = tVal;
	}

	public void setKey(S sKey) {
		key = sKey;
	}

	public void setChild(Node<S, T> n) {
		child = n;
	}

	public void setRight(Node<S, T> n) {
		right = n;
	}

	// Extra boolean methods
	public boolean hasKey() {
		if (key == null) {
			return false;
		}
		else {
			return true;
		}
	}

	public boolean hasVal() {
		if (val == null) {
			return false;
		}
		else {
			return true;
		}	
	}

	public boolean hasChild() {
		if (child == null) {
			return false;
		}
		else {
			return true;
		}
	}

	public boolean hasRight() {
		if (right == null) {
			return false;
		}
		else {
			return true;
		}
	}
}