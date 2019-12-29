import java.io.*;
import java.util.*;

public class Key {
    public LargeInteger p;      // 1st random prime LargeInteger
    public LargeInteger q;      // 2nd random prime LargeInteger
    public LargeInteger n;      // p * q (public key)
    public LargeInteger phi;    // (p-1) * (q-1)
    public LargeInteger e;      // 1 < e < phi(n) and gcd(e, phi(n)) = 1 (public key)
    public LargeInteger d;      // d = e^-1 mod phi(n) (private key)

    // LargeInteger constants used for LargeInteger byte arrays (0 and 1)
    private LargeInteger ONE = new LargeInteger(new byte[] {(byte) 1});
    private LargeInteger ZERO = new LargeInteger(new byte[] {(byte) 0});

    public Key(Random rnd) {
        p = new LargeInteger(255, rnd);     // Constructs a LargeInteger using probablePrime() for p
        q = new LargeInteger(256, rnd);     // Constructs a LargeInteger using probablePrime() for q
        n = p.multiply(q);                  // Gets n = p * q
        phi = p.subtract(ONE).multiply(q.subtract(ONE));  // Gets phi = (p - 1) * (q - 1)
        e = new LargeInteger(new byte[] {(byte) 0}); // Set e to 0 initially
        LargeInteger[] gcdArr = phi.XGCD(e);

        while (gcdArr[0].compareTo(ONE) != 0) {        // While the GCD is NOT 1, keep looping
            e = new LargeInteger(phi.length(), rnd);   // Sets e to a new LargeInteger by using probablePrime())
            gcdArr = phi.XGCD(e);
        }
        d = gcdArr[2].mod(phi);     // Get the y value of the XGCD operation
        
        if (d.isNegative()) {       // If d is negative, add phi to it
            d = d.add(phi);
        }
    }

    // Gets the public key
    public PublicKey getPubKey() {
        return new PublicKey(n, e);
    }

    // Gets the private key
    public PrivateKey getPrivateKey() {
        return new PrivateKey(n, d);
    }
}