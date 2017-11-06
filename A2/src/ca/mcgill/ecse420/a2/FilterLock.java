package ca.mcgill.ecse420.a2;

/**
 * An implementation of a Filter lock
 *
 */
public class FilterLock {
    private static int numThreads;
    private volatile int[] level;
    private volatile int[] victim;

    public FilterLock(int n){
        numThreads = n;
        level = new int[numThreads];
        victim = new int[numThreads];
        for (int i = 0; i< numThreads; i++){
            level[i] = 0;
            victim[i]= 0;
        }
    }

    public void lock(){
        int me = ThreadID.get();
        for (int i = 1; i< numThreads; i++) {
            level[me]= i;
            victim[i]= me;
            while (existsThreadAtSameOrHigherLevel(me, i) && victim[i] == me);

        }
    }

    /**
     * If there is a thread that is waiting at the same or a higher level of the filter, this method returns true
     * false otherwise.
     *
     * @param me threadID of the thread that is attempting to lock
     * @param myLevel level that the thread is waiting at
     * @return
     */
    private boolean existsThreadAtSameOrHigherLevel(int me, int myLevel) {
        for (int threadId = 0; threadId < numThreads; threadId++)
            if (threadId != me && level[threadId] >= myLevel)
                return true;
        return false;
    }

    public void unlock(){
        int me=ThreadID.get();
        level[me]=0;
    }
}
