package org.example;

import java.util.Random;

public class Generator implements Runnable {
    private final int N;
    private final StageQueue<Job> out;

    public Generator(int N, StageQueue<Job> out) {
        this.N = N;
        this.out = out;
    }

    @Override
    public void run() {
        Random r = new Random(123); // fixed seed => fair K=1 vs K=4 comparison
        Op[] ops = Op.values();

        for (long i = 0; i < N; i++) {
            int a = r.nextInt(201) - 100;   // [-100..100]
            int b = r.nextInt(201) - 100;   // [-100..100] includes 0
            Op op = ops[r.nextInt(ops.length)];
            out.put(new Job(i, a, b, op, System.nanoTime()));
        }

        out.put(Job.STOP);
    }
}