package ca.mcgill.ecse420.a3;

import java.util.concurrent.*;


/**
 * Created by solvie on 2017-12-03.
 */
public class ParallelMatrixTasks {
    public static int NUM_THREADS = 4;
  //  static ExecutorService exec = Executors.newWorkStealingPool(NUM_THREADS);
    static ForkJoinPool pool = new ForkJoinPool(NUM_THREADS);

//    static Vector parallelAdd(Vector a, Vector b) throws ExecutionException, InterruptedException {
//        int n = a.getDim();
//        Vector c = new Vector(n, false);
//        long startTime = System.currentTimeMillis();
//    //    Future<?> future = exec.submit(new AddTask(a,b,c));
////        pool.invoke(new VectorAddTask(a,b,c));
//        future.get();
//        long endTime = System.currentTimeMillis();
//        System.out.println("Parallel runtime : " + (endTime - startTime) + "ms");
//        return c;
//    }

    static Vector parallelMult(Matrix a, Vector b) throws Exception{
        int n = a.getDim();
        Vector c = new Vector(n, false);
        MulTask mainTask =  new MulTask(a, b, c);
       // Future future = executor.submit(mainTask);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        long startTime = System.currentTimeMillis();
        forkJoinPool.invoke(mainTask);
        long endTime = System.currentTimeMillis();
        System.out.println("Parallel runtime : " + (endTime - startTime) + "ms");
        return c;
    }

    static class VectorAddTask extends RecursiveAction{
      //  Matrix a,b,c;
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
                    //Future<?>[][] future = (Future<?>[][]) new Future[2][2];
                    for (int i = 0; i < 2; i++) {
                        invokeAll(
                                new MulTask(aa[i][0], bb[i], ll[i]),
                            new MulTask(aa[i][1], bb[i], rr[i]));
                    }
//                    for(int i =0; i<2; i++)
//                        for (int j = 0; j<2; j++)
//                            for(int k =0; k<2; k++)
//                                future[i][j][k].get();
//                    Future<?> done = exec.submit(new AddTask(lhs,rhs, c));
//                    done.get();
                    invokeAll(new VectorAddTask(lhs, rhs, c));
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
