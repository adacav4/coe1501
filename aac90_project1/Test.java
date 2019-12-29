public class Test {
	public static void main(String[] args) {
		DLB<Character, String> test = new DLB<Character, String>('~', "~");

		TrieMethods.setWord(test, "Hello");
		TrieMethods.setWord(test, "Hey");
		TrieMethods.setWord(test, "Hunch");
		TrieMethods.setWord(test, "Dome");
		TrieMethods.setWord(test, "Dumb");

		
	}
}