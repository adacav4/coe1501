// Main driver program

import java.util.*;

public class CarTracker {
    private static CarDLB cars;
    private static PQGarage garage;
    private static Scanner s;

    public static void main(String[] args) {
        cars = new CarDLB();            // Car DLB to store cars
        garage = new PQGarage();        // Garage to store different PQ's
        s = new Scanner(System.in);     // Scanner for input
        String input = "";
        System.out.println("Welcome to CarTracker\n");
        while (true) {
            // Menu print statements
            System.out.println("1. Add a car");
            System.out.println("2. Update a car");
            System.out.println("3. Remove a car");
            System.out.println("4. Retrieve the lowest priced car");
            System.out.println("5. Retrieve the lowest mileage car");
            System.out.println("6. Retrieve the lowest priced car by make and model");
            System.out.println("7. Retrieve the lowest mileage car by make and model");
            System.out.println("8. Quit");
            System.out.print("\nEnter a number 1-8: ");
            input = s.nextLine();

            // Outcome for each input
            if (input.equals("1")) {
                add();
                System.out.println("CarTracker\n");
            }
            else if(input.equals("2")) {
                update();
                System.out.println("CarTracker\n");
            }
            else if(input.equals("3")) {
                remove();
                System.out.println("CarTracker\n");
            }
            else if (input.equals("4")) {
                getMinPrice();
                System.out.println("CarTracker\n");
            } 
            else if (input.equals("5")) {
                getMinMileage();
                System.out.println("CarTracker\n");
            } 
            else if (input.equals("6")) {
                getMinSpecPrice();
                System.out.println("CarTracker\n");
            } 
            else if (input.equals("7")) {
                getMinSpecMileage();
                System.out.println("CarTracker\n");
            } 
            else if (input.equals("8")) {
                System.out.println("\nExiting CarTracker\n");
                System.exit(0);
            } 
            else {
                System.out.println("Invalid entry. Select a number 1-8.\n");
            }
        }
    }

    // Wrapper method for adding a car (adds to all PQ's and the DLB)
    public static void add() {
        // Vars for car stats
        String vin = "";
        String make = "";
        String model = "";
        String color = "";
        int mileage = 0;
        int price = 0;

        // I/O for car stats
        System.out.print("\nEnter the VIN: ");
        vin = s.nextLine();
        System.out.print("Enter the make: ");
        make = s.nextLine();
        System.out.print("Enter the model: ");
        model = s.nextLine();
        System.out.print("Enter the color: ");
        color = s.nextLine();

        // Catch exception if a number was not entered for the mileage
        try{
            System.out.print("Enter the mileage: ");
            mileage = Integer.parseInt(s.nextLine());
            while (mileage < 0) {
                System.out.print("Invalid mileage. Try again: ");
                mileage = Integer.parseInt(s.nextLine());
            }
        } catch (NumberFormatException e) {
            System.out.print("Invalid mileage. Try again: ");
            mileage = Integer.parseInt(s.nextLine());
            while (mileage < 0) {
                System.out.print("Invalid mileage. Try again: ");
                mileage = Integer.parseInt(s.nextLine());
            }
        }

        // Catch exception if a number was not entered for the price
        try {
            System.out.print("Enter a Price: $");
            price = Integer.parseInt(s.nextLine());
            while (price < 0) { //Invalid price <0$
                System.out.print("Invalid price. Try again: $");
                price = Integer.parseInt(s.nextLine());
            }
        } catch(NumberFormatException e){
            System.out.print("Invalid price. Try again: $");
            price = Integer.parseInt(s.nextLine());
            while (price < 0) {
                System.out.print("Invalid price. Try again: $");
                price = Integer.parseInt(s.nextLine());
            }
        }
        // If car exists already, do nothing
        if (cars.exists(vin)) {
            System.out.println("\nA car with that VIN number already exists.");
            return;
        }

        // Store all the car stats in a Car object
        Car newCar = new Car(vin, make, model, color, mileage, price); 
        // Insert into CarDLB
        cars.insert(newCar);
        // Insert into garage (Both PQ's for price and mileage)
        garage.insert(newCar);
        System.out.println();
    }

    // Wrapper method to remove the car from the DLB and the two PQ's
    public static void remove() {
        // I/O
        System.out.print("\nEnter the VIN of the car to remove: ");
        String vin = s.nextLine();

        // Get the car by the VIN from the CarDLB
        Car car = cars.getCar(vin); 
        while (car == null) {           // If car doesn't exist, then the VIN is invalid
            System.out.println("\nInvalid VIN");
            System.out.print("\nEnter the VIN of the car to remove: ");
            vin = s.nextLine();
            car = cars.getCar(vin); // Check newly entered VIN
        }

        // Remove from the garage (two PQ's) and the CarDLB
        garage.remove(car);
        cars.remove(vin);
        System.out.println();
    }

