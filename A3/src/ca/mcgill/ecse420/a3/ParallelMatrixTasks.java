package ca.mcgill.ecse420.a3;

import java.util.concurrent.*;

/**
 * Class to describe parallel Matrix tasks
 */
public class ParallelMatrixTasks {
    public static int NUM_THREADS = 4;
    static ForkJoinPool pool = new ForkJoinPool(NUM_THREADS);

    /**
     * The main parallel multiplication task
     * @param a matrix
     * @param b vector
     * @return vector result
     * @throws Exception
     */
    static Vector parallelMult(Matrix a, Vector b) throws Exception{
        int n = a.getDim();
        Vector c = new Vector(n, false);
        MulTask mainTask =  new MulTask(a, b, c);
        long startTime = System.currentTimeMillis();
        pool.invoke(mainTask);
        long endTime = System.currentTimeMillis();
        System.out.println("Parallel runtime : " + (endTime - startTime) + "ms");
        return c;
    }

    /**
     * Inner class to handle recursive vector adding
     */
    static class VectorAddTask extends RecursiveAction{
        Vector a,b,c;

        public VectorAddTask(Vector a, Vector b, Vector c){
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public void compute(){
            try{
                int n = a.getDim();
                if (n==1){
                    c.set(0,a.get(0)+b.get(0));
                } else{
                    Vector[]aa = a.split(), bb= b.split(), cc = c.split();
                    invokeAll(
                            new VectorAddTask(aa[0], bb[0], cc[0]),
                            new VectorAddTask(aa[1], bb[1], cc[1])
                    );
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    /**
     * Inner class to handle recursive matrix vector multiplication
     */
    static class MulTask extends RecursiveAction{
        Matrix a;
        Vector b,c;
        Vector lhs, rhs;

        public MulTask(Matrix a,Vector b, Vector c){
            this.a=a;
            this.b=b;
            this.c=c;
            lhs = new Vector(a.getDim(), false);
            rhs = new Vector(a.getDim(), false);
        }

        public void compute(){
            try{
                if (a.getDim()==1){
                    c.set(0, a.get(0,0)*b.get(0));
                } else {
                    Matrix[][] aa = a.split();
                    Vector[] bb = b.split(), cc = c.split();
                    Vector[] ll = lhs.split(), rr = rhs.split();
                    for (int i = 0; i < 2; i++) {
                        invokeAll(
                                new MulTask(aa[i][0], bb[i], ll[i]),
                            new MulTask(aa[i][1], bb[i], rr[i]));
                    }
                    invokeAll(new VectorAddTask(lhs, rhs, c));
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
