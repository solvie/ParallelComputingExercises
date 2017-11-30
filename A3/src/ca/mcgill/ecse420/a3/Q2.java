package ca.mcgill.ecse420.a3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Q2 {
    
    //Time spent to remove an element, when the element is found.
    private static final int REMOVE_TIME = 500;
    
    public static void main(String[] args) {
        FineList<String> fineList = new FineList();
        
        //Set up the 
        fineList.add("a");
        fineList.add("b");
        fineList.add("c");
        fineList.add("d");
        fineList.add("e");
        fineList.add("f");

        boolean lockFree = false; 
        //when lockFree is set to FALSE, the program sequentially consistent. Contains() will return false, 
        //it isn't able to acquire a lock on the element.
        //when lockFree is set to TRUE, we can see that the contains doesn't respect the ordering of function calls
        // and when an element is being deleted, it would still return contains as true.
        testRemoveThenContains(fineList, lockFree);

    }
    public static void testRemoveThenContains(FineList<String> fineList, boolean lockFree) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(new removeTask<String>(fineList, "d"));
        executor.execute(new removeTask<String>(fineList, "c"));
        executor.execute(new removeTask<String>(fineList, "e"));
        executor.execute(new containsTask<String>(fineList, "d", lockFree));
        executor.shutdown();
    }
    
    public static class removeTask<T> implements Runnable {
        private FineList<T> fineList;
        private T element;
        
        public removeTask(FineList<T> fineList, T element) {
            this.element = element;
            this.fineList = fineList;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            boolean removed = fineList.remove(element);
            if (removed)
                System.out.println("Removed " + element + ".");
        }
        
    }
    
    public static class containsTask<T> implements Runnable {
        private FineList<T> fineList;
        private T element;
        boolean lockFree;
        
        public containsTask(FineList<T> fineList, T element, boolean lockFree) {
            this.element = element;
            this.fineList = fineList;
            this.lockFree = lockFree;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            boolean contains = this.lockFree ? fineList.containsLockFree(element) : fineList.contains(element);
            System.out.println("The Fine List contains " +element+ ": " + contains + ".");
        }
        
    }
    
    public static class Node<T> {
        public T item;
        public int key;
        public volatile Node<T> next;
        public Lock lock = new ReentrantLock();
        
        public Node(T item) {
            this.item = item;
            this.key = item.hashCode();
        }
        public void lock() { lock.lock(); }
        public void unlock() { lock.unlock(); }
        
    }
    
    public static class FineList<T> {
        private Node head;
        
        public FineList() {

            head = new Node(Integer.MIN_VALUE);
            head.next = new Node(Integer.MAX_VALUE);
        }
        
        /**
         * Adds an item in the set provided from the lecture notes of Chapter 9.
         * @param item
         * @return
         */
        public boolean add(T item) {
            int key = item.hashCode();
            head.lock();
            Node<T> pred = head;
            try {
                Node<T> curr = pred.next;
                curr.lock();
                try {
                    while (curr.key < key) {
                        pred.unlock();
                        pred = curr;
                        curr = curr.next;
                        curr.lock();
                    }
                    if (curr.key == key) { // Cannot have duplicates
                        return false;
                    }
                    Node<T> newNode = new Node<T>(item);
                    newNode.next = curr;
                    pred.next = newNode;
                    return true;
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }
        
        /**
         * Removes an item in the set provided from the lecture notes of Chapter 9.
         * @param item
         * @return
         */
        public boolean remove(T item) {
            Node<T> pred = null, curr = null;
            int key = item.hashCode();
            head.lock();
            try {
                pred = head;
                curr = pred.next;
                curr.lock();
                try {
                    while (curr.key < key) {
                        pred.unlock();
                        pred = curr;
                        curr = curr.next;
                        curr.lock();
                    }
                    if (curr.key == key) {
                        try {
                            Thread.sleep(REMOVE_TIME);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        pred.next = curr.next;
                        return true;
                    }
                    return false;
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }
        
        /**
         * Returns true if the item is found in the list. The method is inspired from the remove function, because 
         * it essentially searches for the item within the set. Hence, we lock the element one by one and 
         * traverse the whole set until we find the item with same key. (Locking is required because we don't want to return
         * true when an item is being remove.)
         * @param item
         * @return
         */
        public boolean contains(T item) {
            Node<T> pred = null, curr = null;
            int key = item.hashCode();
            head.lock();
            try {
                pred = head;
                curr = pred.next;
                curr.lock();
                try {
                    while (curr.key < key) {
                        pred.unlock();
                        pred = curr;
                        curr = curr.next;
                        curr.lock();
                    }
                    if (curr.key == key) {
                        return true;
                    }
                    return false;
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }
        
        /**
         * Returns true if the element is found in the set. This method has been created to show that contains would
         * not work properly if there is no acquiring of lock at each element. A possible scenario is that when a remove
         * operation is being performed on an element and the contains method traverses the set and returns true 
         * on the element being removed.
         * @param item
         * @return
         */
        public boolean containsLockFree(T item) {
            Node<T> pred = null, curr = null;
            int key = item.hashCode();
            try {
                pred = head;
                curr = pred.next;
                try {
                    while (curr.key < key) {
                        pred = curr;
                        curr = curr.next;
                    }
                    if (curr.key == key) {
                        return true;
                    }
                    return false;
                } finally {}
            } finally {}
        }
    }
    

}

