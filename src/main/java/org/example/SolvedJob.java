package org.example;

public final class SolvedJob {
    public final Job job;
    public final Integer result;  // null if rejected
    public final boolean valid;
    public final String reason;   // non-null if invalid
    public final long solvedAt;   // System.nanoTime()

    public static final SolvedJob STOP = new SolvedJob(Job.STOP, null, true, null, 0);

    public SolvedJob(Job job, Integer result, boolean valid, String reason, long solvedAt) {
        this.job = job;
        this.result = result;
        this.valid = valid;
        this.reason = reason;
        this.solvedAt = solvedAt;
    }

    public boolean isStop() {
        return job != null && job.isStop();
    }
}