// DLB to store individual cars

import java.text.StringCharacterIterator;

public class CarDLB{
	private VINNode root;	// Initial VINNode as the root of the DLB

	// Insert a car VIN into the DLB
	public boolean insert(Car car){
		String vin = car.getVIN();	// Get the car VIN
		StringCharacterIterator i = new StringCharacterIterator(vin);	// Create a string iterator object
		if (root == null) { 	// First entry into the DLB
			root = new VINNode(i.current());
			VINNode cur = root;	// Store first char of VIN in root
			i.next();			// Move to next char in VIN
			while (i.getIndex() < i.getEndIndex()) { // Iterate through the VIN
				VINNode newNode = new VINNode(i.current());		// Create new node with next char in VIN to set as child
				cur.setChild(newNode);
				cur = cur.getChild();
				i.next();		// Iterate to next char of VIN
			}
			cur.setChild(new VINNode('^')); // terminal node to end entry
			if (car != null) {		// If the car is not null, it is safe to switch to it's child and set the new car as that child
				cur = cur.getChild();
				cur.setCar(car);
			}
		} 
		else {	// Not the first entry of the DLB
			VINNode cur = root;	// Set cur to root
			while (i.getIndex() < i.getEndIndex()) { 	// Iterate through the VIN
				while (i.current() != cur.getVal()) {	// While there isn't a match, get all siblings
					if (cur.getSibling() == null) {		// If no sibling matches, create a new sibling node
						VINNode newCarNode = new VINNode(i.current());
						cur.setSibling(newCarNode);
						cur = cur.getSibling();
						break;
					}
					cur = cur.getSibling();		// If sibling is found, then set cur to it
				}
				if (cur.getChild() == null){		// If no child, create a child and add the char from VIN to it
					VINNode newNode = new VINNode(i.current());
					cur.setChild(newNode);
				}
				cur = cur.getChild();		// Set cur to its child 
				i.next();
			}
			cur.setVal('^');		// Set the terminal node
			if (car != null) { 		// If the car is not null, it is safe to set the new car
				cur.setCar(car);
			}
		}
		return true;
	}

	// Remove a Car from the DLB
	public boolean remove(String vin){
		if (root == null) {		// If no root, there is nothing in the DLB so can't remove anything
			return false;
		}
		StringCharacterIterator i = new StringCharacterIterator(vin);	// New iterator
		VINNode cur = root;		// Set cur as the root
		while (i.getIndex() < i.getEndIndex()) {	// Iterate through the VIN
			if (cur == null) {	// If node is null, then that VIN is invalid
				return false;
			}
			while (i.current() != cur.getVal()) {	// While there isn't a match, get all siblings
				if (cur.getSibling() == null){
					return false;
				}
				cur = cur.getSibling();			// If there is a match, set cur = sibling
			}
			cur = cur.getChild();					// Keep getting the next child
			i.next();
		}
		if (cur == null) {		// If node is null at the end of loop, VIN was not found
			return false;
		}
		else if (cur.getVal() == '^') {		// If it was found, see if it is a terminal node
			if (cur.getCar() != null) {
				cur.setCar(null);			// Set the car = null to "remove" it
				return true;
			}
		}
		while (cur.getSibling() != null) {	// Iterate through the last node siblings to see if there is a terminal node
			if (cur.getVal() == '^') {
				if (cur.getCar() != null) {
					cur.setCar(null);		// Set the car = null to "remove" it
					return true;
				}
			}
			cur = cur.getSibling();		// If not terminal node, then set to sibling
		}
		return false;
	}

	// See if a car is already stored in the DLB
	public boolean exists(String vin){
		if (root == null) {		// If root is null, then nothing is stored in the DLB
			return false;
		}
		StringCharacterIterator i = new StringCharacterIterator(vin);	// VIN Iterator
		VINNode cur = root;	// Set cur as the root
		while (i.getIndex() < i.getEndIndex()) {		// Iterate through VIN
			if (cur == null) {		// If a node is null while iterating, the VIN doesn't exist
				return false;
			}
			while (i.current() != cur.getVal()) {	// Iterate through siblings to find match
				if (cur.getSibling() == null){		// If no sibling there is no match in the DLB
					return false;
				}
				cur = cur.getSibling();			// Set cur to the sibling that matches
			}
			cur = cur.getChild();					// Get the next child
			i.next();
		}
		if (cur == null) {	// If cur is null at the end of traversal, it is not in the DLB
			return false;
		}
		else if (cur.getVal() == '^') {		// If cur = ^ then it is a terminal node
			if (cur.getCar() != null) {		// If there is a car stored in there, the return true, otherwise return false
				return true;
			}
			return false;
		}
		while (cur.getSibling() != null) {	// Iterate through siblings to find terminal node
			if (cur.getVal() == '^') {
				if (cur.getCar() != null) {	// If there is a car stored in there, the return true, otherwise return false
					return true;
				}
				return false;
			}
			cur = cur.getSibling();		// If found terminal node, set cur to it
		}
		return false;
	}

	//Find the Car associated with a given VIN in the trie
	public Car getCar(String vin){
		if (root == null) {		// If root is null, then there is nothing in the DLB
			return null;
		}
		StringCharacterIterator i = new StringCharacterIterator(vin);	// New VIN iterator
		VINNode cur = root;	// Set cur as root
		while (i.getIndex() < i.getEndIndex()) {	// Iterate through the VIN
			if (cur == null) { 	// If cur == null at any point in the traversal, VIN is invalid
				return null;
			}
			while (i.current() != cur.getVal()) {	// Iterate through sibling list to find match
				if (cur.getSibling() == null) {		// If no match, then VIN is invalid
					return null; 
				}
				cur = cur.getSibling();			// Match, then set cur to that sibling
			}
			cur = cur.getChild();			// Get the next child 
			i.next();
		}
		if (cur == null) {		// If after the traversal, cur = null, then nothing was found
			return null;
		}
		else if (cur.getVal() == '^') {		// If cur is term node then get the car at that node
			return cur.getCar();
		}
		while(cur.getSibling() != null){	// Iterate through siblings to find the terminal node
			if (cur.getVal() == '^'){
				return cur.getCar();		// Get the car at that node
			}
			cur = cur.getSibling();		// Cycle through siblings
		}
		return cur.getCar();		// Null means no car found
	}
}