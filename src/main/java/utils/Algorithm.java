package utils;

import java.io.FileNotFoundException;
import java.util.*;

import graph.Bipartition;
import graph.ConnectedComponent;
import graph.Edge;
import graph.Matching;
import graph.Vertex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class Algorithm {

  public Algorithm(Map<String, Vertex> lookupTable) {
    this.lookupTable = lookupTable;
    this.connectedComponents = createConnectedComponents();
  }

  //Spatial complexity of M, number of Vertices
  private final Map<String, Vertex> lookupTable;
  private Map<Vertex, ConnectedComponent> connectedComponents;

  public Bipartition bipartition() throws Exception {
    Iterator<Edge> edgeIterator = getStreamHead();
    while (edgeIterator.hasNext()) {
      Edge edge = edgeIterator.next();
      VertexPair vertices = getEdgeVertices(edge);
      if (verticesSameSign(vertices)) {
        flipConnectedComponentSigns(vertices);
        if (verticesSameSign(vertices)) {
          throw new NotBipartiteException();
        }
      }
      mergeConnectedComponents(connectedComponents, vertices);
    }
    return createBipartition();
  }

  private Map<Vertex, ConnectedComponent> createConnectedComponents() {
    Map<Vertex, ConnectedComponent> connectedComponentMap = new HashMap<>();
    lookupTable.values().forEach(v -> connectedComponentMap.put(v, new ConnectedComponent(v)));
    return connectedComponentMap;
  }

  private Vertex vertexLookup(String vertexName) {
    return lookupTable.get(vertexName);
  }

  private void mergeConnectedComponents(Map<Vertex, ConnectedComponent> connectedComponents, VertexPair pair) {
    Vertex left = pair.getLeftVertex();
    Vertex right = pair.getRightVertex();
    ConnectedComponent newComponent = connectedComponents.get(left);
    ConnectedComponent toAddComponent = connectedComponents.get(right);
    newComponent.addComponent(toAddComponent);
    connectedComponents.put(left, newComponent);
    connectedComponents.put(right, newComponent);
  }

  private Bipartition createBipartition() {
    Set<Vertex> partitionOne = new HashSet<>();
    Set<Vertex> partitionTwo = new HashSet<>();
    for (Vertex v : lookupTable.values()) {
      if (v.isSign()) {
        partitionOne.add(v);
      } else {
        partitionTwo.add(v);
      }
    }
    return new Bipartition(partitionOne, partitionTwo);
  }

  //1/2 approximation
  public Matching createMaximalMatching() throws FileNotFoundException {
    Set<Edge> matchingEdges = new HashSet<>();
    Set<Vertex> verticesInMatching = new HashSet<>();
    Iterator<Edge> edgeIterator = getStreamHead();
    while (edgeIterator.hasNext()) {
      Edge edge = edgeIterator.next();
      Vertex left = vertexLookup(edge.getLeftVertex());
      Vertex right = vertexLookup(edge.getRightVertex());
      if (!verticesInMatching.contains(left) && !verticesInMatching.contains(right)) {
        matchingEdges.add(edge);
        verticesInMatching.add(left);
        verticesInMatching.add(right);
      }
    }
    return new Matching(matchingEdges);
  }

  private Iterator<Edge> getStreamHead() throws FileNotFoundException {
    String filename = Cache.getEdgeStreamFileName();
    List<Edge> edges = JSONtoGraph.createEdgeStream(filename);
    Iterator<Edge> edgeIterator = edges.iterator();
    return edgeIterator;
  }

  private VertexPair getEdgeVertices(Edge edge) {
    Vertex left = vertexLookup(edge.getLeftVertex());
    Vertex right = vertexLookup(edge.getRightVertex());
    VertexPair pair = new VertexPair(left, right);
    return pair;
  }

  private boolean verticesSameSign(VertexPair pair) {
    boolean leftSign = pair.getLeftVertex().isSign();
    boolean rightSign = pair.getRightVertex().isSign();
    return leftSign == rightSign;
  }

  private void flipConnectedComponentSigns(VertexPair pair) {
    Vertex right = pair.getRightVertex();
    connectedComponents.get(right).flipElementSigns();
  }

  @AllArgsConstructor
  static class VertexPair {

    @Getter
    @Setter
    private Vertex leftVertex;

    @Getter
    @Setter
    private Vertex rightVertex;
  }


}
