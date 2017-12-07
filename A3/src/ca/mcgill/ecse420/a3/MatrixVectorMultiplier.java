package ca.mcgill.ecse420.a3;

/**
 * Main class to house a Matrix Vector multiplication task
 */
public class MatrixVectorMultiplier {
    public static void main(String[] args) throws Exception{
        System.out.println("Hello world, welcome to Matrix Vector Multiplier!");
        System.out.println("Available processors : " +Runtime.getRuntime().availableProcessors());

        //Initialize Vector and Matrix
        Vector a = new Vector(2048, true);
        System.out.println("");
        Matrix b = new Matrix(2048, true);
        System.out.println("");
        Vector c = new Vector(16, false);

        //Multiply in parallel
        c= ParallelMatrixTasks.parallelMult(b,a);

        //Multiply in sequential
        long startTime = System.currentTimeMillis();
        double[] result= sequentialMultiplyMatrixVector(b.getData(), a.getData());
        long endTime = System.currentTimeMillis();

        //Print sequential runtime (parallel runtime prints in ParallelMatrixTasks class)
        System.out.println("Sequential runtime : " + (endTime - startTime) + "ms");
    }


    /**
     * Sequentially multiply matrix with vector. The matrix and vector are assumed to be the same size.
     *
     * @param m Matrix
     * @param v Vector
     * @return
     */
    public static double[] sequentialMultiplyMatrixVector(double[][] m, double[] v) {
        double[] result = new double[v.length];
        for(int rowM = 0; rowM < v.length; rowM++) {
            double c = 0.0;
            for (int columnM = 0; columnM < v.length; columnM++)
                c+= m[rowM][columnM] * v[columnM]; /* columnA = rowB */
            result[rowM] = c;
        }
        return result;
    }


    /**
     * Prints the matrix into console.
     * @param m
     */
    public static void printMatrix(Matrix m) {
        double[][]matrix = m.getData();
        for(int row = 0; row < matrix.length; row++) {
            for(int column = 0; column < matrix[row].length; column++)
                System.out.print(matrix[row][column] + "  ");
            System.out.println("");
        }
    }

    /**
     * Prints the vector into console.
     * @param v
     */
    public static void printVector(Vector v) {
        double[] vector = v.getData();
        for(int row = 0; row < vector.length; row++) {
            System.out.print(vector[row] + "  ");
        }
        System.out.println("");
    }



}

