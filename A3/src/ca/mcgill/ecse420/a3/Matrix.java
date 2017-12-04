package ca.mcgill.ecse420.a3;

/**
 * Created by solvie on 2017-12-03.
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

    private Matrix(double[][] matrix, int x, int y, int d){
        data = matrix;
        rowDisplace = x;
        colDisplace = y;
        dim = d;
    }

    public double get(int row, int col){
        return data[row+rowDisplace][col+colDisplace];
    }


    public void set(int row, int col, double value){
        data[row+rowDisplace][col+colDisplace] = value;
    }

    public int getDim(){
        return dim;
    }
    public double[][] getData(){
        return data;
    }

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
                m[i][j] = 1;//todo;
        return m;
    }


}