package com.csci4760.project3;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public class TraceRouteToken {

    private final int ttl;
    private final String ipAddr;
    private final List<Double> times;

    public TraceRouteToken(int ttl, String ipAddr) {
        this.ttl = ttl;
        this.ipAddr = ipAddr;
        times = new ArrayList<>();
    }

    public TraceRouteToken(int ttl, String ipAddr, double... times) {
        this(ttl, ipAddr);
        Arrays.stream(times).forEach(this::addTime);
    }

    public void addTime(double time) {
        times.add(time);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TTL ").append(ttl).append('\n')
                .append(ipAddr).append('\n');
        times.forEach(time -> sb.append(time).append(" ms\n"));
        return sb.toString();
    }

}
