package com.csci4760.project3;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String SAMPLE_TCP_DUMP_1 = "SampleTcpDump_1.txt";
    private static final String SAMPLE_TCP_DUMP_2 = "SampleTcpDump_2.txt";
    private static final String TCP_DUMP = "TcpDump.txt";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter file path: ");
        String src = TCP_DUMP; // scanner.nextLine();
        System.out.println();
        List<TraceRouteToken> tokens = RouteTracer.traceRoute(src);
        tokens.forEach(token -> System.out.println(token + "\n"));
    }

}
