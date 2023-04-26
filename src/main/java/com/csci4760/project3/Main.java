package com.csci4760.project3;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String src = scanner.nextLine();
        List<TraceRouteToken> tokens = RouteTracer.traceRoute(src);
        tokens.forEach(token -> System.out.println(token + "\n"));
    }

}
