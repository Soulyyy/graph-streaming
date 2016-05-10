package graph;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Matching {

  public Matching() {
    this.edges = new ArrayList<>();
  }

  private List<Edge> edges;

  public void addEdge(Edge edge) {
    edges.add(edge);
  }
}
