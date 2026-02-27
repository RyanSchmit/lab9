package org.example;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class StageQueue {
    private final LinkedList<String> items = new LinkedList<>();

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition notEmpty = lock.newCondition();

    public void put(String equation) {
        // Implementation to add the equation to the queue
        lock.lock();
        try {
            items.add(equation);
            notEmpty.signal(); // Signal that a new item is added
        } finally {
            lock.unlock();
        }
    }

    public String take() throws InterruptedException {
        lock.lock();
        try {
            while (items.isEmpty()) {
                notEmpty.await(); // Wait until an item is available
            }
            return items.removeFirst();
        } finally {
            lock.unlock();
        }
    }
}
