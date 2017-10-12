package ca.mcgill.ecse420.a1;

public class Q3_2{
    
    /**
     * Deadlock will not will never occur even if CHOPSTICK_PICKUP_TIME is high because
     */

    public static final double EAT_TIME=400, WAIT_TIME=100, BASE_THINK_TIME=200,THINK_TIME=400, CHOPSTICK_PICKUP_TIME=50;
    public static final int NUM_PHILOSOPHERS = 5;
    public static Thread[] philosophers;  //Thread[i] holds philosopher sitting at index i on the table.
    public static int[] chopsticks;   //int[i] is 1 if chopstick at index i is on the table, 0 if it is not on the table.
    
    public static final int CHOPSTICK_DOWN = 1;
    public static final int CHOPSTICK_UP = 0;

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
            if (rightChopstickIndex<leftChopstickIndex) {
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
                if (!hasLeftChopstick) hasLeftChopstick = trypickUpChopstick(leftChopstickIndex);
                if (hasLeftChopstick && !hasRightChopstick) hasRightChopstick = trypickUpChopstick(rightChopstickIndex);
                if (hasRightChopstick && hasLeftChopstick)
                    break;
                sleep(Math.random() * WAIT_TIME);
            }

            System.out.println(String.format("Philosopher "+ philospherSeatIndex+" eating after waiting %dms....................\\/(^^)%d>", System.currentTimeMillis()-timeStartedTryingEating, philospherSeatIndex));
            sleep(EAT_TIME);
            putDownChopsticks();
        }

        /**
         *  Thinks for a random amount of time with a cap at THINK_TIME + BASE_THINK_TIME.
         */
        private void think(){
            System.out.println("Philosopher "+ philospherSeatIndex+" thinking.");
            double randomizedThinkTime = BASE_THINK_TIME + Math.random()*THINK_TIME;
            sleep(randomizedThinkTime);
        }

        /**
         * Takes a small set amount of time to pick up a chopstick.
         *
         * @param i index of chopstick to pick up
         * @return
         */
        private synchronized boolean trypickUpChopstick(int i){
            if (chopsticks[i] == CHOPSTICK_DOWN) {
                chopsticks[i] = CHOPSTICK_UP;
                sleep(CHOPSTICK_PICKUP_TIME);
                System.out.println("Philosopher "+ philospherSeatIndex+" picked up chopstick "+i);
                return true;
            }
            return false;
        }

        private void putDownChopsticks(){
            chopsticks[leftChopstickIndex] = CHOPSTICK_DOWN;
            chopsticks[rightChopstickIndex] = CHOPSTICK_DOWN;
            hasLeftChopstick = false;
            hasRightChopstick = false;
            System.out.println("Philosopher "+ philospherSeatIndex + " put down chopsticks");
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
        new Q3_2();
    }

    public Q3_2(){
        this.chopsticks = new int[NUM_PHILOSOPHERS];
        this.philosophers = new Thread[NUM_PHILOSOPHERS];
        initChopsticks();
        initPhilosophersAndRun();
    }

    public void initChopsticks(){
        for (int i = 0; i < NUM_PHILOSOPHERS; i++)
            this.chopsticks[i] = 1;
    }

    public void initPhilosophersAndRun(){
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            this.philosophers[i] = new Thread(new Philosopher(i));
            philosophers[i].start();
        }
    }
}