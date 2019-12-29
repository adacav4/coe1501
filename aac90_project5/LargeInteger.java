import java.util.Random;
import java.math.BigInteger;
import java.io.Serializable;	// Convert objects to byte streams

public class LargeInteger implements Serializable {
	
	// Constants used for LargeInteger byte arrays (0 and 1)
	private final byte[] ONE = {(byte) 1};
	private final byte[] ZERO = {(byte) 0};

	private byte[] val;

	/**
	 * Construct the LargeInteger from a given byte array
	 * @param b the byte array that this LargeInteger should represent
	 */
	public LargeInteger(byte[] b) {
		val = b;
	}

	/**
	 * Construct a new LargeInteger from a given LargeInteger value
	 * @param newLI the LargeInteger that the new LargeInteger should represent
	 */
	public LargeInteger(LargeInteger newLI) {
		val = newLI.getVal();
	}

	/**
	 * Construct the LargeInteger by generatin a random n-bit number that is
	 * probably prime (2^-100 chance of being composite).
	 * @param n the bitlength of the requested integer
	 * @param rnd instance of java.util.Random to use in prime generation
	 */
	public LargeInteger(int n, Random rnd) {
		val = BigInteger.probablePrime(n, rnd).toByteArray();
	}
	
	/**
	 * Return this LargeInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/** 
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most 
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other LargeInteger to sum with this
	 */
	public LargeInteger add(LargeInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		LargeInteger res_li = new LargeInteger(res);
	
		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public LargeInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's 
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		LargeInteger neg_li = new LargeInteger(neg);
	
		// add 1 to complete two's complement negation
		return neg_li.add(new LargeInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other LargeInteger to subtract from this
	 * @return difference of this and other
	 */
	public LargeInteger subtract(LargeInteger other) {
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * @param other LargeInteger to multiply by this
	 * @return product of this and other
	 */
	public LargeInteger multiply(LargeInteger other) {
		int sAmt = 0;	// Shift amount used in grade school algorithm
		LargeInteger first = new LargeInteger(ZERO);	// Initialize to 0 (First number multiplied)
		LargeInteger second = new LargeInteger(ZERO);	// Initialize to 0 (Second number multiplied)
		LargeInteger product = new LargeInteger(ZERO);	// Initialize to 0 (Product of first and second)

		// Easy edge case when one of the numbers = 0, return 0
		if (this.isZero() || other.isZero()) {
			return new LargeInteger(ZERO);	// first == 0 at this point
		}
		if (val.length < other.length()) { 	// If other > this, "other" gets multiplied "this" many times 
			first = other;
			second = this;
		}
		else {		// If other > this, "this" gets multiplied "other" many times 
			first = this;
			second = other;
		}

		// If one of the numbers being multiplied is negative, then the product must be negative (used to negate the product later)
		boolean negProd = (first.isNegative() && !second.isNegative()) || (!first.isNegative() && second.isNegative());
		if (first.isNegative() && second.isNegative()) { 	// If both numbers are negative, make them both postive
			first = first.negate();						 	// Will get the same result since product will also be postive
			second = second.negate();
		} 
		else if (first.isNegative() && !second.isNegative()) {	// If first is negative, make positive for multiplication purposes
			first = first.negate();
		} 
		else if (!first.isNegative() && second.isNegative()) {	// If second is negative, make positive for multiplication purposes
			second = second.negate();
		}
		byte[] sVal = second.getVal();

		// Used the grade School algorithm
		int cur = 0;			// Initialize to 0
		byte secondByte = 0;	// Initialize to 0
		for (int i = sVal.length - 1; i >= 0; i--) {	// Loop through the second LargeInteger val array
			secondByte = sVal[i];				// Set the specified byte as the current byte in the array
			for (int j = 0; j < 8; j++) {		// Loop through each bit of the byte
				cur = (secondByte >> j) & 1;	// Right shift the current byte by j, then AND it with 1
				if (cur == 1) {
					product = product.add(first.leftShift(sAmt, false));	// Set the product using GS algorithm
				}
				sAmt++;		// Increment the amount of left shifts needed
			}
		}
		if (negProd) {		// If the product is supposed to be negative, negate it
			product = product.negate();
		}
		return product.removePadding();	// Return the product wihtout leading bytes
	}
	
	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another LargeInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	public LargeInteger[] XGCD(LargeInteger other) {
	 	LargeInteger one = new LargeInteger(ONE);		// Make a new LargeInteger for the number 1
		LargeInteger zero = new LargeInteger(ZERO);		// Make a new LargeInteger for the number 0

		// Base case: If the other LargeInteger == 0, then the GCD == the same LargeInteger, x = 1, y = 0
		if (other.isZero()) {
			return new LargeInteger[] {this, one, zero};
		}

		// Inspired by ExtendedEuclid.java in the textbook
		LargeInteger[] vals = other.XGCD(this.mod(other));		// Recurse: find GCD(other, (this)mod(other))
	 	LargeInteger d = vals[0];		// Get d value for formula (GCD)
		LargeInteger a = vals[2];		// Get a value for formula (y)
		LargeInteger div = this.divide(other)[0];	// Result of this (LargeInteger / other LargeInteger)
		LargeInteger b = vals[1].subtract(div.multiply(a));	// x - (LargeInteger / other LargeInteger) * y
		return new LargeInteger[] {d, a, b};
	}

	 /**
	  * Compute the result of raising this to the power of y mod n
	  * @param y exponent to raise this to
	  * @param n modulus value to use
	  * @return this^y mod n
	  */
	public LargeInteger modularExp(LargeInteger y, LargeInteger n) {
	 	LargeInteger ans = new LargeInteger(ONE);	// Initialize the answer to 1
	 	int cur = 0;				// Initialized to 0
	 	byte[] exp = y.getVal();	// Gets the value array from the first LargeInteger
	 	for (byte b : exp) {		// Loop through the value array
	 		for (int i = 7; i >= 0; i--) {			// Loop through each bit in byte (starting from LSB)
				cur = (b >> i) & 1;
				ans = ans.multiply(ans).mod(n);		// Do modular exponentiation to do (ans^2)mod(n)
				if (cur == 1) {		// If second to last bit in byte
					ans = ans.multiply(this).mod(n);	// Do another modular exponentiation to do (ans * this)mod(n)
				}
			}
		}
		return ans;
	}
	
	// HELPER METHODS

	public LargeInteger mod(LargeInteger divisor) {
	 	return this.divide(divisor)[1];		// Returns the remainder
	}

	public boolean isZero() {
	 	for (byte b : val) {	// Iterate through the byte array to see if there are any 0's
	 		if (b != 0) {
	 			return false;	// Return false if at least one 0 is present in the val array
	 		}
	 	}
		return true;			// If no 0's are in the val array, return true
	}

	public int getPaddingAmt() {
	 	int i = 0;	// Increment value
	 	int pAmt = 0;	// The amount of padding
		byte cur = val[i];				// Set the cur to first byte of LargeInteger
		int firstBit = cur >> 7 & 1;	// Right shift by 7 then AND by one to get the first bit
		byte padded = 0;
		if ((byte)firstBit == 1) {
			padded = (byte)0xFF;
		}	// Else, just leave it as 0
		while (val.length > i + 1 && cur == padded) {	// Add 8 bits for every byte that is padding
			pAmt += 8;			// Increment number of bits (8 bits per byte) by 8
			cur = val[++i];		// Set cur as the next byte in the array
		}
		int curBit = 7;	// New cur int for each individual bit (start at the 7th (last) bit of the byte)
		while ((cur >> curBit & 0x01) == firstBit && curBit >= 0) {
			pAmt++;		// Increment the padding by one bit
			curBit--;	// Decrement curBit until it goes through all bits in the byte
		}
		return pAmt;	// Return the amount of padding
	}

	public LargeInteger removePadding() {
	 	int pAmt = getPaddingAmt() - 1;							// Get the padding amount for the Large Integer (in bits)
		int lBit = pAmt / 8;									// Divide by 8 to get padding amount in bytes
		int newSize = val.length - lBit;						// New size of byte array without padding 0 entries
		if (pAmt > 8) {											// If padding is greater than 8 bits, remove the padding
			byte[] newByte = new byte[newSize];					// Create a new byte array without space for padding
			System.arraycopy(val, lBit, newByte, 0, newSize);	// Copy the desired portion of the val array (without padding)
			return new LargeInteger(newByte);					// Return the new LargeInteger without the padding
		}
		return this;											// Return the same LargeInteger if padding <= 8 bits
	}

	public LargeInteger leftShift(int sAmt, boolean shiftOne) {
		if (sAmt == 0) {	// If the shift amount is 0, return the same LargeInteger, unshifted
			return this;
		}
		// Bit Masks (used to manipulate each byte in the byte array)
		// AND'd and OR'd with each byte to get a different value
		// The index of the arrays correspond with the number of bits
		byte[] onesMask = {0, 1, 3, 7, 15, 31, 63, 127, -1};
		byte[] lMask = {-1,  -2, -4, -8, -16, -32, -64, -128, 0};
		byte[] rMask = {-1, 127, 63, 31, 15, 7, 3, 1};
		int pAmt = getPaddingAmt() % 8;		// Gets number of leftover bits that don't form a byte
		int extraBits = sAmt % 8;			// Gets number of leftover bits to shift over (not bytes)
		int shiftedBytes = 0;				// Initialize to 0
		if (sAmt % 8 == 0 || extraBits < pAmt) {	// Gets the number of whole bytes to shift over
			shiftedBytes = sAmt / 8;		// If no leftover bits, just divide to get the number whole bytes
		}
		else {
			shiftedBytes = (sAmt / 8) + 1;	// Add's an extra byte for the remaining bits leftover
		}
		if (extraBits == 0) {	// If the number of leftover bits = 0, create an extra byte's worth of extra bits
			extraBits = 8;
		}
		int rsAmt = 8 - extraBits;		// Right shift 8 - the number of leftover bits (used to index the right bitmask array for splitting the bytes into two)
		byte[] newBytes = new byte[val.length + shiftedBytes];	// New array of shifted bytes
		byte left = 0;		// Initialize to 0 (left half of split byte)
		byte right = 0;		// Initialize to 0 (right half of split byte)
		if (extraBits < pAmt) {		// If the number of extra bits is less than the padding amount
			for (int i = 0; i < val.length - 1; i++) {		// Loop through val array
				left = (byte)((val[i] << extraBits) & lMask[extraBits]);		// Gets the left split of the byte after left shifts using bitmasking
				right = (byte)((val[i + 1] >> rsAmt) & rMask[rsAmt]);			// Gets the right split of the byte after right shifts using bitmasking
				newBytes[i] = (byte)(left | right);		// Once shifting is done, OR the two split halves together to form the new byte
			}
			newBytes[val.length - 1] = (byte)(val[val.length - 1] << extraBits);	// Right shift by the number of remaining bits
			if (shiftOne) {			// If we want to shift in a one
				newBytes[val.length - 1] = (byte)(newBytes[val.length - 1] | onesMask[extraBits]); // OR the byte with a bitmask
				for (int i = val.length; i < newBytes.length; i++) {	// Fills in extra indecies with 0xFF
					newBytes[i] = (byte)(0xFF);
				}
			}
		} 
		else {			// If the number of extra bits is greater or equal than the padding amount
			newBytes[0] = (byte)(val[0] >>> rsAmt);		// Logical right shift the first byte in the array by rsAmt
			for (int i = 1; i < val.length; i++) {		// Loop through the val array
				left = (byte)((val[i - 1] << extraBits) & lMask[extraBits]);	// Gets the left split of the byte after left shifts using bitmasking
				right = ((byte)((val[i] >>> rsAmt) & rMask[rsAmt]));			// Gets the right split of the byte after logical right shifts using bitmasking
				newBytes[i] = (byte)(left | right);		// Once shifting is done, OR the two split halves together to form the new byte
			}
			newBytes[val.length] = (byte)(val[val.length - 1] << extraBits);
			if (shiftOne) {
				newBytes[val.length] = (byte)(newBytes[val.length] | onesMask[extraBits]);	// OR the byte with a bitmask
				for (int i = val.length + 1; i < newBytes.length; i++) {	// Fills in extra indecies with 0xFF
					newBytes[i] = (byte)(0xFF);
				}
			}
		}
		return new LargeInteger(newBytes);
	}

	public LargeInteger rightShift() {
		byte[] shiftedBytes = new byte[val.length];	// Create new byte array to store shifted LargeInteger
		int firstBit = val[0] & 1;					// Sets firstBit to either 0 or 1, depending on what the MSB is of val[0]
		shiftedBytes[0] = (byte)(val[0] >> 1);		// Right shifts val[0] by 1 and stores in new byte array
		for (int i = 1; i < val.length; i++) {		// Loops through entire val array starting from val[1] 
			byte cur = val[i];
			int lsb = cur & 1;			// Gets least significant bit by AND-ing with the cur byte
			byte fmask = 0;			// Uses bitmasking to get 
			if (firstBit == 1) {	// If first bit is 1, negative number
				fmask = (byte)128;	// 0b10000000 keeps signed value in shifted byte
			}
			else {		// Else positive number
				fmask = (byte)0;	// Sign doesn't matter in bitmask
			}
			shiftedBytes[i] = (byte)(cur >> 1 & 0b01111111 | fmask);	// Right shifts by one, AND's with 127, then OR's with fmask
			firstBit = lsb;		// Resets the firstBit to the least significant bit of the byte
		}
		return new LargeInteger(shiftedBytes);
	}
	
	public LargeInteger[] divide(LargeInteger divisor) {	// Returns the array {quotient, remainder}
		LargeInteger dividend = this;						// Stores this LargeInteger as the dividend
		boolean negRem = this.isNegative();					// If the dividend is negative, the remainder will be negative
		LargeInteger one = new LargeInteger(ONE);			// Make a new LargeInteger for the number 1
		LargeInteger zero = new LargeInteger(ZERO);			// Make a new LargeInteger for the number 0
		
		// If one of the numbers is negative, the quotient will be negative (used to negate the quotient later)
		boolean sign = (dividend.isNegative() && !divisor.isNegative()) || (!dividend.isNegative() && divisor.isNegative());
		if (divisor.isNegative()) {		// Make the divisor positive if negative
			divisor = divisor.negate();
		}
		if (dividend.isNegative()) {	// Make the dividend positive if negative
			dividend = dividend.negate();
		}

		// Easy edge case for if the two numbers are equal
		if (dividend.compareTo(divisor) == 0) {
			if (sign) {		// if the quotient is supposed to be negative, make the 1 --> -1
				one = one.negate();
			}
			return new LargeInteger[] {one, zero};	// Return 1 (or -1 if sign is true)
		}

		// Easy edge case for if the dividend is less than the divisor
		if (dividend.compareTo(divisor) == -1) {
			if (negRem) {			// If the remainder is supposed to be negative, the dividend should be negated back
				dividend = dividend.negate();
			}
			return new LargeInteger[] {zero, dividend};		// No quotient, but the remainder == dividend
		}
		int sAmt = (dividend.length() * 8) - 1;		// Get the shift amount in bytes
		divisor = divisor.leftShift(sAmt, false);	// Left shift the divisor by the shift amount
		LargeInteger rem = dividend;
		LargeInteger quotient = new LargeInteger(ZERO);

		int i = 0;	// Iterator
		while (i <= sAmt) {		// Loop for the value of the shift amount
			LargeInteger diff = rem.subtract(divisor);		// Calculate the difference (remainder - divisor)
			if (diff.isNegative()) {			// If the difference is negative, left shift a 0 in
				quotient = quotient.leftShift(1, false);
			}
			else {		// If the difference is positive, left shift a 1 in
				rem = diff;		// Set the remainder as the difference
				quotient = quotient.leftShift(1, true);	
			}
			divisor = divisor.rightShift();	// Right shift the divisor by one
			i++;	// Increment iterator
		}
		quotient = quotient.removePadding();		// Get rid of the leading bytes on the quotient
		rem = rem.removePadding();					// Get rid of the leading bytes on the remainder
		if (sign && !quotient.isZero()) {	// If the quotient is supposed to be negative, make it negative
			quotient = quotient.negate();
		}
		if (negRem && !rem.isZero()) {		// If the remainder is supposed to be negative, make it negative
			rem = rem.negate();
		}
		return new LargeInteger[] {quotient, rem};	// Returns an array of {quotient, remainder}	(dynamic programming solution)
	}

	public int compareTo(LargeInteger other) {
		LargeInteger first = this.removePadding();		// Stores the first LargeInteger (this one) without leading bytes
		LargeInteger second = other.removePadding();	// Stores the second LargeInteger (arg) without leading bytes
		byte[] firstArr = first.getVal();		// Gets the val array for first LargeInteger
		byte[] secondArr = second.getVal();		// Gets the val array for second LargeInteger
		int sign = 1;	// If first number is negative, set to -1. If not, set to 1 (no change)

		// Easy edge cases for if one number is negative and the other is positive (negative num < positive num)
		if (!first.isNegative() && second.isNegative()) {
			return 1;
		}
		if (first.isNegative() && !second.isNegative()) {
			return -1;
		}
		if (first.isNegative()) {	// Set to -1 if first number is negative
			sign = -1;
		}

		// Edge cases for if the length of the LargeIntegers are different (longer num > shorter num) accounting number sign (+ or -)
		if (first.length() > second.length()) {
			return 1 * sign;
		}
		if (first.length() < second.length()) {
			return -1 * sign;
		}
		if (first.isNegative()) {					// If the first LargeInteger is negative,
			firstArr = first.negate().getVal();		// get the byte arrays for first and second and negate them
			secondArr = first.negate().getVal();
		}
		for (int i = 0; i < firstArr.length; i++) {	// Loop through the first LargeInteger byte array
			int curFirst = firstArr[i] & 0xFF;		// Convert first to unsigned
			int curSecond = secondArr[i] & 0xFF;	// Convert second to unsigned
			
			// Compare the two unsigned to see which is greater
			if (curFirst < curSecond) {
				return -1 * sign;
			}
			else if (curFirst > curSecond) {
				return 1 * sign;
			}
		}
		return 0;	// Else, if none of those, ans will remain 0 because they are equal
	}
}