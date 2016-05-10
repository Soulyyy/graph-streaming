package graph;

import java.io.FileNotFoundException;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import utils.Algorithm;
import utils.Cache;
import utils.JSONtoGraph;
import utils.NotBipartiteException;

import static java.lang.Math.*;

@AllArgsConstructor
@Slf4j
public class MatchingAlgorithm {

  //Spatial complexity of M, number of Vertices
  private final Map<String, Vertex> lookupTable;

  private Matching matching;

  public void findUnweightedMatching(double epsilon) throws NotBipartiteException, FileNotFoundException {
    Algorithm algorithm = new Algorithm(lookupTable);
    Bipartition bipartition = algorithm.bipartition();
    int loopConstant = (int)ceil(log(6.0 * epsilon) / log(8.0 / 9.0));
    log.info("Loop constant is: {}", loopConstant);
    for (int i = 1; i < loopConstant; i++) {
      double sigma = epsilon / (2 - 3 * epsilon);
      findAugmentingPaths(bipartition, sigma);
    }

  }

  public void findAugmentingPaths(Bipartition bipartition, double sigma) throws FileNotFoundException {
    Set<MatchingLeftWingPair> leftWings = findDisjointLeftWings(matching);
    if(leftWings.size() <= matching.getEdges().size() * sigma) {
      return;
    }
    List<>

    while(iterator.hasNext()) {
      Edge edge = iterator.next();
      System.out.println(edge);
    }
  }

  //Find for matching only
  private Set<MatchingLeftWingPair> findDisjointLeftWings(Matching matching) throws FileNotFoundException {
    Set<Vertex> verticesInLeftWing = new HashSet<>();
    Set<Edge> leftEdges = new HashSet<>();
    Set<MatchingLeftWingPair> matchingPairs = new HashSet<>();
    Iterator<Edge> iterator = JSONtoGraph.createEdgeStream(Cache.getEdgeStreamFileName()).iterator();
    while (iterator.hasNext()) {
      Edge edge = iterator.next();
      Vertex left = vertexLookup(edge.getLeftVertex());
      Vertex right = vertexLookup(edge.getRightVertex());
      if (!verticesInLeftWing.contains(left) && !verticesInLeftWing.contains(right)) {
        Set<String> matchingVertexNames = matching.getMatchingVertices();
        if(matchingVertexNames.contains(edge.getLeftVertex()) || matchingVertexNames.contains(edge.getRightVertex())) {
          log.trace("Matching edges are: {}", matching.getEdges());
          if(!matching.getEdges().contains(edge)) {
            leftEdges.add(edge);
            for(Edge matchingEdge : matching.getEdges()) {
              if(matchingEdge.getLeftVertex().equals(edge.getLeftVertex()) || matchingEdge.getLeftVertex().equals(edge.getRightVertex())) {
                matchingPairs.add(new MatchingLeftWingPair(matchingEdge, edge, matchingEdge.getRightVertex()));
              }
              else if(matchingEdge.getRightVertex().equals(edge.getLeftVertex()) || matchingEdge.getRightVertex().equals(edge.getRightVertex())) {
                matchingPairs.add(new MatchingLeftWingPair(matchingEdge, edge, matchingEdge.getLeftVertex()));
              }
            }
            verticesInLeftWing.add(left);
            verticesInLeftWing.add(right);
          }
        }
      }
    }
    log.trace("Left edges: {}", leftEdges);
    log.info("Matcing pairs: {}", matchingPairs);
    return matchingPairs;
  }

  private List<Edge> findDisjointRightWings(Set<MatchingLeftWingPair> matchingPairs) throws FileNotFoundException {
    Iterator<Edge> iterator = JSONtoGraph.createEdgeStream(Cache.getEdgeStreamFileName()).iterator();
    while (iterator.hasNext()) {
      Edge edge = iterator.next();
      if(!matching.getEdges().contains(edge)) {
        for(MatchingLeftWingPair pair : matchingPairs) {
          if(pair.getOpenVertex().equals(edge.getLeftVertex()) || pair.getOpenVertex().equals(edge.getRightVertex())) {
            List<Edge> changeList = new ArrayList<>();
            changeList.add(pair.matchingEdge);
            changeList.add(pair.getLeftWingEdge());
            changeList.add(edge);
            return changeList;
          }
        }
      }

    }
  }

  private Vertex vertexLookup(String vertexName) {
    return lookupTable.get(vertexName);
  }


  @AllArgsConstructor
  @ToString
  private static class MatchingLeftWingPair {

    @Getter
    @Setter
    private Edge matchingEdge;

    @Getter
    @Setter
    private Edge leftWingEdge;

    @Getter
    @Setter
    private String openVertex;

  }
}
