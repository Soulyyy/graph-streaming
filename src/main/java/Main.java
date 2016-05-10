import graph.Graph;
import utils.JSONtoGraph;

import java.io.FileNotFoundException;

public class Main {

  public static void main(String[] args) throws FileNotFoundException {
    Graph graph = JSONtoGraph.createVertexList("vertices.json");
  }
}
