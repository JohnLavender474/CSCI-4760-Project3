package com.csci4760.project3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter path to tcp dump: ");
        String src = scanner.nextLine();
        System.out.println();
        System.out.print("Enter output file name: ");
        String output = scanner.nextLine();
        System.out.println();
        List<TraceRouteToken> tokens = RouteTracer.traceRoute(src);
        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
        tokens.forEach(token -> {
            try {
                writer.write(token + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        writer.close();
    }

}
