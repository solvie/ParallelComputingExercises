package ca.mcgill.ecse420.a3;

/**
 * Class to describe a Matrix which can be split into smaller matrices
 */
public class Matrix{
    int dim;
    double[][] data;
    int rowDisplace, colDisplace;

    public Matrix(int d, boolean ones){
        dim = d;
        rowDisplace = colDisplace = 0;
        if (!ones){
            data = new double[d][d];
        }else {
            data = generateOnesSquareMatrixOfSize(d);
        }
    }

    /**
     * Contructor that returns a displaced matrix (as a result of splitting)
     * @param matrix data
     * @param x amount to displace rows
     * @param y amount to displace columns
     * @param d dimension
     */
    private Matrix(double[][] matrix, int x, int y, int d){
        data = matrix;
        rowDisplace = x;
        colDisplace = y;
        dim = d;
    }

    /**
     * Return data at row and column taking into account displacement
     * @param row row index
     * @param col column index
     * @return value at row and col
     */
    public double get(int row, int col){
        return data[row+rowDisplace][col+colDisplace];
    }

    /**
     * Set the value of data at row and column taking into account displacement
     * @param row row index
     * @param col column index
     * @param value value to set
     */
    public void set(int row, int col, double value){
        data[row+rowDisplace][col+colDisplace] = value;
    }

    public int getDim(){
        return dim;
    }

    public double[][] getData(){
        return data;
    }

    /**
     * Splits the matrix into four equal parts
     * @return Matrix of Matrices
     */
    Matrix[][] split(){
        Matrix[][] result = new Matrix[2][2];
        int newDim = dim/2;
        result[0][0] = new Matrix(data, rowDisplace, colDisplace, newDim);
        result[0][1] = new Matrix(data, rowDisplace, colDisplace+newDim, newDim);
        result[1][0] = new Matrix(data, rowDisplace+newDim, colDisplace, newDim);
        result[1][1] = new Matrix(data, rowDisplace+newDim, colDisplace+newDim, newDim);
        return result;
    }

    /**
     * Generates a square matrix filled with 1s
     * @param n length and width of matrix
     * @return
     */
    private double[][] generateOnesSquareMatrixOfSize(int n) {
        double[][] m = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                m[i][j] = 1;
        return m;
    }


}