    // Wrapper method to update one of the car's stats (update the garage and the CarDLB)
    public static void update() {
        // I/O
        System.out.print("\nEnter the VIN of the car you wish to update: ");
        String vin = s.nextLine();
        Car car = cars.getCar(vin);     // Get car from DLB
        while (car == null) {           // If car is null, then it is an invalid VIN
            System.out.println("\nInvalid VIN");
            System.out.print("Enter the VIN of the car you wish to update: ");
            vin = s.nextLine();
            car = cars.getCar(vin);     // Check new VIN to see if it's in the CarDLB
        }

        // More I/O for updating specific car stat
        System.out.print("\nChoose what you want to update: 1. Price  2. Mileage  3. Color: ");
        String input = s.nextLine();
        while (!input.equals("1") && !input.equals("2") && !input.equals("3") && !input.equals("q")) {
            System.out.println("\nInvalid Choice");
            System.out.print("\nChoose what you want to update: 1. Price  2. Mileage  3. Color: ");
            input = s.nextLine();   // Check new input if invalid
        }
        // 1 -> Update car price
        if (input.equals("1")) {
            int price = -1;     // Initialized to -1 so that if user doesn't enter a number, it will count as an invalid input

            // I/O
            // Catch exception if a number was not entered for the price
            try {
                System.out.print("Enter a new Price: $");
                price = Integer.parseInt(s.nextLine());
                while (price < 0) {
                    System.out.print("Invalid price. Try again: $");
                    price = Integer.parseInt(s.nextLine());
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid price. Try again: $");
                price = Integer.parseInt(s.nextLine());
                while (price < 0) {
                    System.out.print("Invalid price. Try again: $");
                    price = Integer.parseInt(s.nextLine());
                }
            }
            car.setPrice(price);    // Set the new price of the car in the CarDLB
            garage.updatePrice(car);    // Update car price in the garage as well
        }
        // 2 -> Update car mileage
        else if (input.equals("2")) { 
            int mileage = -1;   // Initialized to -1 so that if user doesn't enter a number, it will count as an invalid input

            // I/O
            // Catch exception if a number was not entered for the mileage
            try {
                System.out.print("Enter a new Mileage: ");
                mileage = Integer.parseInt(s.nextLine());
                while (mileage < 0) {
                    System.out.print("Invalid mileage. Try again: ");
                    mileage = Integer.parseInt(s.nextLine());
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid mileage. Try again: ");
                mileage = Integer.parseInt(s.nextLine());
                while (mileage < 0) {
                    System.out.print("Invalid mileage. Try again: ");
                    mileage = Integer.parseInt(s.nextLine());
                }
            }
            car.setMileage(mileage);    // Set the new price of the car in the CarDLB
            garage.updateMileage(car);  // Update car mileage in the garage as well
        } 

        // 3 -> Update car color
        else if (input.equals("3")) {
            System.out.print("Enter a new color: ");    // I/O for car color
            String color = s.nextLine();        
            car.setColor(color);    // Update CarDLB (no need to update garage)
        }
        System.out.println();
    }

    //Gets the lowest mileage car in general
    public static void getMinMileage() {
        Car min = garage.getMinMileage();   // Gets the min car from the mileage PQ in the garage
        if (min == null) {                  // If there are no cars added yet, do nothing
            System.out.println("\nThere are no cars in the Garage.");
        }
        else {
            System.out.println("\nLowest mileage car:\n" + min.printCar());  // Otherwise, print the car's stats
        }
    }

    // Gets the lowest priced car in general
    public static void getMinPrice() {
        Car min = garage.getMinPrice();     // Gets the min car from the price PQ in the garage
        if (min == null) {                  // If there are no cars added yet, do nothing
            System.out.println("\nThere are no cars in the Garage.");
        }
        else {
            System.out.println("\nLowest priced car:\n" + min.printCar());  // Otherwise, print the car's stats
        }
    }

    // Gets the lowest priced car from a specific make and model
    public static void getMinSpecPrice() {
        // I/O for make + model
        System.out.print("\nEnter a make: ");
        String make = s.nextLine();
        System.out.print("\nEnter a model: ");
        String model = s.nextLine();
        Car min = garage.getMinSpecPrice(make, model);      // Gets the min price for the specific make/model PQ in the garage
        if (min == null) {                              // If there are no cars added yet, do nothing
            System.out.println("\nThere are no cars in the Garage.");
        }
        else {
            System.out.println("\nLowest priced car for this make and model:\n" + min.printCar());  // Otherwise, print the car's stats
        }
    }

    // Gets the lowest mileaged car from a specific make and model
    public static void getMinSpecMileage() {
        // I/O for make + model
        System.out.print("\nEnter a make: ");
        String make = s.nextLine();
        System.out.print("Enter a model: ");
        String model = s.nextLine();
        Car min = garage.getMinSpecMileage(make, model); // Gets the min mileage for the specific make/model PQ in the garage
        if (min == null) {                              // If there are no cars added yet, do nothing
            System.out.println("\nThere are no cars in the Garage.");
        }
        else {
            System.out.println("\nLowest mileage car for this make and model:\n" + min.printCar());  // Otherwise, print the car's stats
        }
    }
}