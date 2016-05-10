import graph.*;
import utils.Algorithm;
import utils.Cache;
import utils.GraphToMap;
import utils.JSONtoGraph;

import java.util.Map;

public class Main {

  public static void main(String[] args) throws Exception {
    Graph graph = JSONtoGraph.createVertexList("vertices2.json");
    Map<String, Vertex> lookupTable = GraphToMap.createGraphLookup(graph);
    Cache.setEdgeStreamFileName("edges2.json");
    Algorithm algorithm = new Algorithm(lookupTable);
    Bipartition bipartition = algorithm.bipartition();
    System.out.println(bipartition);
    Matching matching = algorithm.createMaximalMatching();
    MatchingAlgorithm matchingAlgorithm = new MatchingAlgorithm(lookupTable, matching);
    System.out.println(algorithm.createMaximalMatching());
    matchingAlgorithm.findUnweightedMatching(0.2);
  }
}
