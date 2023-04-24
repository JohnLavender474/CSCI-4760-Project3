package com.csci4760.project3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TcpDumpParser {

    private static final Pattern SRC_REGEX = Pattern.compile("proto TCP");
    private static final Pattern RESP_REGEX = Pattern.compile("proto ICMP");
    private static final Pattern TTL_REGEX = Pattern.compile("ttl \\d+");
    private static final Pattern ID_REGEX = Pattern.compile("id \\d+");
    private static final Pattern TIME_REGEX = Pattern.compile("\\d+\\.\\d+ IP");
    private static final Pattern IP_REGEX = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+ > \\d+\\.\\d+\\.\\d+\\.\\d+");

    private record ResponseInfo(int srcTTL, String destIP, float time) implements Comparable<ResponseInfo> {

        @Override
        public int compareTo(ResponseInfo o) {
            return srcTTL - o.srcTTL();
        }

    }

    /**
     * @param src
     * @return
     */
    public static List<TraceRouteToken> parse(String src) throws FileNotFoundException, IllegalStateException {
        // parse sources and responses
        File file = new File(src);
        Scanner scanner = new Scanner(file);
        Map<Integer, Float> sources = new HashMap<>();
        Map<Integer, ResponseInfo> responses = new HashMap<>();
        int lineNumber = 0;
        while (scanner.hasNextLine()) {
            lineNumber++;
            String line = scanner.nextLine();

            // fetch the time of the message
            Optional<String> timeOptional = match(line, TIME_REGEX);
            if (timeOptional.isEmpty()) {
                throw new IllegalStateException("No TIME on line " + lineNumber);
            }
            float time = Float.parseFloat((timeOptional.get()).split(" ")[0]);

            // check if line type is for source message, and if so then put id number and time to sources map
            // and continue while loop
            Optional<String> lineTypeOptional = match(line, SRC_REGEX);
            if (lineTypeOptional.isPresent()) {
                // fetch the src id of the source message
                Optional<String> idStringOptional = match(line, ID_REGEX);
                if (idStringOptional.isEmpty()) {
                    throw new IllegalStateException("No ID for source on line " + lineNumber);
                }
                int srcId = Integer.parseInt(idStringOptional.get().split(" ")[1]);
                sources.put(srcId, time);
                // skip next line of scanner
                scanner.nextLine();
                continue;
            }

            // line type MUST be response type at this point or else throw illegal state exception
            lineTypeOptional = match(line, RESP_REGEX);
            if (lineTypeOptional.isEmpty()) {
                throw new IllegalStateException("No LINE TYPE on line " + lineNumber);
            }
            // fetch dest ip address from second line of response block
            line = scanner.nextLine();
            lineNumber++;
            Optional<String> ipOptional = match(line, IP_REGEX);
            if (ipOptional.isEmpty()) {
                throw new IllegalStateException("No IP on line " + lineNumber);
            }
            String destIp = ipOptional.get().split(" ")[0];
            // fetch src id and src ttl from third line of response block
            line = scanner.nextLine();
            lineNumber++;
            Optional<String> idStringOptional = match(line, ID_REGEX);
            if (idStringOptional.isEmpty()) {
                throw new IllegalStateException("No source ID in response block on line " + lineNumber);
            }
            int srcId = Integer.parseInt(idStringOptional.get().split(" ")[1]);
            Optional<String> ttlStringOptional = match(line, TTL_REGEX);
            if (ttlStringOptional.isEmpty()) {
                throw new IllegalStateException("No TTL in response block on line " + lineNumber);
            }
            int srcTTL = Integer.parseInt(ttlStringOptional.get().split(" ")[1]);
            responses.put(srcId, new ResponseInfo(srcTTL, destIp, time));
        }

        // create trace route tokens using sources and responses
        List<TraceRouteToken> traceRouteTokens = new ArrayList<>();
        sources.forEach((srcId, time) -> {
            if (!responses.containsKey(srcId)) {
                throw new IllegalStateException("No response info for source id " + srcId);
            }
            ResponseInfo responseInfo = responses.get(srcId);

        });
        Collections.sort(traceRouteTokens);
        return traceRouteTokens;
    }

    private static Optional<String> match(String string, Pattern pattern) {
        Matcher matcher = pattern.matcher(string);
        return matcher.find() ? Optional.of(matcher.group()) : Optional.empty();
    }

    private static float calculateTime(float start, float end) {
        return (end - start) * 1000;
    }

}
