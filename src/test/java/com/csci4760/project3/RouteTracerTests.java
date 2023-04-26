package com.csci4760.project3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Scanner;

public class RouteTracerTests {

    private static final String SAMPLE_TCP_DUMP_1 = "SampleTcpDump_1.txt";

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

}
