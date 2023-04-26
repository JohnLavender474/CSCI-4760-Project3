package com.csci4760.project3;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            List<TraceRouteToken> tokens = RouteTracer.traceRoute(args[0]);
            tokens.forEach(token -> System.out.println(token + "\n"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
