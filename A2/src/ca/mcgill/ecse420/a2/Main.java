package ca.mcgill.ecse420.a2;

public class Main {
    private static Account account = new Account();
    private static final int nThreads = 20;

    public static void main(String[] args) throws InterruptedException {

        Thread[] pool = new Thread[nThreads];

        for (int i = 0; i < nThreads; i++)
            pool[i] = new Thread(new AddAPennyTaskFilter());
        for (int i = 0; i < nThreads; i++)
            pool[i].start();
        for (int i = 0; i < nThreads; i++)
            pool[i].join();

        System.out.println("FilterLock with "+nThreads+" AddAPennyTasks");
        System.out.println("What is balance ? " + account.getBalance());

        account.resetBalance();
        ThreadID.reset();

        for (int i = 0; i < nThreads; i++)
            pool[i] = new Thread(new AddAPennyTaskBakery());
        for (int i = 0; i < nThreads; i++)
            pool[i].start();
        for (int i = 0; i < nThreads; i++)
            pool[i].join();

        System.out.println("BakeryLock with "+nThreads+" AddAPennyTasks");
        System.out.println("What is balance ? " + account.getBalance());

    }

    // A thread for adding a penny to the account
    public static class AddAPennyTaskFilter implements Runnable {
        public void run() {
            account.depositWithFilterLock(1);
        }
    }

    // A thread for adding a penny to the account
    public static class AddAPennyTaskBakery implements Runnable {
        public void run() {
            account.depositWithBakeryLock(1);
        }
    }

    /**
     * Inner class for Account -
     * Modified from from: http://www.cs.armstrong.edu/liang/intro11e/html/AccountWithSyncUsingLock.html
     */
    public static class Account {
        private static FilterLock filterLock = new FilterLock(nThreads); // Create a lock
        private static BakeryLock bakeryLock = new BakeryLock(nThreads); // Create a lock

        private volatile int balance = 0;

        public void resetBalance(){ balance = 0;}
        public int getBalance() {
            return balance;
        }

        public void depositWithFilterLock(int amount) {
            filterLock.lock(); // Acquire the lock
            try {
                depositAction(amount);
            } catch (InterruptedException ex) {
                System.err.println("Interrupted Exception Occurred!");
            } finally {
                filterLock.unlock(); // Release the lock
            }
        }

        public void depositWithBakeryLock(int amount) {
            bakeryLock.lock(); // Acquire the lock
            try {
                depositAction(amount);
            } catch (InterruptedException ex) {
                System.err.println("Interrupted Exception Occurred!");
            } finally {
                bakeryLock.unlock(); // Release the lock
            }
        }

        private void depositAction(int amount) throws InterruptedException{
            int newBalance = balance + amount;
            // This delay is deliberately added to magnify the
            // data-corruption problem and make it easy to see.
            Thread.sleep(5);
            balance = newBalance;
        }
    }
}
