import graph.Bipartition;
import graph.Edge;
import graph.Graph;
import graph.Vertex;
import utils.Algorithm;
import utils.GraphToMap;
import utils.JSONtoGraph;
import graph.MatchingAlgorithm;

import java.util.List;
import java.util.Map;

public class Main {

  public static void main(String[] args) throws Exception {
    Graph graph = JSONtoGraph.createVertexList("vertices.json");
    Map<String, Vertex> lookupTable = GraphToMap.createGraphLookup(graph);
    List<Edge> edges = JSONtoGraph.createEdgeStream("edges.json");
    Algorithm algorithm = new Algorithm(lookupTable, edges.stream());
    Bipartition bipartition = algorithm.bipartition();
    System.out.println(bipartition);
    MatchingAlgorithm matching = new MatchingAlgorithm(lookupTable, edges.stream());
    matching.findUnweightedMatching(0.2);
  }
}
