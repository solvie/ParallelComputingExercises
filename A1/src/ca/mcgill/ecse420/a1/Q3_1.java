package ca.mcgill.ecse420.a1;

public class Q3_1{
    /**
     * We can make a deadlock more likely to occur by setting a higher value for chopstickPickupTime (for example 200),
     * and less likely to occur by setting it lower (50).
     */

    public static final double eatTime=400, waitTime=100, baseThinkTime=200,thinkTime=400, chopstickPickupTime=50;
    public static final int numPhilosphers = 5;
    public static Thread[] philosophers;  //Thread[i] holds philosopher sitting at index i on the table.
    public static int[] chopsticks;   //int[i] is 1 if chopstick at index i is on the table, 0 if it is not on the table.

    class Philosopher implements Runnable{
        int philospherSeatIndex, rightChopstickIndex, leftChopstickIndex;
        boolean hasRightChopstick, hasLeftChopstick;

        /**
         * @param philospherSeatIndex refers to the philosopher's position in the circular array. This determines which chopsticks s/he has access to.
         */
        public Philosopher(int philospherSeatIndex){
            this.philospherSeatIndex = philospherSeatIndex;
            this.leftChopstickIndex = (philospherSeatIndex+1)%numPhilosphers; //wraps around.
            this.rightChopstickIndex = philospherSeatIndex;
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
            long timeStartedTryingEating = System.currentTimeMillis();
            while (true) {
                //Keep trying to pick up whatever chopstick not in hand, until both are acquired.
                if (!hasLeftChopstick) hasLeftChopstick =trypickUpChopstick(leftChopstickIndex);
                if (!hasRightChopstick) hasRightChopstick = trypickUpChopstick(rightChopstickIndex);
                if (hasRightChopstick&&hasLeftChopstick)break;
                double randomizedWaitTime = Math.random()*waitTime;
                sleep(randomizedWaitTime); //wait a little bit before trying again.
            }
            System.out.println(String.format("Philosopher "+ philospherSeatIndex+" eating after waiting %dms....................\\/(^^)%d>", System.currentTimeMillis()-timeStartedTryingEating, philospherSeatIndex));
            sleep(eatTime);
            putDownChopsticks();
        }

        /**
         *  Thinks for a random amount of time with a cap at thinkTime.
         */
        private void think(){
            System.out.println("Philosopher "+ philospherSeatIndex+" thinking.");
            double randomizedThinktime = baseThinkTime+ Math.random()*thinkTime;
            sleep(randomizedThinktime);
        }

        /**
         * Takes a small set amount of time to pick up a chopstick.
         *
         * @param i index of chopstick to pick up
         * @return
         */
        private synchronized boolean trypickUpChopstick(int i){
            if (chopsticks[i] == 1) {
                chopsticks[i] = 0;
                sleep(chopstickPickupTime);
                System.out.println("Philosopher "+ philospherSeatIndex+" picked up chopstick "+i);
                return true;
            }
//            System.out.println("Philosopher "+ philospherSeatIndex+" failed to pick up chopstick "+i);
            return false;
        }

        private void putDownChopsticks(){
            chopsticks[leftChopstickIndex]=1;
            chopsticks[rightChopstickIndex]=1;
            hasLeftChopstick=false;
            hasRightChopstick = false;
            System.out.println("Philosopher "+ philospherSeatIndex+" put down chopsticks");
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
        new Q3_1();
    }

    public Q3_1(){
        this.chopsticks = new int[numPhilosphers];
        this.philosophers = new Thread[numPhilosphers];
        initChopsticks();
        initPhilosophersAndRun();
    }

    public void initChopsticks(){
        for (int i=0; i<numPhilosphers; i++)
            this.chopsticks[i] = 1;
    }

    public void initPhilosophersAndRun(){
        for (int i=0; i<numPhilosphers; i++) {
            this.philosophers[i] = new Thread(new Philosopher(i));
            philosophers[i].start();
        }
    }
}