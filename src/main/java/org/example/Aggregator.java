package org.example;

import java.util.EnumMap;

public class Aggregator implements Runnable {
    private final StageQueue<SolvedJob> in;
    private final int solverCount;
    private final int totalGenerated;

    private int totalSolved = 0;
    private int totalRejected = 0;

    // counts among SOLVED jobs
    private final EnumMap<Op, Integer> opCounts = new EnumMap<>(Op.class);

    private Integer minResult = null;
    private Integer maxResult = null;

    private long totalLatencyNsAllCompleted = 0; // solved + rejected (completed jobs)

    public Aggregator(StageQueue<SolvedJob> in, int solverCount, int totalGenerated) {
        this.in = in;
        this.solverCount = solverCount;
        this.totalGenerated = totalGenerated;
        for (Op op : Op.values()) opCounts.put(op, 0);
    }

    @Override
    public void run() {
        int stopsSeen = 0;

        try {
            while (stopsSeen < solverCount) {
                SolvedJob sj = in.take();

                if (sj.isStop()) {
                    stopsSeen++;
                    continue;
                }

                long latency = sj.solvedAt - sj.job.createdAt;
                totalLatencyNsAllCompleted += latency;

                if (sj.valid) {
                    totalSolved++;
                    opCounts.put(sj.job.op, opCounts.get(sj.job.op) + 1);

                    int r = sj.result;
                    minResult = (minResult == null) ? r : Math.min(minResult, r);
                    maxResult = (maxResult == null) ? r : Math.max(maxResult, r);
                } else {
                    totalRejected++;
                }
            }

            printReport();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void printReport() {
        int completed = totalSolved + totalRejected;

        double avgLatencyNs = completed == 0 ? 0 : (double) totalLatencyNsAllCompleted / completed;
        double avgLatencyMs = avgLatencyNs / 1_000_000.0;

        System.out.println("Total generated: " + totalGenerated);
        System.out.println("Total solved:    " + totalSolved);
        System.out.println("Total rejected:  " + totalRejected);
        System.out.println("Total completed: " + completed);

        System.out.printf("Average latency per completed job: %.0f ns (%.3f ms)%n",
                avgLatencyNs, avgLatencyMs);

        System.out.println("Operator counts (solved only): " + opCounts);
        System.out.println("Min result (solved only): " + (minResult == null ? "N/A" : minResult));
        System.out.println("Max result (solved only): " + (maxResult == null ? "N/A" : maxResult));
    }
}