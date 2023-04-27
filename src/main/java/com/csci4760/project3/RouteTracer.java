package com.csci4760.project3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteTracer {

    static final String SRC_STRING = "proto TCP";
    static final Pattern ID_REGEX = Pattern.compile("id \\d+");
    static final Pattern TTL_REGEX = Pattern.compile("ttl \\d+");
    static final Pattern IP_REGEX = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+ > \\d+\\.\\d+\\.\\d+\\.\\d+");

    record Source(int ttl, double time) {
    }

    record Response(String ipAddr, double time) {
    }

    public static List<TraceRouteToken> traceRoute(String src) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(src));
        // source map: src ttl and src id for key, src time stamp for value
        // use linked hash map to retain insertion order
        Map<Integer, Source> srcMap = new LinkedHashMap<>();
        // response map: src ttl and src id for key, resp time stamp and resp IP for value
        Map<Integer, Response> respMap = new HashMap<>();
        // scan file, collect sources and respones
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
            boolean isSrc = line.contains(SRC_STRING);
            // if is source, then fetch source information and continue while loop
            if (isSrc) {
                int srcTtl = fetchTTL(line);
                srcMap.put(fetchId(line), new Source(srcTtl, timeStamp));
                scanner.nextLine();
                continue;
            }
            // if line is not source, then gather response information
            line = scanner.nextLine();
            String destIpAddr = match(line, IP_REGEX).split(" ")[0];
            line = scanner.nextLine();
            respMap.put(fetchId(line), new Response(destIpAddr, timeStamp));
        }
        // collect trace route tokens
        Map<Integer, TraceRouteToken> tokenMap = new TreeMap<>();
        srcMap.forEach(((id, source) -> {
            Response response = respMap.get(id);
            // Map.Entry<Double, String> respEntry = respMap.get(ttl_id_key);
            if (response == null) {
                return;
            }
            int ttl = source.ttl();
            TraceRouteToken token = tokenMap.getOrDefault(ttl, new TraceRouteToken(ttl, response.ipAddr()));
            double time = calculateTime(source.time(), response.time());
            token.addTime(time);
            tokenMap.put(ttl, token);
        }));
        return new ArrayList<>(tokenMap.values());
    }

    static int fetchId(String line) {
        return Integer.parseInt(match(line, ID_REGEX).split(" ")[1]);
    }

    static int fetchTTL(String line) {
        return Integer.parseInt(match(line, TTL_REGEX).split(" ")[1]);
    }

    static String match(String string, Pattern pattern) {
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new RuntimeException("No match in string [" + string + "] for pattern [" + pattern + "]");
    }

    static double calculateTime(double srcTimeStamp, double respTimeStamp) {
        double time = (respTimeStamp - srcTimeStamp) * 1000;
        return Math.round(time * 100.0) / 100.0;
    }

}
