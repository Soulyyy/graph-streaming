package graph;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class Matching {

  public Matching() {
    this.edges = new ArrayList<>();
  }

  private List<Edge> edges;

  public void addEdge(Edge edge) {
    edges.add(edge);
  }

  @Override
  public String toString() {
    return "Matching(" +
        edges +
        ")";
  }
}
