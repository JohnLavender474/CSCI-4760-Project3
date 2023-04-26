package com.csci4760.project3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteTracer {

    public static final String SRC_STRING = "proto TCP";
    public static final Pattern ID_REGEX = Pattern.compile("id \\d+");
    public static final Pattern TTL_REGEX = Pattern.compile("ttl \\d+");
    public static final Pattern TIME_STAMP_REGEX = Pattern.compile("\\d+\\.\\d+ IP");
    public static final Pattern IP_REGEX = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+ > \\d+\\.\\d+\\.\\d+\\.\\d+");

    public record TTL_ID_Key(int ttl, int id) {
    }

    /**
     *
     *
     * @param src
     * @return
     * @throws FileNotFoundException
     */
    public static List<TraceRouteToken> traceRoute(String src) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(src));
        // source map: src ttl and src id for key, src time stamp for value
        // use linked hash map to retain insertion order
        Map<TTL_ID_Key, Double> srcMap = new LinkedHashMap<>();
        // response map: src ttl and src id for key, resp time stamp and resp IP for value
        Map<TTL_ID_Key, Map.Entry<Double, String>> respMap = new HashMap<>();
        // scan file, collect sources and respones
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            double timeStamp = Double.parseDouble(match(line.split(" ")[0], TIME_STAMP_REGEX));
            boolean isSrc = line.contains(SRC_STRING);
            // if is source, then fetch source information and continue while loop
            if (isSrc) {
                srcMap.put(fetchTTL_ID_Key(line), timeStamp);
                scanner.nextLine();
                continue;
            }
            // if line is not source, then gather response information
            line = scanner.nextLine();
            String destIpAddr = match(line, IP_REGEX).split(" ")[0];
            line = scanner.nextLine();
            respMap.put(fetchTTL_ID_Key(line), Map.entry(timeStamp, destIpAddr));
        }
        // collect trace route tokens
        Map<Integer, TraceRouteToken> tokenMap = new TreeMap<>();
        srcMap.forEach(((ttl_id_key, startTime) -> {
            Map.Entry<Double, String> respEntry = respMap.get(ttl_id_key);
            int ttl = ttl_id_key.ttl();
            TraceRouteToken token = tokenMap.getOrDefault(ttl, new TraceRouteToken(ttl, respEntry.getValue()));
            double time = calculateTime(startTime, respEntry.getKey());
            token.addTime(time);
            tokenMap.put(ttl, token);
        }));
        return new ArrayList<>(tokenMap.values());
    }

    private static TTL_ID_Key fetchTTL_ID_Key(String line) {
        int srcTTL = Integer.parseInt(match(line, TTL_REGEX).split(" ")[1]);
        int srcID = Integer.parseInt(match(line, ID_REGEX).split(" ")[1]);
        return new TTL_ID_Key(srcTTL, srcID);
    }

    private static String match(String string, Pattern pattern) {
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new RuntimeException("No match in string [" + string + "] for pattern [" + pattern + "]");
    }

    private static double calculateTime(double start, double end) {
        return (end - start) * 1000;
    }

}
