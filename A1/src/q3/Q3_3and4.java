public class Q3_3and4{

    public static final double eatTime=300,  waitTime=100,baseThinkTime=200, thinkTime=300, chopstickPickupTime=50;
    public static int numPhilosphers;
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
            this.leftChopstickIndex = (philospherSeatIndex+1)%numPhilosphers; //wraps around.
            this.rightChopstickIndex = philospherSeatIndex;
            //if the lower numbered chopstick is in the right hand, swap chopsticks between hands so that left holds the lower one.
            if (rightChopstickIndex<leftChopstickIndex) {
                int temp = rightChopstickIndex;
                rightChopstickIndex=leftChopstickIndex;
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
                if (!hasLeftChopstick) hasLeftChopstick =trypickUpChopstick(leftChopstickIndex,true);
                if (hasLeftChopstick &&!hasRightChopstick) hasRightChopstick = trypickUpChopstick(rightChopstickIndex,false);
                if (hasRightChopstick&&hasLeftChopstick)break;
                sleep(Math.random()*waitTime);
            }

            System.out.println(String.format("Philosopher "+ philospherSeatIndex+" eating after waiting %dms....................\\/(^^)%d>", System.currentTimeMillis()-timeStartedTryingEating, philospherSeatIndex));
            sleep(eatTime);
            putDownChopsticks();
        }

        /**
         *  Thinks for a random amount of time with a cap at thinkTime+baseThinkTime.
         */
        private void think(){
            System.out.println("Philosopher "+ philospherSeatIndex+" thinking.");
            double randomizedThinktime = baseThinkTime+Math.random()*thinkTime;
            sleep(randomizedThinktime);
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
                if (!checkIfLastUsed||(checkIfLastUsed&&chopsticks[i]!=philospherSeatIndex)) {
                    chopsticks[i] = -1;
                    sleep(chopstickPickupTime);
                    System.out.println("Philosopher " + philospherSeatIndex + " picked up chopstick " + i);
                    return true;
                }
            }
//            System.out.println("Philosopher "+ philospherSeatIndex+" failed to pick up chopstick "+i);
            return false;
        }

        private void putDownChopsticks(){
            chopsticks[leftChopstickIndex]=philospherSeatIndex;
            chopsticks[rightChopstickIndex]=philospherSeatIndex;
            hasLeftChopstick=false;
            hasRightChopstick = false;
            System.out.println("Philosopher "+ philospherSeatIndex+" put down chopsticks");
            sleep(chopstickPickupTime);

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
            numPhilosphers = Integer.parseInt(args[0]);
            if (numPhilosphers<2)
                System.out.println("Please pass an integer value greater than 1 as an argument");
            else {
                System.out.println("There are " + numPhilosphers + " philosophers at the table");
                new Q3_3and4();
            }
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Please pass the number of philosophers as an argument");
        } catch (NumberFormatException e){
            System.out.println("Please pass an integer value greater than 1 as an argument");
        }
    }

    public Q3_3and4(){
        this.chopsticks = new int[numPhilosphers];
        this.philosophers = new Thread[numPhilosphers];
        initChopsticks();
        initPhilosophersAndRun();
    }

    public void initChopsticks(){
        for (int i=0; i<numPhilosphers; i++)
            this.chopsticks[i] = numPhilosphers+1; //nobody has used it at first, so set to arbitrary out of index number.
    }

    public void initPhilosophersAndRun(){
        for (int i=0; i<numPhilosphers; i++) {
            this.philosophers[i] = new Thread(new Philosopher(i));
            philosophers[i].start();
        }
    }
}