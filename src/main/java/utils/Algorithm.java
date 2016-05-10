package utils;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Stream;

import graph.Bipartition;
import graph.ConnectedComponent;
import graph.Edge;
import graph.Matching;
import graph.Vertex;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class Algorithm {

  //Spatial complexity of M, number of Vertices
  private final Map<String, Vertex> lookupTable;

  public Bipartition bipartition() throws NotBipartiteException, FileNotFoundException {
    Map<Vertex, ConnectedComponent> connectedComponents = createConnectedComponents();
    Iterator<Edge> edgeIterator = JSONtoGraph.createEdgeStream(Cache.getEdgeStreamFileName()).iterator();
    while (edgeIterator.hasNext()) {
      Edge edge = edgeIterator.next();
      Vertex left = vertexLookup(edge.getLeftVertex());
      Vertex right = vertexLookup(edge.getRightVertex());
      if (left.isSign() == right.isSign()) {
        connectedComponents.get(right).flipElementSigns();
        if (left.isSign() == right.isSign()) {
          log.info("Left: {}", connectedComponents.get(left));
          log.info("Right: {}", connectedComponents.get(right));
          log.info("This is not bipartite");
          throw new NotBipartiteException();

        }
      }
      mergeConnectedComponents(connectedComponents, left, right);
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

  private void mergeConnectedComponents(Map<Vertex, ConnectedComponent> connectedComponents, Vertex left, Vertex right) {
    ConnectedComponent newComponent = connectedComponents.get(left);
    ConnectedComponent toAddComponent = connectedComponents.get(right);
    newComponent.addComponent(toAddComponent);
    connectedComponents.put(left, newComponent);
    connectedComponents.put(right, newComponent);
  }

  private Bipartition createBipartition() {
    List<Vertex> partitionOne = new ArrayList<>();
    List<Vertex> partitionTwo = new ArrayList<>();
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
    List<Edge> matchingEdges = new ArrayList<>();
    Set<Vertex> verticesInMatching = new HashSet<>();
    Iterator<Edge> edgeIterator = JSONtoGraph.createEdgeStream(Cache.getEdgeStreamFileName()).iterator();
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


}
