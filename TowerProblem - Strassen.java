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
	Strassen strassen;

	Calculations(int h1[]) {

		// Objects for BigInteger processing
		
		zero = new BigInteger("0");	
		one = new BigInteger("1");
		two = new BigInteger("2");
		strassen = new Strassen();

		long tempmod = (long) Math.pow((double) 10, (double) 9) + 7;
		mod = new BigInteger(Long.toString(tempmod));
		lmod = 1000000007;

		f = new int[16];
		h = new int[16];
		m = new long[16][16];
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
		// the extra row and column for Strassens.
		for(int i=0;i<16;i++)
			{
				m[15][i]=0;
				m[i][15]=0;
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
		for (int i = 0; i < m[0].length-1; i++)
			result = result.add(BigInteger.valueOf(m[14][i] * f[i + 1])); // Since value can go outside the range of long

		result = result.multiply(two);

		return result.mod(mod);
	}

	// exponentiation by squaring
	
	long[][] expMatrix(long m[][], BigInteger n) {

		if (n.compareTo(one) == 0)
			return m;
		else {

			BigInteger temp = n.mod(two);
			if (temp.compareTo(zero) == 0)
				return expMatrix(strassen.multiply(m, m), (n.divide(two)));
			else
				return strassen.multiply(
						m,
						expMatrix(strassen.multiply(m, m),
								((n.subtract(one).divide(two)))));
			
		}
	}

}

// Class for Strassens.
class Strassen {
	BigInteger mod;
	BigInteger temp;
	
	Strassen(){
		long tempmod = (long) Math.pow((double) 10, (double) 9) + 7;
		mod = new BigInteger(Long.toString(tempmod));
	}
	
	public long[][] multiply(long[][] A, long[][] B) {
		int n = A.length;
		
		long[][] R = new long[n][n];
		
		if (n == 1) {						
	
			temp=new BigInteger(Long.toString(A[0][0]*B[0][0]));
			temp=temp.mod(mod);
			
			if((A[0][0]*B[0][0])<0) // Since mod of negative values is not required
			R[0][0]=A[0][0]*B[0][0];
	
			else
				R[0][0]=temp.longValue();
		} else {
			long[][] A11 = new long[n / 2][n / 2];
			long[][] A12 = new long[n / 2][n / 2];
			long[][] A21 = new long[n / 2][n / 2];
			long[][] A22 = new long[n / 2][n / 2];
			long[][] B11 = new long[n / 2][n / 2];
			long[][] B12 = new long[n / 2][n / 2];
			long[][] B21 = new long[n / 2][n / 2];
			long[][] B22 = new long[n / 2][n / 2];

			split(A, A11, 0, 0);
			split(A, A12, 0, n / 2);
			split(A, A21, n / 2, 0);
			split(A, A22, n / 2, n / 2);
			
			split(B, B11, 0, 0);
			split(B, B12, 0, n / 2);
			split(B, B21, n / 2, 0);
			split(B, B22, n / 2, n / 2);
			
			long[][] M1 = multiply(add(A11, A22), add(B11, B22));
			long[][] M2 = multiply(add(A21, A22), B11);
			long[][] M3 = multiply(A11, sub(B12, B22));
			long[][] M4 = multiply(A22, sub(B21, B11));
			long[][] M5 = multiply(add(A11, A12), B22);
			long[][] M6 = multiply(sub(A21, A11), add(B11, B12));
			long[][] M7 = multiply(sub(A12, A22), add(B21, B22));

			long[][] C11 = add(sub(add(M1, M4), M5), M7);
			long[][] C12 = add(M3, M5);
			long[][] C21 = add(M2, M4);
			long[][] C22 = add(sub(add(M1, M3), M2), M6);

			join(C11, R, 0, 0);
			join(C12, R, 0, n / 2);
			join(C21, R, n / 2, 0);
			join(C22, R, n / 2, n / 2);
		}
		return R;
	}

	public long[][] sub(long[][] A, long[][] B) {
		int n = A.length;
		long[][] C = new long[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++){
				
				// To avoid of range values
				temp=new BigInteger(Long.toString(A[i][j] - B[i][j]));
				temp=temp.mod(mod);
	
				if((A[i][j] + B[i][j])<0) 
					C[i][j]=A[i][j] + B[i][j];
				else
					C[i][j]=temp.longValue();
			}
		return C;
	}

	public long[][] add(long[][] A, long[][] B) {
		int n = A.length;
		long[][] C = new long[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++){

				// to avoid out of range values.
				temp=new BigInteger(Long.toString(A[i][j] + B[i][j]));
				temp=temp.mod(mod);
	
				if((A[i][j] + B[i][j])<0)
					C[i][j]=A[i][j] + B[i][j];
				else
					C[i][j]=temp.longValue();
			}
		return C;
	}

	public void split(long[][] P, long[][] C, int iB, int jB) {
		for (int i1 = 0, i2 = iB; i1 < C.length; i1++, i2++)
			for (int j1 = 0, j2 = jB; j1 < C.length; j1++, j2++)
				C[i1][j1] = P[i2][j2];
	}

	public void join(long[][] C, long[][] P, int iB, int jB) {
		for (int i1 = 0, i2 = iB; i1 < C.length; i1++, i2++)
			for (int j1 = 0, j2 = jB; j1 < C.length; j1++, j2++)
				P[i2][j2] = C[i1][j1];
	}

}