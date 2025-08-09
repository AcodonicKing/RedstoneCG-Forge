package net.acodonic_king.redstonecg.procedures;

import java.util.*;

public class RedCuWireTransitionPathFinder {
    public static Map<String, List<String>> nodemap = new HashMap<>();
    static {
        buildMap();
    }
    public static List<String> execute(String start, String end){
        Queue<List<String>> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        queue.add(List.of(start));

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String node = path.get(path.size() - 1);

            if (node.equals(end)) return path;

            if (!visited.add(node)) continue;

            for (String neighbor : nodemap.getOrDefault(node, List.of())) {
                List<String> newPath = new ArrayList<>(path);
                newPath.add(neighbor);
                queue.add(newPath);
            }
        }

        return new ArrayList<String>();
    }
    private static void addNode(String from, String to) {
        nodemap.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
    }
    private static void addNodes(String from, String[] to) {
        for(String t: to)
            nodemap.computeIfAbsent(from, k -> new ArrayList<>()).add(t);
    }
    private static void buildMap() {
        addNodes("D", new String[]{"ND", "ED", "SD", "WD"}); //N2D0", "E2D1", "S2D2", "W2D3
        addNodes("N", new String[]{"NU", "NE", "ND", "NW"}); //N0U0", "N1E1", "N2D0", "N3W1
        addNodes("E", new String[]{"EU", "NE", "ED", "SE"}); //E0U1", "N1E1", "E2D1", "S1E3
        addNodes("S", new String[]{"SU", "SE", "SD", "SW"}); //S0U2", "S1E3", "S2D2", "S3W3
        addNodes("W", new String[]{"WU", "NW", "WD", "SW"}); //W0U3", "N3W1", "W2D3", "S3W3
        addNodes("U", new String[]{"NU", "EU", "SU", "WU"}); //N0U0", "E0U1", "S0U2", "W0U3

        addNodes("ND", new String[]{"D", "N"}); //N2D0
        addNodes("ED", new String[]{"D", "E"}); //E2D1
        addNodes("SD", new String[]{"D", "S"}); //S2D2
        addNodes("WD", new String[]{"D", "W"}); //W2D3
        addNodes("NU", new String[]{"N", "U"}); //N0U0
        addNodes("NE", new String[]{"N", "E"}); //N1E1
        addNodes("NW", new String[]{"N", "W"}); //N3W1
        addNodes("EU", new String[]{"E", "U"}); //E0U1
        addNodes("SE", new String[]{"S", "E"}); //S1E3
        addNodes("SU", new String[]{"S", "U"}); //S0U2
        addNodes("SW", new String[]{"S", "W"}); //S3W3
        addNodes("WU", new String[]{"W", "U"}); //W0U3
    }
    public static String getNodeBasedOnConnectionFace(String g, byte c){
        if (c < 0) {return "";}
        if (c < 4) {
            return nodemap.get(g).get(c);
        } else if (c == 4) {
            return g;
        }
        return "";
    }
}
