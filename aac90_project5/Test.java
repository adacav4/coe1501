public class Test {
	public static void main(String args[]) {
		byte[] xArr = {(byte) 1};
		byte[] yArr = {(byte) 2};
		byte[] zArr = {(byte) 1};

		LargeInteger x = new LargeInteger(xArr);
		LargeInteger y = new LargeInteger(yArr);
		LargeInteger z = new LargeInteger(zArr);

		System.out.println("x = " + x.toString() + "\ny = " + y.toString() + "\nz = " + z.toString());

		System.out.println("x * y = " + x.multiply(y).toString());
		System.out.println("x ModularExp y = " + z.modularExp(x, y).toString());
		System.out.println("GCD = " + x.XGCD(y)[0] + " x(GCD) = " + x.XGCD(y)[1]+ " y(GCD) = " + x.XGCD(y)[2]);
		System.out.println("x / y = " + x.divide(y)[0] + " Remainder = " + x.divide(y)[1]);

	}
}