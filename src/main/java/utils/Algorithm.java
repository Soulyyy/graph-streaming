package utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import graph.Bipartition;
import graph.ConnectedComponent;
import graph.Edge;
import graph.Vertex;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class Algorithm {

  //Spatial complexity of M, number of Vertices
  private final Map<String, Vertex> lookupTable;
  //Stream, look at one edge at once
  private Stream<Edge> edgeStream;

  public boolean isBipartite() throws NotBipartiteException {
    Map<Vertex, ConnectedComponent> connectedComponents = createConnectedComponents();
    Iterator<Edge> edgePointer = edgeStream.iterator();
    while (edgePointer.hasNext()) {
      Edge edge = edgePointer.next();
      Vertex left = vertexLookup(edge.getLeftVertex());
      Vertex right = vertexLookup(edge.getRightVertex());
      if(left.isSign() == right.isSign()) {
        connectedComponents.get(right).flipElementSigns();
        if(left.isSign() == right.isSign()) {
          log.info("Left: {}", connectedComponents.get(left));
          log.info("Right: {}", connectedComponents.get(right));
          log.info("This is not bipartite");
          throw new NotBipartiteException();

        }
      }
     mergeConnectedComponents(connectedComponents, left, right);
    }
    return true;
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

}
