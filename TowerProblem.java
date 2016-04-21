import java.util.*;
import java.math.*;

class TowerProblem {
	public static void main(String args[]) {

		Scanner o = new Scanner(System.in);

		BigInteger n;
		BigInteger fifteen = new BigInteger("15");
		BigInteger result = new BigInteger("0");
		int numberOfBrickHeights = 0;

		n = o.nextBigInteger();
		numberOfBrickHeights = o.nextInt();

		int h[] = new int[15];

		for (int i = 0; i < numberOfBrickHeights; i++)
			h[o.nextInt() - 1] = 1;		// Initialize height array

		Calculations c = new Calculations(h);		// class Calculation contains all the functions needed for our implementation

		c.initiateVector(15);	// Initialize vector V

		if (n.compareTo(fifteen) > 0) {

			result = c.calculatResult(c.expMatrix(c.m, n.subtract(fifteen)));	// expMatrix calculates M raised to N-15
		} else {
			result = BigInteger.valueOf(2 * c.f[n.intValue()]);					// If value is less than equal to 15 the Vector V itself gives us the result
		}

		System.out.println(result);
	}
}

class Calculations {
	int f[];
	int h[];
	long m[][], lmod;
	BigInteger zero, one, two, mod;

	Calculations(int h1[]) {

		// Objects for BigInteger processing
		
		zero = new BigInteger("0");	
		one = new BigInteger("1");
		two = new BigInteger("2");
		

		long tempmod = (long) Math.pow((double) 10, (double) 9) + 7;
		mod = new BigInteger(Long.toString(tempmod));
		lmod = 1000000007;

		f = new int[16];
		h = new int[16];
		m = new long[15][15];
		for (int i = 0; i < f.length; i++)
			f[i] = -1;
		f[0] = 1;
		h = h1;

		// Initialize matrix M with heights given
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 15; j++) {
				m[i][j] = 0;
				if (i == 14)
					m[i][j] = h[14 - j];

				if (i + 1 == j)
					m[i][j] = 1;
			}
		
	}

	// Recursive function calls for computing V
	int initiateVector(int n) {

		int temp = 0;

		if (f[n] != -1)	// stop if value already calculated
			return f[n];
		else {
			for (int i = 1; i <= n; i++) {

				temp += h[i - 1] * initiateVector(n - i);

			}
		}
		f[n] = temp;

		return f[n];
	}

	// compute result based on M and F i.e vector V
	
	BigInteger calculatResult(long m[][]) {
		
		BigInteger result = zero;
		for (int i = 0; i < m[0].length; i++)
			result = result.add(BigInteger.valueOf(m[14][i] * f[i + 1])); // Since value can go outside the range of long

		result = result.multiply(two);

		return result.mod(mod);
	}
	// Matrix Multiplication.
	long[][] multiply(long a[][], long b[][]) {
		long c[][] = new long[15][15];
		BigInteger temp;
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 15; j++)
				c[i][j] = 0;
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 15; j++){
				for (int k = 0; k < 15; k++){
						temp=new BigInteger(Long.toString(a[i][k] * b[k][j]));
						temp=temp.mod(mod);
						c[i][j]+=temp.longValue();			
				}
				c[i][j]=c[i][j]%lmod;
			}
		return c;
	}

	// exponentiation by squaring
	
	long[][] expMatrix(long m[][], BigInteger n) {

		if (n.compareTo(one) == 0)
			return m;
		else {

			BigInteger temp=n.mod(two);
			if (temp.compareTo(new BigInteger("0"))==0)
				return expMatrix(multiply(m, m), (n.divide(two)));
			else
				return multiply(m, expMatrix(multiply(m, m), ((n.subtract(new BigInteger("1")).divide(two)))));
		
		}
	}

}
