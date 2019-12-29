import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.security.MessageDigest;

public class HashEx {
	public static LargeInteger hashGen(String file) {
		// Lazily catch all exceptions...
		try {
			// Read in the file to hash
			Path path = Paths.get(file);
			byte[] data = Files.readAllBytes(path);

			// Create class instance to create SHA-256 hash
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			// Process the file
			md.update(data);
			// Generate a hash of the file
			byte[] digest = md.digest();
			return new LargeInteger(digest);	// Return the hashed message as a Large Integer
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
	}
}