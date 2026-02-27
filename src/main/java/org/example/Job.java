package org.example;

public final class Job {
    public final long id;
    public final int a;
    public final int b;
    public final Op op;
    public final long createdAt; // System.nanoTime()

    public static final Job STOP = new Job(-1, 0, 0, Op.ADD, 0);

    public Job(long id, int a, int b, Op op, long createdAt) {
        this.id = id;
        this.a = a;
        this.b = b;
        this.op = op;
        this.createdAt = createdAt;
    }

    public boolean isStop() {
        return id == -1;
    }
}