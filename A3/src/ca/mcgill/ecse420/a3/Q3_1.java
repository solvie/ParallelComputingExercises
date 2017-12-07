package ca.mcgill.ecse420.a3;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import ca.mcgill.ecse420.a3.Q2.Node;

public class Q3_1 {

    public static void main(String[] args) {
        BoundedQueue<Integer> q = new BoundedQueue<>(10);
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                int x = q.deq();
                System.out.println("Was sleeping, now got it: "+x);
            }
            
        }).start();

        q.enq(1);        
        
    }
    
    public static class BoundedQueue<T> {
        private ReentrantLock headLock, tailLock;
        private Condition notFullCondition, notEmptyCondition;

        
        private int head = 0;
        private int tail = 0;
        
        private T[] items;
        
        /**
         * Initialize the bounded queue with capacity
         * @param capacity
         */
        public BoundedQueue(int capacity) {
            headLock = new ReentrantLock();
            tailLock = new ReentrantLock();
            notFullCondition = tailLock.newCondition();
            notEmptyCondition = headLock.newCondition();
            this.items = (T[]) new Object[capacity];
        }
        
        /**
         * Enqueue an element in the queue, by moving the tail by one. 
         * Sleep if queue is full.
         * Signal if queue is not empty
         * @param elem
         */
        public void enq(T elem) {
            tailLock.lock();
            try {
                while(tail - head == items.length) { // queue is full
                    try {
                       notFullCondition.await();
                    } catch (InterruptedException e) {
                       
                    }
                }
                items[tail % items.length] = elem;
                tail++;
                
                if (tail - head == 1) { // queue not empty, let others know
                    headLock.lock();
                    try {
                        notEmptyCondition.signal();
                    } finally {
                        headLock.unlock();
                    }
                }
            } finally {
                tailLock.unlock();
            }
        }
        
        /**
         * Dequeue an element in the queue, by moving the head by one.
         * Sleep is the queue is empty.
         * Signal is the queue is not full
         * @return
         */
        public T deq() {
            headLock.lock();
            try {
                while(tail - head == 0) { //queue is empty
                    try {
                        notEmptyCondition.await();
                    } catch (InterruptedException e) {
                        
                    }
                }
                T elem = items[head % items.length];
                head++;
                if (tail - head == items.length - 1) { //queue not full, let others know
                    tailLock.lock();
                    try {
                        notFullCondition.signal();
                    } finally {
                        tailLock.unlock();
                    }
                }
                return elem; 
            } finally {
                headLock.unlock();
            }
        }

    }
}
