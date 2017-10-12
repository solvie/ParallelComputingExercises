package ca.mcgill.ecse420.a1;

public class Q3_3and4{

    public static final double EAT_TIME=300, WAIT_TIME=100, BASE_THINK_TIME=200, THINK_TIME=300, CHOPSTICK_PICKUP_TIME=50;
    public static int NUM_PHILOSOPHERS;
    public static Thread[] philosophers;  //Thread[i] holds philosopher sitting at index i on the table.
    public static int[] chopsticks;  //int[i] is j if chopstick at index i is on the table last used by Philosopher j, -1 if it is not on the table.

    class Philosopher implements Runnable{
        int philospherSeatIndex, rightChopstickIndex, leftChopstickIndex;
        boolean hasRightChopstick, hasLeftChopstick;

        /**
         * @param philospherSeatIndex refers to the philosopher's position in the circular array. This determines which chopsticks s/he has access to.
         */
        public Philosopher(int philospherSeatIndex){
            this.philospherSeatIndex = philospherSeatIndex;
            this.leftChopstickIndex = (philospherSeatIndex+1) % NUM_PHILOSOPHERS; //wraps around.
            this.rightChopstickIndex = philospherSeatIndex;
            //if the lower numbered chopstick is in the right hand, swap chopsticks between hands so that left holds the lower one.
            if (rightChopstickIndex < leftChopstickIndex) {
                int temp = rightChopstickIndex;
                rightChopstickIndex = leftChopstickIndex;
                leftChopstickIndex = temp;
            }
            System.out.println(
                    String.format("Philosopher %d just arrived at the table, between chopsticks %d and %d",
                            philospherSeatIndex, leftChopstickIndex, rightChopstickIndex));
        }

        /**
         * Alternates between thinking and eating. When not eating, is thinking.
         */
        public void run(){
            while (true) {
                think();
                eat();
            }
        }

        /**
         *  Picks up the two closest chopsticks and eats for a set amount of time.
         */
        private void eat(){
            System.out.println("Philosopher "+ philospherSeatIndex+" wants to eat.");
            long timeStartedTryingEating = System.currentTimeMillis();
            while (true) {
                // A philosopher must pick up the lower numbered chopstick first before picking up the higher number.
                // Also, if the lower numbered chopstick was used last by this philosopher, they must wait for someone else to use it befcre they can eat next.
                if (!hasLeftChopstick) hasLeftChopstick = trypickUpChopstick(leftChopstickIndex,true);
                if (hasLeftChopstick && !hasRightChopstick) hasRightChopstick = trypickUpChopstick(rightChopstickIndex,false);
                if (hasRightChopstick && hasLeftChopstick)
                    break;
                sleep(Math.random()*WAIT_TIME);
            }

            System.out.println(String.format("Philosopher "+ philospherSeatIndex+" eating after waiting %dms....................\\/(^^)%d>", System.currentTimeMillis()-timeStartedTryingEating, philospherSeatIndex));
            sleep(EAT_TIME);
            putDownChopsticks();
        }

        /**
         *  Thinks for a random amount of time with a cap at THINK_TIME+BASE_THINK_TIME.
         */
        private void think(){
            System.out.println("Philosopher "+ philospherSeatIndex+" thinking.");
            double randomizedThinkTime = BASE_THINK_TIME + Math.random()*THINK_TIME;
            sleep(randomizedThinkTime);
        }

        /**
         * Takes a small set amount of time to pick up a chopstick.
         * @param iindex of chopstick to pick up
         * @param checkIfLastUsed if true, only picks it up if the label is some other philosopher's.
         * @return
         */
        private synchronized boolean trypickUpChopstick(int i, boolean checkIfLastUsed){
            if (chopsticks[i] != -1) {
                //pick up the chopstick either if we don't care about last used
                //or if we do care about last used, and the last used was not me
                if ( !checkIfLastUsed || (checkIfLastUsed && chopsticks[i] != philospherSeatIndex) ) {
                    chopsticks[i] = -1;
                    sleep(CHOPSTICK_PICKUP_TIME);
                    System.out.println("Philosopher " + philospherSeatIndex + " picked up chopstick " + i);
                    return true;
                }
            }
            return false;
        }

        private void putDownChopsticks(){
            chopsticks[leftChopstickIndex] = philospherSeatIndex;
            chopsticks[rightChopstickIndex] = philospherSeatIndex;
            hasLeftChopstick = false;
            hasRightChopstick = false;
            System.out.println("Philosopher "+ philospherSeatIndex+" put down chopsticks");
            sleep(CHOPSTICK_PICKUP_TIME);

        }

        private void sleep(double time){
            try {
                Thread.sleep((long)time);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Q3- Dining Philosophers.
     */
    public static void main(String[] args){
        try {
            NUM_PHILOSOPHERS = Integer.parseInt(args[0]);
            if (NUM_PHILOSOPHERS<2)
                System.out.println("Please pass an integer value greater than 1 as an argument");
            else {
                System.out.println("There are " + NUM_PHILOSOPHERS + " philosophers at the table");
                new Q3_3and4();
            }
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Please pass the number of philosophers as an argument");
        } catch (NumberFormatException e){
            System.out.println("Please pass an integer value greater than 1 as an argument");
        }
    }

    public Q3_3and4(){
        this.chopsticks = new int[NUM_PHILOSOPHERS];
        this.philosophers = new Thread[NUM_PHILOSOPHERS];
        initChopsticks();
        initPhilosophersAndRun();
    }

    public void initChopsticks(){
        for (int i=0; i<NUM_PHILOSOPHERS; i++)
            this.chopsticks[i] = NUM_PHILOSOPHERS+1; //nobody has used it at first, so set to arbitrary out of index number.
    }

    public void initPhilosophersAndRun(){
        for (int i=0; i<NUM_PHILOSOPHERS; i++) {
            this.philosophers[i] = new Thread(new Philosopher(i));
            philosophers[i].start();
        }
    }
}