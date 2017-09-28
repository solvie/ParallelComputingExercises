
public class Q1 {

    public static void main(String[] args) {
        System.out.println("Hello world, welcome to Q1!");
        double[][] D = generateSquareMatrixOfSize(2000);
        double[][] E = generateSquareMatrixOfSize(2000);
        
        long startTime = System.currentTimeMillis();
        sequentialMultiplyMatrix(D,E);
        long endTime = System.currentTimeMillis();
        System.out.println("Sequetial runtime : " + (endTime - startTime));
        
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
    
    public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
        return new double[1][1];
    }
}
