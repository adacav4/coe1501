import java.io.*;
import java.nio.file.*;

public class RsaSign {
    static String file;         // File that you want to sign/verify
    static LargeInteger hLI;    // Hash of the file message (LargeInteger)

    public static Object readFile(String f) {
        try {
            ObjectInputStream objOut = new ObjectInputStream(new FileInputStream(f));   // Create new object in stream
            return (objOut.readObject());   // Return the object after reading the file
        } catch (Exception e) {             // Catch exceptions if file not found
            System.out.println("File not found");
            System.exit(0);
            return null;    // Return null for compilation purposes
        }
    }

    public static LargeInteger sigIn(String file) {
        try {
            Path p = Paths.get(file + ".sig");      // Get sig file
            byte[] data = Files.readAllBytes(p);    // Read in file as bytes
            return new LargeInteger(data);          // Return the data as a LargeInteger
        } catch (Exception e) {     // Catch exceptions if file not found
            System.out.println("File not found");
            System.exit(0);
            return null;
        }
    }

    public static void sigOut(String file, LargeInteger s) {
        try {
            FileOutputStream fOut = new FileOutputStream(file + ".sig");    // Create new sig file
            fOut.write(s.getVal());   // Write data to the val array of the LargeInteger
            fOut.close();             // Close file output stream
            System.out.println("Signed signature successfully!");
        } catch (Exception e) {       // Catch exceptions if file could not be written to and exit
            System.out.println("Unable to output data to file");
            System.exit(0);
        }
    }

    public static void verifySig() {
        PublicKey pub = (PublicKey) readFile("pubkey.rsa");  // Reads the key file to get the public key
        LargeInteger s = sigIn(file);                       // Reads the sig file to get the signature
        if (s.modularExp(pub.e, pub.n).subtract(hLI).isZero()) {        // Use modular exponentiation to calculate m
            System.out.println("Signature successfully validated!");    // If m - (the hash of the LargeInteger) == 0, the signatures are the same (valid)
        }
        else {
            System.out.println("Signature is NOT valid!");  // If not equal, then the signature is invaild
        }
    }

    public static void signSig() {
        PrivateKey priv = (PrivateKey) readFile("privkey.rsa");     // Get private key from file
        sigOut(file, hLI.modularExp(priv.d, priv.n));      // Use modular exponentiation to get signature and send the new signature out to file
    }

    public static void main(String[] args) {
        if (args.length != 2) {     // If wrong command line args, just exit
            System.out.println("Invalid command line arguments");
            System.exit(0);
        }
        char flag = args[0].charAt(0);      // Check whether user wants to verify or sign the key
        file = args[1];
        hLI = HashEx.hashGen(file);
        if (flag == 'v') {      // v == verify the signature of the key file
            verifySig();
        }
        else {      // s == sign a key file
            signSig();
        }
    }
}