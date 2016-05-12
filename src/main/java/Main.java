import computation.Algorithm;
import computation.MatchingAlgorithm;
import graph.*;
import utils.Cache;
import utils.GraphToMap;
import utils.JSONtoGraph;
import utils.NotBipartiteException;

import java.io.FileNotFoundException;
import java.util.Map;

public class Main {

  public static void main(String[] args) throws Exception {
    Graph graph = JSONtoGraph.createVertexList("verticesfailmatching.json");
    Map<String, Vertex> lookupTable = GraphToMap.createGraphLookup(graph);
    Matching matching = createMatchingFromFilePaths("verticesfailmatching.json", "edgesfailmatching.json");
    MatchingAlgorithm matchingAlgorithm = new MatchingAlgorithm(lookupTable, matching);
    matchingAlgorithm.findUnweightedMatching(0.05);
  }

  private static Matching createMatchingFromFilePaths(String vertexPath, String edgePath) throws Exception {
    Graph graph = JSONtoGraph.createVertexList("verticesfailmatching.json");
    Map<String, Vertex> lookupTable = GraphToMap.createGraphLookup(graph);
    Cache.setEdgeStreamFileName("edgesfailmatching.json");
    Algorithm algorithm = new Algorithm(lookupTable);
    Bipartition bipartition = algorithm.bipartition();
    System.out.println(bipartition);
    return algorithm.createMaximalMatching();
  }
}
