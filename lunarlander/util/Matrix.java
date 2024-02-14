package lunarlander.util;
/**
 * An immutable 2x2 matrix
 * 
 * @author Michael Yu
 */
public class Matrix {

    /**
     * Construct a new matrix with the given values
     * 
     * @param m11 is the value for row 1, column 1
     * @param m12 is the value for row 1, column 2
     * @param m21 is the value for row 2, column 1
     * @param m22 is the value for row 2, column 2
     */
    public Matrix(double m11, double m12, double m21, double m22) {
        M = new double[][] { { m11, m12 }, { m21, m22 } };
    }

    /**
     * Get the value in row m and column n (with 1 <= m <= 2 and 1 <= n <= 2)
     * 
     * @param m is the row index
     * @param n is the column index
     * @return the value in row m and column n
     */
    public double get(int m, int n) {
        return M[m - 1][n - 1];
    }

    /**
     * Right-multiply this matrix by A
     * 
     * @param A is a matrix
     * @return a new matrix that is the product of this and A
     */
    public Matrix multiply(Matrix A) {
        double m11 = M[0][0] * A.get(1, 1) + M[0][1] * A.get(2, 1);
        double m12 = M[0][0] * A.get(1, 2) + M[0][1] * A.get(2, 2);
        double m21 = M[1][0] * A.get(1, 1) + M[1][1] * A.get(2, 1);
        double m22 = M[1][0] * A.get(1, 2) + M[1][1] * A.get(2, 2);
        return new Matrix(m11, m12, m21, m22);
    }

    /**
     * Multpily the vector v by this matrix
     * 
     * @param v is a vector
     * @return a new vector that is the product of this matrix * v
     */
    public Vect2D times(Vect2D v) {
        double x = M[0][0] * v.getX() + M[0][1] * v.getY();
        double y = M[1][0] * v.getX() + M[1][1] * v.getY();
        return new Vect2D(x, y);
    }


    private double[][] M; // The matrix is represented interanally as a 2d array
}