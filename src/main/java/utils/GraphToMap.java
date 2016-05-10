package utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import graph.Graph;
import graph.Vertex;

public class GraphToMap {

  public static Map<String, Vertex> createGraphLookup(Graph graph) {
    Map<String, Vertex> lookupTable = new HashMap<>();
    Arrays.stream(graph.getVertices()).forEach(v -> lookupTable.put(v.getName(), v));
    return lookupTable;
  }
}
