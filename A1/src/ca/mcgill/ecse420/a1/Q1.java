package ca.mcgill.ecse420.a1;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Q1 {

    public static int NUM_THREADS = 8;

    public static void main(String[] args) {
        System.out.println("Hello world, welcome to Q1!");
        System.out.println("Available processors : " +Runtime.getRuntime().availableProcessors());

        //Q1.3
        //compareSequentialAndParallel2000();
        
        //Q1.4
//        compareParallelAndThreads2000(50); //runs from 1 to 50 threads on matrices 5000 by 5000

        //Q1.5
//        compareSequentialAndParallelWithMatrixSizeUpTo(4097, NUM_THREADS);

    }

    /**
     * Question 1.1
     * Computes matrix multiplication sequentially given Anxm and Bpxq,
     * we assume that m = p, hence Anxm * Bpxq = Cnxq.
     * @param a  Matrix A
     * @param b  Matrix B
     * @return  the computed matrix multiplication A*B
     */
    public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
        double[][] result = new double[a.length][b[0].length];
        for (int columnB = 0; columnB < b[0].length; columnB++) {
            for(int rowA = 0; rowA < a.length; rowA++) {
                double c = 0.0;
                for (int columnA = 0; columnA < a[rowA].length; columnA++)
                    c+= a[rowA][columnA] * b[columnA][columnB]; /* columnA = rowB */
                result[rowA][columnB] = c;
            }
        }
        return result;
    }

    /**
     * Question 1.2
     * Computes matrix multiplication in parallel using ComputeMatrixUnitTask a parallel unit
     * @param a Matrix A
     * @param b Matrix B
     * @return  Matrix mupltication A*B
     */
    public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        double[][] result = new double[a.length][b[0].length];

        for (int columnB = 0; columnB < b[0].length; columnB++) 
            for(int rowA = 0; rowA < a.length; rowA++) 
                executor.execute(new ComputeMatrixUnitTask(result,a,b,rowA,columnB));

        executor.shutdown();

        try {
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    /**
     * This is the unit task for used for parallelization of the matrix computation reading the necessary row and column to compute c(i,j) from 
     * row A and column B.
     * @author Steven
     *
     */
    public static class ComputeMatrixUnitTask implements Runnable {
        double[][] result;
        double[][] a;
        double[][] b;
        int rowA;
        int columnB;

        public ComputeMatrixUnitTask(double[][] result, double[][] a, double[][] b, int rowA, int columnB) {
            this.result = result;
            this.a = a;
            this.b = b;
            this.rowA = rowA;
            this.columnB = columnB;
        }

        @Override
        public void run() {
            double c = 0.0;

            for (int columnA = 0; columnA < a[rowA].length; columnA++)
                c+= a[rowA][columnA] * b[columnA][columnB]; /* columnA = rowB */

            result[rowA][columnB] = c;
        }
    }

    /**
     * Question 1.3
     * Compares execution time of the matrix multiplication of size 2000 by 2000
     */
    public static void compareSequentialAndParallel2000() {
        double[][] D = generateSquareMatrixOfSize(2000);
        double[][] E = generateSquareMatrixOfSize(2000);

        long startTime = System.currentTimeMillis();
        sequentialMultiplyMatrix(D,E);
        long endTime = System.currentTimeMillis();
        System.out.println("Sequetial runtime : " + (endTime - startTime) + "ms");

        startTime = System.currentTimeMillis();
        parallelMultiplyMatrix(D,E);
        endTime = System.currentTimeMillis();
        System.out.println("Parallel runtime : " + (endTime - startTime) + "ms, Number of threads: " + NUM_THREADS);
    }

    /**
     * Question 1.4
     * Runs the parallel matrix multiplication with incrementing number of threads from 1 .. n threads
     * @param n
     */
    public static void compareParallelAndThreads2000(int n) {
        double[][] D = generateSquareMatrixOfSize(2000);
        double[][] E = generateSquareMatrixOfSize(2000);
        
        for (int i = 0; i < n; i++) {
            NUM_THREADS = i+1;
            long startTime = System.currentTimeMillis();
            parallelMultiplyMatrix(D,E);
            long endTime = System.currentTimeMillis();
            System.out.println("Parallel runtime : " + (endTime - startTime) + "ms, Number of threads: " + NUM_THREADS);
        }
    }
    
    /**
     * Question 1.5
     * Varies the number of matrix size in 2^n iterations and compares the computation speed between sequential and parallel with custom number of threads
     * @param size      Size of the matrix
     * @param threads   Number of threads to be used for parallel computation
     */
    public static void compareSequentialAndParallelWithMatrixSizeUpTo(int size, int threads) {
        NUM_THREADS = threads;
        
        for (int i = 1; i < size; i*=2) {
        System.out.println("-------Matrix Size : " + i + "---------");
            double[][] D = generateSquareMatrixOfSize(i);
            double[][] E = generateSquareMatrixOfSize(i);
    
            long startTime = System.currentTimeMillis();
            sequentialMultiplyMatrix(D,E);
            long endTime = System.currentTimeMillis();
            System.out.println("Sequetial runtime : " + (endTime - startTime) + "ms");
    
            startTime = System.currentTimeMillis();
            parallelMultiplyMatrix(D,E);
            endTime = System.currentTimeMillis();
            System.out.println("Parallel runtime : " + (endTime - startTime) + "ms, Number of threads: " + NUM_THREADS);
        }
    }


    /**
     * Prints the matrix into console.
     * @param matrix
     */
    public static void printMatrix(double[][] matrix) {
        for(int row = 0; row < matrix.length; row++) {
            for(int column = 0; column < matrix[row].length; column++)
                System.out.print(matrix[row][column] + "  ");
            System.out.println("");
        }
    }

    /**
     * Generates a simple square matrix
     * @param n
     * @return
     */
    public static double[][] generateSquareMatrixOfSize(int n) {
        double[][] m = new double[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                m[i][j] = n*i + j;

        return m;
    }
}


