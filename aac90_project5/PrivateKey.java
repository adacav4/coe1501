import java.io.Serializable;

public class PrivateKey implements Serializable {
    public LargeInteger d;
    public LargeInteger n;

    public PrivateKey(LargeInteger nVal, LargeInteger dVal) {
        d = dVal;	// Store the d value (private key)
        n = nVal;	// Store the n value to use for mod(n)
    }
}