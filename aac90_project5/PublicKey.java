import java.io.Serializable;

public class PublicKey implements Serializable {
    public LargeInteger e;
    public LargeInteger n;

    public PublicKey(LargeInteger nVal, LargeInteger eVal) {
        e = eVal;	// Store the e value (public key)
        n = nVal;	// Store the n value for mod(n)
    }
}