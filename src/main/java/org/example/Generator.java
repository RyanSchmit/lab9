package org.example;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Generator implements Runnable {
    private final StageQueue queue;
    private int capacity;

    private final Semaphore empty = new Semaphore(capacity);
    private final Semaphore full = new Semaphore(0);

    private final Lock lock = new ReentrantLock();

    public Generator(StageQueue queue, int capacity) {
        this.queue = queue;
        this.capacity = capacity;
    }

    // Method to produce and add jobs to the repository
    public void produceJob(String equation) throws InterruptedException {
        produces(equation);
    }

    public void produces(String equation) throws InterruptedException {
        empty.acquire();
        lock.lock();
        try {
            queue.put(equation);
            //firePropertyChange("jobs", null, equation);
            //System.out.println("Produced: " + equation + " | Current jobs in repository: " + jobs.size());
        } finally {
            lock.unlock();
        }
        full.release();
    }

    @Override
    public void run() {
        // Where should equations be stored
        ArrayList<String> equations = new ArrayList<>();
        equations.add("5 * 3");
        equations.add("10 / 2");
        equations.add("5 + 13");

        try {
            while (true) {
                for (String equation : equations) {
                    produceJob(equation);
                    Thread.sleep(1000); // Simulate time taken to produce a job
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Producer was interrupted");
        }
    }

}
