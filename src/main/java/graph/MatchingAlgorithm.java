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
    int loopConstant = (int) ceil(log(6.0 * epsilon) / log(8.0 / 9.0));
    log.info("Loop constant is: {}", loopConstant);
    for (int i = 1; i < loopConstant; i++) {
      double sigma = epsilon / (2 - 3 * epsilon);
      List<AugmentingPath> augmentingPaths = findAugmentingPaths(bipartition, sigma);
      applyAugmentingPathChanges(augmentingPaths);
    }
    log.info("The final matching is: {}", matching);

  }

  public List<AugmentingPath> findAugmentingPaths(Bipartition bipartition, double sigma) throws FileNotFoundException {
    List<AugmentingPath> augmentingPaths = new ArrayList<>();
    Set<String> ignoreVertices = new HashSet<>();
    while (true) {
      Set<MatchingLeftWingPair> leftWings = findDisjointLeftWings(ignoreVertices);
      if (leftWings.size() <= matching.getEdges().size() * sigma) {
        return augmentingPaths;
      }
      Set<Edge> rightWings = findDisjointRightWings(leftWings);
      ignoreVertices = filterVertices(leftWings, rightWings, ignoreVertices);

      for(MatchingLeftWingPair pair: leftWings) {
        for(Edge rightwing: rightWings) {
          if (pair.getOpenVertex().equals(rightwing.getLeftVertex()) || pair.getOpenVertex().equals(rightwing.getRightVertex())) {
            augmentingPaths.add(new AugmentingPath(pair.getMatchingEdge(), pair.getLeftWingEdge(), rightwing));
          }
        }
      }
    }
  }

  //Find for matching only
  private Set<MatchingLeftWingPair> findDisjointLeftWings(Set<String> ignoreVertices) throws FileNotFoundException {
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
        if (matchingVertexNames.contains(edge.getLeftVertex()) || matchingVertexNames.contains(edge.getRightVertex())) {
          log.trace("Matching edges are: {}", matching.getEdges());
          if (!matching.getEdges().contains(edge)) {
            leftEdges.add(edge);
            for (Edge matchingEdge : matching.getEdges()) {

              //Refac these blocks
              boolean contains = false;
              if(ignoreVertices.contains(matchingEdge.getLeftVertex()) || ignoreVertices.contains(matchingEdge.getRightVertex())) {
                contains = true;
              }

              if(ignoreVertices.contains(edge.getLeftVertex()) || ignoreVertices.contains(edge.getRightVertex())) {
                contains = true;
              }

              if (matchingEdge.getLeftVertex().equals(edge.getLeftVertex()) || matchingEdge.getLeftVertex().equals(edge.getRightVertex())) {
                if(!contains) {
                  matchingPairs.add(new MatchingLeftWingPair(matchingEdge, edge, matchingEdge.getRightVertex()));
                }
              } else if (matchingEdge.getRightVertex().equals(edge.getLeftVertex()) || matchingEdge.getRightVertex().equals(edge.getRightVertex())) {
                if(!contains) {
                  matchingPairs.add(new MatchingLeftWingPair(matchingEdge, edge, matchingEdge.getLeftVertex()));
                }
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

  private Set<Edge> findDisjointRightWings(Set<MatchingLeftWingPair> matchingPairs) throws FileNotFoundException {
    Set<Edge> rightWings = new HashSet<>();
    Iterator<Edge> iterator = JSONtoGraph.createEdgeStream(Cache.getEdgeStreamFileName()).iterator();
    while (iterator.hasNext()) {
      Edge edge = iterator.next();
      if (!matching.getEdges().contains(edge)) {
        for (MatchingLeftWingPair pair : matchingPairs) {
          if (pair.getOpenVertex().equals(edge.getLeftVertex()) || pair.getOpenVertex().equals(edge.getRightVertex())) {
            rightWings.add(edge);
/*            List<Edge> changeList = new ArrayList<>();
            changeList.add(pair.matchingEdge);
            changeList.add(pair.getLeftWingEdge());
            changeList.add(edge);
            return changeList;*/
          }
        }
      }

    }
    log.info("Right wings are: {}", rightWings);
    return rightWings;
  }

  private Set<String> filterVertices(Set<MatchingLeftWingPair> leftPairs, Set<Edge> rightWings, Set<String> ignoreVertices) throws FileNotFoundException {

    Iterator<Edge> iterator = JSONtoGraph.createEdgeStream(Cache.getEdgeStreamFileName()).iterator();
    //Filter left pairs
    for (MatchingLeftWingPair pair : leftPairs) {
      Edge matchingEdge = pair.getMatchingEdge();
      ignoreVertices.add(matchingEdge.getLeftVertex());
      ignoreVertices.add(matchingEdge.getRightVertex());
      for (Edge rightWing : rightWings) {
        if (rightWing.getLeftVertex().equals(pair.getOpenVertex()) || rightWing.getRightVertex().equals(pair.getOpenVertex())) {
          if (pair.getOpenVertex().equals(pair.getLeftWingEdge().getLeftVertex())) {
            ignoreVertices.add(pair.getLeftWingEdge().getRightVertex());
          } else {
            ignoreVertices.add(pair.getLeftWingEdge().getLeftVertex());
          }

          if (rightWing.getLeftVertex().equals(pair.getOpenVertex())) {
            ignoreVertices.add(rightWing.getRightVertex());
          } else {
            ignoreVertices.add(rightWing.getLeftVertex());
          }
        }
      }
    }

    //TODO failure sweep for case 2
    //What is the point with the third point?

    return ignoreVertices;
  }

  private Vertex vertexLookup(String vertexName) {
    return lookupTable.get(vertexName);
  }

  private void applyAugmentingPathChanges(List<AugmentingPath> augmentingPaths) {
    for(AugmentingPath augmentingPath: augmentingPaths) {
      matching.getEdges().remove(augmentingPath.getToReplace());
      matching.addEdge(augmentingPath.leftEdge);
      matching.addEdge(augmentingPath.rightEdge);
    }
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

  @AllArgsConstructor
  private static class AugmentingPath {

    @Getter
    @Setter
    private Edge toReplace;

    @Getter
    @Setter
    private Edge leftEdge;

    @Getter
    @Setter
    private Edge rightEdge;
  }
}
