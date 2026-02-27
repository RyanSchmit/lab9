package org.example;


public class Main {
    public static void main(String[] args) {
        final String STOP = "__STOP__";

        StageQueue valq = new StageQueue();
        StageQueue solverq = new StageQueue();
        StageQueue agq = new StageQueue();



        Generator generator = new Generator();
        Solver solver = new Solver(solverq);

    }
}