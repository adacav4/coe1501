// Class to manage the Priority Queues and DLB's

public class PQGarage {
    private static CarPriorityQueue prices;     // PQ for prices
    private static CarPriorityQueue mileages;   // PQ for mileages
    private static MakeModelDLB specPrices;     // DLB to store prices for specific makes and models
    private static MakeModelDLB specMileages;   // DLB to store mileages for specific makes and models

    // Constructor
    public PQGarage() {
        prices = new CarPriorityQueue('p', false);      // For all cars
        mileages = new CarPriorityQueue('m', false);    // For all cars
        specPrices = new MakeModelDLB();                // For specific make and model
        specMileages = new MakeModelDLB();              // For specific make and model
    }   

    public void insert(Car car){
        String make = car.getMake();    // Get make and model and concatonate into one string later to store in DLB
        String model = car.getModel();
        // Add the car into both PQ's (mileage and price)
        prices.insert(car);
        mileages.insert(car);
        // See if the same make/model was entered before in both PQ's
        CarPriorityQueue specPricePQ = specPrices.getPQ(make + model);
        CarPriorityQueue specMileagePQ = specMileages.getPQ(make + model);
        if (specPricePQ == null) {  // If not, create a new PQ for prices of a specific make/model
            CarPriorityQueue newPQ = new CarPriorityQueue('p', true);   
            specPrices.insert(make + model, newPQ);    // Store into the prices PQ
            specPricePQ = newPQ;
        }
        if (specMileagePQ == null) { // If not create a new PQ for mileages of a specific make/model
            CarPriorityQueue newPQ = new CarPriorityQueue('m', true); 
            specMileages.insert(make + model, newPQ); // Store into the mileages PQ
            specMileagePQ = newPQ;
        }
        // Store the car into both price and mileage make/model PQ's
        specPricePQ.insert(car);
        specMileagePQ.insert(car);
    }

    // Remove a car from the garage (all PQ's)
    public void remove(Car car){
        // Get all of the car's stats
        String make = car.getMake();
        String model = car.getModel();
        int pricesIndex = car.getPricesIndex();
        int mileagesIndex = car.getMileageIndex();
        int specPriceIndex = car.getSpecPriceIndex();
        int specMileageIndex = car.getSpecMileageIndex();
        
        // Delete from both PQ's
        prices.delete(pricesIndex);
        mileages.delete(mileagesIndex);

        // Concatonated make + model string used to find specfic PQ's 
        CarPriorityQueue specPricePQ = specPrices.getPQ(make + model);
        CarPriorityQueue specMileagePQ = specMileages.getPQ(make + model);
        
        // If these PQ's exist, then delete from them, otherwise do nothing
        if (specPricePQ != null) {
            specPricePQ.delete(specPriceIndex);
        }
        if(specMileagePQ != null) {
            specMileagePQ.delete(specMileageIndex);
        }
    }

    // Update the car's price
    public void updatePrice(Car car){ 
        // Get car stats
        String make = car.getMake();
        String model = car.getModel();
        int pricesIndex = car.getPricesIndex();
        int specPricesIndex = car.getSpecPriceIndex();
        prices.update(pricesIndex); // Update the price PQ at specified index

        CarPriorityQueue specPricesPQ = specPrices.getPQ(make + model);    // Get PQ of the VIN specified
        if (specPricesPQ != null) {         // If null, VIN is invalid
            specPricesPQ.update(specPricesIndex);
        }
    }

    // Update the car's mileage
    public void updateMileage(Car car){ 
        // Get car stats
        String make = car.getMake();
        String model = car.getModel();
        int mileageIndex = car.getMileageIndex();
        int specMileageIndex = car.getSpecMileageIndex();
        mileages.update(mileageIndex);  // Update the price PQ at specified index

        CarPriorityQueue specMileagesPQ = specMileages.getPQ(make + model);    // Get PQ of the VIN specified
        if (specMileagesPQ != null) {               // If null, VIN is invalid
            specMileagesPQ.update(specMileageIndex); 
        }
    }

    // Get the car with the min price
    public Car getMinPrice(){
        return prices.getMin();
    }

    // Get the car with the min mileage
    public Car getMinMileage(){
        return mileages.getMin();
    }

    // Get the car with the min price for a specific make/model
    public Car getMinSpecPrice(String make, String model){
        CarPriorityQueue specPricePQ = specPrices.getPQ(make + model);
        if (specPricePQ != null) {      // If null, then no PQ for that make/model exists
            return specPricePQ.getMin();
        }
        return null;
    }

    // Get the car with the lowest mileage for a specific make/model
    public Car getMinSpecMileage(String make, String model){
        CarPriorityQueue specMileagePQ = specMileages.getPQ(make + model);
        if (specMileagePQ != null) {    // If null, then no PQ for that make/model exists
            return specMileagePQ.getMin();
        }
        return null;
    }
}