import graph.Bipartition;
import graph.Edge;
import graph.Graph;
import graph.Vertex;
import utils.Algorithm;
import utils.GraphToMap;
import utils.JSONtoGraph;

import java.util.List;
import java.util.Map;

public class Main {

  public static void main(String[] args) throws Exception {
    Graph graph = JSONtoGraph.createVertexList("vertices.json");
    Map<String, Vertex> lookupTable = GraphToMap.createGraphLookup(graph);
    List<Edge> edges = JSONtoGraph.createEdgeStream("edges.json");
    Algorithm algorithm = new Algorithm(lookupTable, edges.stream());
    boolean isBipartite = algorithm.isBipartite();
    System.out.println(isBipartite);
  }
}
