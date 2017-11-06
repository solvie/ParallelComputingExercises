package ca.mcgill.ecse420.a2;


/**
 * An implementation of a Bakery lock
 *
 */
public class BakeryLock {
    private static int numThreads;
    private volatile boolean[] flag;
    private volatile int[] label;

    public BakeryLock(int n){
        numThreads = n;
        flag = new boolean[numThreads];
        label = new int[numThreads];
        for (int i=0; i<n; i++){
            flag[i] = false;
            label[i] = 0;
        }
    }

    public void lock(){
        int me = ThreadID.get();
        flag[me]= true;
        label[me] = findMax()+1;
        for (int k =0; k<numThreads; k++)
            while((k != me) &&
                    flag[k] &&
                        ((label[k] < label[me]) || ((label[k] == label[me]) && k < me)));//spin
    }

    /**
     * Helper method to return the maximum label
     * @return
     */
    private int findMax(){
        int maxVal = 0;
        for (int i =0; i<numThreads; i++)
            if (label[i]>maxVal)
                maxVal = label[i];
        return maxVal;
    }

    public void unlock(){
        int me = ThreadID.get();
        flag[me] = false;
    }

}
