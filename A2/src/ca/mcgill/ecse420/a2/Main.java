package ca.mcgill.ecse420.a2;

public class Main {
    private static Account account = new Account();
    private static final int nThreads = 20;

    public static void main(String[] args) throws InterruptedException {

        Thread[] pool = new Thread[nThreads];

        for (int i = 0; i < nThreads; i++) {
            pool[i] = new Thread(new AddAPennyTask());
        }

        for (int i = 0; i < nThreads; i++) {
            pool[i].start();
        }

        for (int i = 0; i < nThreads; i++) {
            pool[i].join();
        }

        System.out.println("What is balance ? " + account.getBalance());

    }

    // A thread for adding a penny to the account
    public static class AddAPennyTask implements Runnable {
        public void run() {
            account.deposit(1);
        }
    }

    /**
     * Inner class for Account -
     * Modified from from: http://www.cs.armstrong.edu/liang/intro11e/html/AccountWithSyncUsingLock.html
     */
    public static class Account {
        private static FilterLock lock = new FilterLock(nThreads); // Create a lock
        private volatile int balance = 0;

        public int getBalance() {
            return balance;
        }

        public void deposit(int amount) {
            lock.lock(); // Acquire the lock

            try {
                int newBalance = balance + amount;
                // This delay is deliberately added to magnify the
                // data-corruption problem and make it easy to see.
                Thread.sleep(5);
                balance = newBalance;
            }
            catch (InterruptedException ex) {
                System.err.println("Interrupted Exception Occurred!");
            }
            finally {
                lock.unlock(); // Release the lock
            }
        }
    }
}
