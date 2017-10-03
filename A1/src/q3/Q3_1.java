public class Q3_1{

    public static final double eatTime=400, waitTime=100, baseThinkTime=200,thinkTime=400, chopstickPickupTime=200;
    public static final int numPhilosphers = 5;
    public static Thread[] philosophers;
    public static int[] chopsticks;

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
        public void eat(){
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
        public void think(){
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
        public boolean trypickUpChopstick(int i){
            if (chopsticks[i] == 1) {
                chopsticks[i] = 0;
                sleep(chopstickPickupTime);
                System.out.println("Philosopher "+ philospherSeatIndex+" picked up chopstick "+i);
                return true;
            }
//            System.out.println("Philosopher "+ philospherSeatIndex+" failed to pick up chopstick "+i);
            return false;
        }

        public void putDownChopsticks(){
            chopsticks[leftChopstickIndex]=1;
            chopsticks[rightChopstickIndex]=1;
            hasLeftChopstick=false;
            hasRightChopstick = false;
            System.out.println("Philosopher "+ philospherSeatIndex+" put down chopsticks");
        }

        public void sleep(double time){
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