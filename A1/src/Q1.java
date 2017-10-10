import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Q1 {
    
    public static int NUM_THREADS = 2;

    public static void main(String[] args) {
        System.out.println("Hello world, welcome to Q1!");
        double[][] D = generateSquareMatrixOfSize(2000);
        double[][] E = generateSquareMatrixOfSize(2000);
        
        double[][] A = {{2,1,5,3},{0,7,1,6},{9,2,4,4},{3,6,7,2}};
        double[][] B = {{6,1,2,3},{4,5,6,5},{1,9,8,8},{4,0,8,5}};
        
        long startTime = System.currentTimeMillis();
        printMatrix(sequentialMultiplyMatrix(A,B));
        long endTime = System.currentTimeMillis();
        System.out.println("Sequetial runtime : " + (endTime - startTime) + "ms");

        startTime = System.currentTimeMillis();
        parallelMultiplyMatrix(D,E);
        endTime = System.currentTimeMillis();
        System.out.println("Parallel runtime : " + (endTime - startTime) + "ms, Number of threads: " + NUM_THREADS);
        
//        System.out.println("Available processors : " +Runtime.getRuntime().availableProcessors());
        
    }
    
    public static void printMatrix(double[][] matrix) {
        for(int row = 0; row < matrix.length; row++) {
            for(int column = 0; column < matrix[row].length; column++)
                System.out.print(matrix[row][column] + "  ");
            System.out.println("");
        }
    }
    
    public static double[][] generateSquareMatrixOfSize(int n) {
        double[][] m = new double[n][n];
        
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                m[i][j] = n*i + j;
        
        return m;
    }
    
    /**
     * Question 1.1
     * Computes matrix multiplication sequentially given Anxm and Bpxq,
     * we assume that m = p, hence Anxm * Bpxq = Cnxq.
     * @param a
     * @param b
     * @return  the computed matrix multiplication
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
            // TODO Auto-generated method stub
          double c = 0.0;
          
          for (int columnA = 0; columnA < a[rowA].length; columnA++)
              c+= a[rowA][columnA] * b[columnA][columnB]; /* columnA = rowB */
          
          result[rowA][columnB] = c;
        }
    }
    
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
}
