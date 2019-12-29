// Taken from texbook code on Dr. Farnan's website

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CarPriorityQueue{
    private int elements;   // Number of elements in the PQ
    private char pqType;    // Determines which PQ is accessed (price or mileage)
    private Car[] heap;     // Heap array backing PQ for indexing a PQ (indirection)
    private boolean specMakeModel;  // User input specifying if user is searching for specific make/model

    // Constructor
    public CarPriorityQueue(char type, boolean spec) {
        elements = 0;
        pqType = type;
        heap = new Car[600];    // Arbitrary large number for starting size
        specMakeModel = spec;
    }

    // Returns the number of elements in the PQ
    public int size() {
        return elements;
    }

    // Inserts a car into the PQ
    public void insert(Car car){
        if (car == null) {  // If Car is null, can't do anything
            return;
        }
        elements++; // Increase the element count
        if (elements >= heap.length){ // Resize the heap if needed
            resizeHeap();
        }
        heap[elements] = car; // Insert the car into the heap so it's indexable
        swim(elements); 
    }

    // Resizes the heap
    public void resizeHeap(){
        Car[] newHeap = new Car[elements * 2]; // Doubles the size of the heap
        for (int i = 0; i <= elements; i++) { // Moving items over to new heap
            newHeap[i] = heap[i];
        }
    }

    // Gets the first entry of the PQ (the min)
    public Car getMin(){
        if (elements == 0) {
            return null;
        }
        return heap[1];
    }

    // Updates an entry at index i
    public void update(int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }
        swim(i);
        sink(i);
    }

    // Deletes an entry at index i
    public void delete(int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }

        exch(i, elements--); 
        swim(i);
        sink(i);
        heap[elements + 1] = null;
    }

    // General Helper functions

    // Compares two Cars at indexes i and j
    private boolean greater(int i, int j) {
        if (pqType == 'm') {    // Compares mileages
            return heap[i].getMileage() > heap[j].getMileage();
        }
        else if (pqType == 'p') {   // Compares prices
            return heap[i].getPrice() > heap[j].getPrice();
        }
        return false; 
    }

    // Swaps two Cars to the other car's index
    private void exch(int i, int j) {
        Car swap = heap[i];
        heap[i] = heap[j];
        heap[j] = swap;

        setIndex(i);
        setIndex(j);
    }

    // Sets the index of a car in either a mileage PQ or price PQ
    private void setIndex(int j) {
        if (pqType == 'm') { // Editing a mileage PQ
            if(specMakeModel) {     // If user requests specific make/model
                heap[j].setSpecMileageIndex(j); // Setting the new index
            }
            else { 
                heap[j].setMileageIndex(j); // If looking at all cars in general
            }
        } 

        else if (pqType == 'p') { // Editing a price PQ
            if(specMakeModel) {     // If user requests specific make/model
                heap[j].setSpecPriceIndex(j); // Setting the new index
            }
            else {
                heap[j].setPriceIndex(j); // If looking at all cars in general
            }
        }
    }

   // Heap helper functions

    private void swim(int k) {
        while (k > 1 && greater(k / 2, k)) {
            exch(k, k / 2);
            k = k / 2;
        }
        setIndex(k);
    }

    private void sink(int k) {
        while (2 * k <= elements){
            int j = 2 * k;
            if (j < elements && greater(j, j + 1)) {
                j++;
            }
            if (!greater(k, j)) {
                break;
            }
            exch(k, j);
            k = j;
        }
        setIndex(k);
    }
}