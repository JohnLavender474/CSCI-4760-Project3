package com.csci4760.project3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

public class RouteTracerTests {

    private static final String SAMPLE_TCP_DUMP_1 = "SampleTcpDump_1.txt";
    private static final String SAMPLE_TCP_DUMP_2 = "SampleTcpDump_2.txt";

    /*

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
        RouteTracer.TTL_ID_Key ttl_id_key = RouteTracer.fetchTTL_ID_Key(line);
        Assertions.assertEquals(new RouteTracer.TTL_ID_Key(1, 42733), ttl_id_key);
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
        RouteTracer.TTL_ID_Key ttl_id_key = RouteTracer.fetchTTL_ID_Key(line);
        double srcTimeStamp = Double.parseDouble(line.split(" ")[0]);
        scanner.nextLine();
        line = scanner.nextLine();
        double respTimeStamp = Double.parseDouble(line.split(" ")[0]);
        double time = RouteTracer.calculateTime(srcTimeStamp, respTimeStamp);
        line = scanner.nextLine();
        String destIpAddr = RouteTracer.match(line, RouteTracer.IP_REGEX).split(" ")[0];
        TraceRouteToken token = new TraceRouteToken(ttl_id_key.ttl(), destIpAddr);
        token.addTime(time);
        Assertions.assertEquals(new TraceRouteToken(1, "128.192.76.129", 0.52), token);
    }

    @Test
    void test7() throws Exception {
        Scanner scanner = new Scanner(new File(SAMPLE_TCP_DUMP_1));
        Map<RouteTracer.TTL_ID_Key, Double> srcMap = new LinkedHashMap<>();
        Map<RouteTracer.TTL_ID_Key, Map.Entry<Double, String>> respMap = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                continue;
            }
            double timeStamp = Double.parseDouble((line.split(" ")[0]));
            boolean isSrc = line.contains(RouteTracer.SRC_STRING);
            if (isSrc) {
                srcMap.put(RouteTracer.fetchTTL_ID_Key(line), timeStamp);
                scanner.nextLine();
                continue;
            }
            line = scanner.nextLine();
            String destIpAddr = RouteTracer.match(line, RouteTracer.IP_REGEX).split(" ")[0];
            line = scanner.nextLine();
            respMap.put(RouteTracer.fetchTTL_ID_Key(line), Map.entry(timeStamp, destIpAddr));
        }
        Map<Integer, TraceRouteToken> tokenMap = new TreeMap<>();
        srcMap.forEach(((ttl_id_key, startTime) -> {
            Map.Entry<Double, String> respEntry = respMap.get(ttl_id_key);
            int ttl = ttl_id_key.ttl();
            TraceRouteToken token = tokenMap.getOrDefault(ttl, new TraceRouteToken(ttl, respEntry.getValue()));
            double time = RouteTracer.calculateTime(startTime, respEntry.getKey());
            token.addTime(time);
            tokenMap.put(ttl, token);
        }));
        List<TraceRouteToken> tokensToReturn = new ArrayList<>(tokenMap.values());
        Assertions.assertEquals(List.of(new TraceRouteToken(1, "128.192.76.129", 0.52)), tokensToReturn);
    }

    @Test
    void test8() throws Exception {
        Scanner scanner = new Scanner(new File(SAMPLE_TCP_DUMP_2));
        Map<RouteTracer.TTL_ID_Key, Double> srcMap = new LinkedHashMap<>();
        Map<RouteTracer.TTL_ID_Key, Map.Entry<Double, String>> respMap = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                continue;
            }
            double timeStamp = Double.parseDouble((line.split(" ")[0]));
            boolean isSrc = line.contains(RouteTracer.SRC_STRING);
            if (isSrc) {
                srcMap.put(RouteTracer.fetchTTL_ID_Key(line), timeStamp);
                scanner.nextLine();
                continue;
            }
            line = scanner.nextLine();
            String destIpAddr = RouteTracer.match(line, RouteTracer.IP_REGEX).split(" ")[0];
            line = scanner.nextLine();
            respMap.put(RouteTracer.fetchTTL_ID_Key(line), Map.entry(timeStamp, destIpAddr));
        }
        Map<Integer, TraceRouteToken> tokenMap = new TreeMap<>();
        srcMap.forEach(((ttl_id_key, startTime) -> {
            Map.Entry<Double, String> respEntry = respMap.get(ttl_id_key);
            int ttl = ttl_id_key.ttl();
            TraceRouteToken token = tokenMap.getOrDefault(ttl, new TraceRouteToken(ttl, respEntry.getValue()));
            double time = RouteTracer.calculateTime(startTime, respEntry.getKey());
            token.addTime(time);
            tokenMap.put(ttl, token);
        }));
        List<TraceRouteToken> tokensToReturn = new ArrayList<>(tokenMap.values());
        Assertions.assertEquals(List.of(new TraceRouteToken(1, "128.192.76.129", 0.52, 0.64, 0.78)), tokensToReturn);
    }

     */

}
