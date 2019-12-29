import java.io.*;
import java.util.*;

public class RsaKeyGen {
    public static void main(String[] args) {
        Random rnd = new Random();      // Generate random seed generator for key
        Key key = new Key(rnd);         // Generate a new key
        try {
            ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream("pubkey.rsa"));     // Create new file to output key to
            objOut.writeObject(key.getPubKey());        // Write public key to file
            objOut = new ObjectOutputStream(new FileOutputStream("privkey.rsa"));   // Create new file to output key to
            objOut.writeObject(key.getPrivateKey());    // Write private key to file
            objOut.close();         // Close out output Stream
            System.out.println("Keys created successfully!");
        } catch (Exception e) {     // Catch an error if not able to output RSA key to file and exit
            System.out.println("Error with outputting RSA key to file");
            System.exit(0);
        }
    }
}
