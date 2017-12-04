package ca.mcgill.ecse420.a3;

/**
 * Created by solvie on 2017-12-04.
 */
public class Vector {
    int dim;
    double[] data;
    int rowDisplace;

    public Vector(int d, boolean ones){
        dim = d;
        rowDisplace = 0;
        if (!ones){
            data = new double[d];
        }else {
            data = generateOnesVectorOfSize(d);
        }
    }

    private Vector(double[] vector, int x, int d){
        data = vector;
        rowDisplace = x;
        dim = d;
    }

    public double get(int row){
        return data[row+rowDisplace];
    }


    public void set(int row, double value){
        data[row+rowDisplace]= value;
    }

    public int getDim(){
        return dim;
    }
    public double[] getData(){
        return data;
    }

    Vector[] split(){
        Vector[] result = new Vector[2];
        int newDim = dim/2;
        result[0] = new Vector(data, rowDisplace, newDim);
        result[1] = new Vector(data, rowDisplace+newDim, newDim);
        return result;
    }

    /**
     * Generates a vector filled with ones
     * @param n length of vector
     * @return
     */
    private double[] generateOnesVectorOfSize(int n) {
        double[] v= new double[n];
        for (int i = 0; i < n; i++)
            v[i] = 1;//todo;
        return v;
    }

}
