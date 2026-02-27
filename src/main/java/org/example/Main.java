package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final int N = 100_000;  // change as needed
        final int K = 4;        // run manually with K=1 and K=4

        System.out.println("Starting pipeline...");
        System.out.println("Jobs (N): " + N);
        System.out.println("Solver threads (K): " + K);

        StageQueue<Job> q1 = new StageQueue<>();        // Generator -> Validator
        StageQueue<Job> q2 = new StageQueue<>();        // Validator -> Solvers
        StageQueue<SolvedJob> q3 = new StageQueue<>();  // Validator/Solvers -> Aggregator

        Thread generatorThread = new Thread(new Generator(N, q1), "Generator");
        Thread validatorThread = new Thread(new Validator(q1, q2, q3, K), "Validator");
        Thread aggregatorThread = new Thread(new Aggregator(q3, K, N), "Aggregator");

        Thread[] solverThreads = new Thread[K];
        for (int i = 0; i < K; i++) {
            solverThreads[i] = new Thread(new SolverWorker(q2, q3), "Solver-" + i);
        }

        long startMs = System.currentTimeMillis();

        // Start threads
        aggregatorThread.start();
        for (Thread t : solverThreads) t.start();
        validatorThread.start();
        generatorThread.start();

        // Wait
        generatorThread.join();
        validatorThread.join();
        for (Thread t : solverThreads) t.join();
        aggregatorThread.join();

        long endMs = System.currentTimeMillis();
        System.out.println("\nPipeline completed.");
        System.out.println("Total pipeline runtime: " + (endMs - startMs) + " ms");
    }
}