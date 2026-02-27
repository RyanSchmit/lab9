package org.example;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class StageQueue<T> {
    private final Deque<T> q = new ArrayDeque<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    public void put(T item) {
        lock.lock();
        try {
            q.addLast(item);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (q.isEmpty()) {
                notEmpty.await();
            }
            return q.removeFirst();
        } finally {
            lock.unlock();
        }
    }
}