package org.example;

public class SolverWorker implements Runnable {
    private final StageQueue<Job> in;
    private final StageQueue<SolvedJob> out;

    public SolverWorker(StageQueue<Job> in, StageQueue<SolvedJob> out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Job job = in.take();

                if (job.isStop()) {
                    // forward STOP to aggregator (one per solver)
                    out.put(SolvedJob.STOP);
                    return;
                }

                try {
                    int result = compute(job);
                    out.put(new SolvedJob(job, result, true, null, System.nanoTime()));
                } catch (ArithmeticException ex) {
                    // should be rare because validator filters div/mod by 0
                    out.put(new SolvedJob(job, null, false, ex.getMessage(), System.nanoTime()));
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private int compute(Job j) {
        switch (j.op) {
            case ADD: return j.a + j.b;
            case SUB: return j.a - j.b;
            case MUL: return j.a * j.b;
            case DIV: return j.a / j.b;
            case MOD: return j.a % j.b;
            default: throw new IllegalArgumentException("Unknown op");
        }
    }
}