package com.csci4760.project3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

public class RouteTracerTests {

    private static final String SAMPLE_TCP_DUMP_1 = "SampleTcpDump_1.txt";
    private static final String SAMPLE_TCP_DUMP_2 = "SampleTcpDump_2.txt";

    @Test
    void test1() throws Exception {
        Scanner scanner = new Scanner(new File(SAMPLE_TCP_DUMP_1));
        String line = scanner.nextLine();
        Assertions.assertTrue(line.contains(RouteTracer.SRC_STRING));
        scanner.close();
    }

    @Test
    void test2() throws Exception {
        Scanner scanner = new Scanner(new File(SAMPLE_TCP_DUMP_1));
        String line = scanner.nextLine();
        double timeStamp = Double.parseDouble(line.split(" ")[0]);
        Assertions.assertEquals(1296181912.313218, timeStamp);
        scanner.close();
    }

    @Test
    void test3() throws Exception {
        Scanner scanner = new Scanner(new File(SAMPLE_TCP_DUMP_1));
        String line = scanner.nextLine();
        int ttl = RouteTracer.fetchTTL(line);
        int id = RouteTracer.fetchId(line);
        Assertions.assertEquals(1, ttl);
        Assertions.assertEquals(42733, id);
        scanner.close();
    }

    @Test
    void test4() throws Exception {
        Scanner scanner = new Scanner(new File(SAMPLE_TCP_DUMP_1));
        String line = scanner.nextLine();
        double srcTimeStamp = Double.parseDouble(line.split(" ")[0]);
        scanner.nextLine();
        line = scanner.nextLine();
        double respTimeStamp = Double.parseDouble(line.split(" ")[0]);
        double time = RouteTracer.calculateTime(srcTimeStamp, respTimeStamp);
        Assertions.assertEquals(0.52, time);
        scanner.close();
    }

    @Test
    void test5() throws Exception {
        Scanner scanner = new Scanner(new File(SAMPLE_TCP_DUMP_1));
        scanner.nextLine();
        scanner.nextLine();
        scanner.nextLine();
        String line = scanner.nextLine();
        String destIpAddr = RouteTracer.match(line, RouteTracer.IP_REGEX).split(" ")[0];
        Assertions.assertEquals("128.192.76.129", destIpAddr);
        scanner.close();
    }

    @Test
    void test6() throws Exception {
        Scanner scanner = new Scanner(new File(SAMPLE_TCP_DUMP_1));
        String line = scanner.nextLine();
        int ttl = RouteTracer.fetchTTL(line);
        double srcTimeStamp = Double.parseDouble(line.split(" ")[0]);
        scanner.nextLine();
        line = scanner.nextLine();
        double respTimeStamp = Double.parseDouble(line.split(" ")[0]);
        double time = RouteTracer.calculateTime(srcTimeStamp, respTimeStamp);
        line = scanner.nextLine();
        String destIpAddr = RouteTracer.match(line, RouteTracer.IP_REGEX).split(" ")[0];
        TraceRouteToken token = new TraceRouteToken(ttl, destIpAddr);
        token.addTime(time);
        Assertions.assertEquals(new TraceRouteToken(1, "128.192.76.129", 0.52), token);
    }

    @Test
    void test7() throws Exception {
        Scanner scanner = new Scanner(new File(SAMPLE_TCP_DUMP_1));
        Map<Integer, RouteTracer.Source> srcMap = new LinkedHashMap<>();
        Map<Integer, RouteTracer.Response> respMap = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                continue;
            }
            double timeStamp;
            try {
                timeStamp = Double.parseDouble((line.split(" ")[0]));
            } catch (NumberFormatException ignore) {
                continue;
            }
            boolean isSrc = line.contains(RouteTracer.SRC_STRING);
            if (isSrc) {
                int srcTtl = RouteTracer.fetchTTL(line);
                srcMap.put(RouteTracer.fetchId(line), new RouteTracer.Source(srcTtl, timeStamp));
                scanner.nextLine();
                continue;
            }
            line = scanner.nextLine();
            String destIpAddr = RouteTracer.match(line, RouteTracer.IP_REGEX).split(" ")[0];
            line = scanner.nextLine();
            respMap.put(RouteTracer.fetchId(line), new RouteTracer.Response(destIpAddr, timeStamp));
        }
        Map<Integer, TraceRouteToken> tokenMap = new TreeMap<>();
        srcMap.forEach(((id, source) -> {
            RouteTracer.Response response = respMap.get(id);
            if (response == null) {
                return;
            }
            int ttl = source.ttl();
            TraceRouteToken token = tokenMap.getOrDefault(ttl, new TraceRouteToken(ttl, response.ipAddr()));
            double time = RouteTracer.calculateTime(source.time(), response.time());
            token.addTime(time);
            tokenMap.put(ttl, token);
        }));
        List<TraceRouteToken> tokensToReturn = new ArrayList<>(tokenMap.values());
        Assertions.assertEquals(List.of(new TraceRouteToken(1, "128.192.76.129", 0.52)), tokensToReturn);
    }

    @Test
    void test8() throws Exception {
        Scanner scanner = new Scanner(new File(SAMPLE_TCP_DUMP_2));
        Map<Integer, RouteTracer.Source> srcMap = new LinkedHashMap<>();
        Map<Integer, RouteTracer.Response> respMap = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                continue;
            }
            double timeStamp;
            try {
                timeStamp = Double.parseDouble((line.split(" ")[0]));
            } catch (NumberFormatException ignore) {
                continue;
            }
            boolean isSrc = line.contains(RouteTracer.SRC_STRING);
            if (isSrc) {
                int srcTtl = RouteTracer.fetchTTL(line);
                srcMap.put(RouteTracer.fetchId(line), new RouteTracer.Source(srcTtl, timeStamp));
                scanner.nextLine();
                continue;
            }
            line = scanner.nextLine();
            String destIpAddr = RouteTracer.match(line, RouteTracer.IP_REGEX).split(" ")[0];
            line = scanner.nextLine();
            respMap.put(RouteTracer.fetchId(line), new RouteTracer.Response(destIpAddr, timeStamp));
        }
        Map<Integer, TraceRouteToken> tokenMap = new TreeMap<>();
        srcMap.forEach(((id, source) -> {
            RouteTracer.Response response = respMap.get(id);
            if (response == null) {
                return;
            }
            int ttl = source.ttl();
            TraceRouteToken token = tokenMap.getOrDefault(ttl, new TraceRouteToken(ttl, response.ipAddr()));
            double time = RouteTracer.calculateTime(source.time(), response.time());
            token.addTime(time);
            tokenMap.put(ttl, token);
        }));
        List<TraceRouteToken> tokensToReturn = new ArrayList<>(tokenMap.values());
        Assertions.assertEquals(List.of(new TraceRouteToken(1, "128.192.76.129", 0.52, 0.64, 0.78)), tokensToReturn);
    }

}
