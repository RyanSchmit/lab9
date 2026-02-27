package org.example;

public class Validator implements Runnable {
    private final StageQueue<Job> in;            // Generator -> Validator
    private final StageQueue<Job> outToSolvers;  // Validator -> Solvers
    private final StageQueue<SolvedJob> outToAgg;// Validator -> Aggregator
    private final int solverCount;               // K

    public Validator(StageQueue<Job> in,
                     StageQueue<Job> outToSolvers,
                     StageQueue<SolvedJob> outToAgg,
                     int solverCount) {
        this.in = in;
        this.outToSolvers = outToSolvers;
        this.outToAgg = outToAgg;
        this.solverCount = solverCount;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Job job = in.take();

                if (job.isStop()) {
                    // forward STOP to solver stage K times
                    for (int i = 0; i < solverCount; i++) {
                        outToSolvers.put(Job.STOP);
                    }
                    return;
                }

                // reject div/mod by zero
                if ((job.op == Op.DIV || job.op == Op.MOD) && job.b == 0) {
                    outToAgg.put(new SolvedJob(
                            job,
                            null,
                            false,
                            "Division/mod by zero",
                            System.nanoTime()
                    ));
                } else {
                    outToSolvers.put(job);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}