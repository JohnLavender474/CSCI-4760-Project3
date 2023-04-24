package com.csci4760.project3;

import lombok.Getter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public final class TraceRouteToken implements Comparable<TraceRouteToken> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

    private final int ttl;
    private final String destIP;
    private final List<Float> responseTimes;

    public TraceRouteToken(int ttl, String destIP) {
        this.ttl = ttl;
        this.destIP = destIP;
        this.responseTimes = new ArrayList<>();
    }

    public void addResponseTime(float responseTime) {
        responseTimes.add(responseTime);
    }

    @Override
    public int compareTo(TraceRouteToken o) {
        return ttl - o.getTtl();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TTL ").append(ttl).append('\n').append(destIP).append('\n');
        responseTimes.forEach(responseTime ->
                sb.append(String.format(DECIMAL_FORMAT.format(responseTime))).append(" ms\n"));
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TraceRouteToken t && ttl == t.getTtl() && Objects.equals(destIP, t.getDestIP());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ttl, destIP);
    }


}
