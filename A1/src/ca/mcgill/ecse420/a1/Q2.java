package ca.mcgill.ecse420.a1;

public class Q2 {
    
    private static Object Lock1 = new Object();
    private static Object Lock2 = new Object();
    
    
    public static void main(String[] args) {
        System.out.println("Hello World, Welcome to Q2!");
        
        Thread t1 = new Thread(new Task1());
        Thread t2 = new Thread(new Task2());
        t1.start();
        t2.start();
    }
    
    /**
     * Task1 will acquire Lock1 and wait; while Task2 will acquire Lock2, Task1 will be
     * done sleeping and will try to acquire Lock2 but will be blocked since Task2 has the Lock2.
     * @author Steven
     *
     */
    static class Task1 implements Runnable {

        @Override
        public void run() {
            System.out.println("T1: waiting for Lock1");
            synchronized(Lock1) {
                System.out.println("T1: acquired Lock1");
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                synchronized(Lock2) {
                    System.out.println("T1: acquired Lock2");
                    System.out.println("T1: released Lock2");
                }
                
                System.out.println("T1: released Lock1");
            }
        }
    }
    
    /**
     * After Task2 has acquired Lock2, Task1 will try to acquire Lock1 but it is already acquired
     * by Task1, hence it will be blocked. Since Task1 is blocked for Lock2 and Task2 is blocked
     * for Lock1, we are facing a deadlock.
     * @author Steven
     *
     */
    static class Task2 implements Runnable {

        @Override
        public void run() {
            System.out.println("T2: waiting for Lock2");
            
            synchronized(Lock2) {
                System.out.println("T2: acquired Lock2");
                
                synchronized(Lock1) {
                    System.out.println("T2: acquired Lock1");
                    System.out.println("T2: released Lock1");
                }
                
                System.out.println("T2: released Lock2");
            }
        }
    }
}
