public class Car{
    private String vin = ""; 
    private String make = "";
    private String model = "";
    private String color = "";
    private int price;
    private int mileage;
    private int priceIndex = -1; // Price PQ heap index for all cars
    private int mileageIndex = -1;// Mileage PQ heap index for all cars
    private int specPriceIndex = -1; // Price PQ heap index for a specific make/model
    private int specMileageIndex = -1; // Mileage PQ heap index for a specific make/model

    // Constructor
    public Car(String v, String ma, String mo, String col, int mi, int p){
        vin = v;
        make = ma;
        model = mo;
        color = col;
        price = p;
        mileage = mi;
    }

    // Setters

    // Sets the VIN for a car
    public void setVIN(String v){
        vin = v;
    }

    // Sets the Make of a car
    public void setMake(String ma){
        make = ma;
    }

    // Sets the Model of a car
    public void setModel(String mo){
        model = mo;
    }

    // Sets the Price of a car
    public void setPrice(int newP){
        price = newP;
    }

    // Sets the color of a car
    public void setColor(String newCol){
        color = newCol;
    }

    // Sets the mileage of a car
    public void setMileage(int newMi){
        mileage = newMi;
    }

    // Sets the price index of a car (for all cars)
    public void setPriceIndex(int i){
        priceIndex = i;
    }

    // Sets the mileage index of a car (for all cars)
    public void setMileageIndex(int i){
        mileageIndex = i;
    }

    // Sets the price index of a car (for specific make/model)
    public void setSpecPriceIndex(int i){
        specPriceIndex = i;
    }

    // Sets the mileage index of a car (for specific make/model)
    public void setSpecMileageIndex(int i){
        specMileageIndex = i;
    }

    // Getters

    // Gets VIN of a car
    public String getVIN(){
        return vin;
    }

    // Gets Make of a car
    public String getMake(){
        return make;
    }

    // Gets Model of a car
    public String getModel(){
        return model;
    }

    // Gets color of a car
    public String getColor(){
        return color;
    }

    // Gets mileage of a car
    public int getMileage(){
        return mileage;
    }

    // Gets price of a car
    public int getPrice(){
        return price;
    }

    // Gets price index of a car (for all cars)
    public int getPricesIndex(){
        return priceIndex;
    }

    // Gets mileage index of a car (for all cars)
    public int getMileageIndex(){
        return mileageIndex;
    }

    // Gets price index of a car (for specific make/model)
    public int getSpecPriceIndex(){
        return specPriceIndex;
    }

    // Gets mileage index of a car (for specific make/model)
    public int getSpecMileageIndex(){
        return specMileageIndex;
    }

    // Returns out the stats of the car
    public String printCar(){
        return "VIN: " + vin + "\nMake: " + make + "\nModel: " + model + "\nColor: " + color + "\nMileage: " + mileage + " miles\nPrice: $" + price + "\n";
    }
}