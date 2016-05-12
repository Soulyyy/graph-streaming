package graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode(exclude = {"matchingVertices", "vertexEdgeMap"})
public class Matching {

  public Matching(Set<Edge> edges) {
    this.edges = edges;
    this.matchingVertices = new HashSet<>();
    this.vertexEdgeMap = new HashMap<>();
    //TODO REFACCCC!!!!
    edges.iterator().forEachRemaining(e -> {
      matchingVertices.add(e.getLeftVertex());
      matchingVertices.add(e.getRightVertex());
      vertexEdgeMap.put(e.getLeftVertex(), e);
      vertexEdgeMap.put(e.getRightVertex(), e);
    });
  }

  public Matching() {
    this.edges = new HashSet<>();
    this.matchingVertices = new HashSet<>();
  }

  @Getter
  private Set<Edge> edges;

  //TODO major hack, remove
  @Getter
  Map<String, Edge> vertexEdgeMap;

  //TODO convert to vertices
  @Getter
  private Set<String> matchingVertices;

  public void addEdge(Edge edge) {
    edges.add(edge);
    matchingVertices.add(edge.getLeftVertex());
    matchingVertices.add(edge.getRightVertex());
  }

  @Override
  public String toString() {
    return "Matching(" +
        edges +
        ")";
  }
}
