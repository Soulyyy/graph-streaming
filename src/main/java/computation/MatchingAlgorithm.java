package computation;

import java.io.FileNotFoundException;
import java.util.*;

import graph.Bipartition;
import graph.Edge;
import graph.Matching;
import graph.Vertex;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import utils.Cache;
import utils.JSONtoGraph;

import static java.lang.Math.*;

@Slf4j
public class MatchingAlgorithm extends AbstractAlgorithm {

  private Set<String> globalIgnore;

  private Set<String> ignoreVertices;
  private Set<Edge> edgesWithLeftWings;

  public MatchingAlgorithm(Map<String, Vertex> lookupTable, Matching matching) {
    super(lookupTable, matching);
    ignoreVertices = new HashSet<>();
    edgesWithLeftWings = new HashSet<>();
    this.globalIgnore = new HashSet<>();
  }

  public Matching findUnweightedMatching(double epsilon) throws Exception {
    Algorithm algorithm = new Algorithm(lookupTable);
    Bipartition bipartition = algorithm.bipartition();
    int loopConstant = (int) ceil(log(6.0 * epsilon) / log(8.0 / 9.0));
    log.info("Loop constant is: {}", loopConstant);
    for (int i = 1; i < loopConstant; i++) {
      double sigma = epsilon / (2 - 3 * epsilon);
      List<AugmentingPath> augmentingPaths = findAugmentedPaths(bipartition, sigma);
      applyAugmentingPathChanges(augmentingPaths);
    }
    log.info("The final matching is: {}", matching);
    return matching;
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

      for (MatchingLeftWingPair pair : leftWings) {
        for (Edge rightwing : rightWings) {
          if (pair.getOpenVertex().equals(rightwing.getLeftVertex()) || pair.getOpenVertex().equals(rightwing.getRightVertex())) {
            augmentingPaths.add(new AugmentingPath(pair.getMatchingEdge(), pair.getLeftWingEdge(), rightwing));
          }
        }
      }
    }
  }

  public List<AugmentingPath> findAugmentedPaths(Bipartition bipartition, double sigma) throws Exception {
    int matchingSize = matching.getEdges().size();
    List<AugmentingPath> augmentingPaths = new ArrayList<>();
    Set<Vertex> left = bipartition.getFirstPartition();
    Set<Vertex> right = bipartition.getSecondPartition();
    while (true) {
      Set<Edge> leftWings = disjointLeft(left);
      if (leftWings.size() < matchingSize * sigma) {
        return augmentingPaths;
      }
      Set<Edge> rightWings = disjointRight(right);
      Set<AugmentingPath> foundPaths = createAugmentingPaths(leftWings, rightWings);
      Set<String> addToIngore = filterVertices(foundPaths);
      globalIgnore.addAll(addToIngore);
      augmentingPaths.addAll(foundPaths);
    }
  }

  private Set<Edge> disjointLeft(Set<Vertex> left) throws Exception {
    Set<Edge> disjointLeftWings = new HashSet<>();
    Iterator<Edge> iterator = getStreamHead();
    while (iterator.hasNext()) {
      Edge edge = iterator.next();
      if (areVerticesIgnored(edge)) {
        continue;
      }
      VertexPair pair = getEdgeVertices(edge);
      if (left.contains(pair.getLeftVertex()) || left.contains(pair.getRightVertex())) {
        if (!matching.getEdges().contains(edge)) {
          if (!ignoreContainsEdgeVertices(edge)) {
            if(isPairFree(pair)) {
              disjointLeftWings.add(edge);
              addEdgeVerticesToIgnore(edge);
              addMatchingEdgeToChecked(edge);
            }
          }
        }
      }
    }
    return disjointLeftWings;
  }

  private Set<Edge> disjointRight(Set<Vertex> right) throws Exception {
    Set<String> ignoreRightVertices = new HashSet<>();
    Set<Edge> disjointRightWings = new HashSet<>();
    Iterator<Edge> iterator = getStreamHead();
    while (iterator.hasNext()) {
      Edge edge = iterator.next();
      if (areVerticesIgnored(edge)) {
        continue;
      }
      VertexPair pair = getEdgeVertices(edge);
      if (right.contains(pair.getLeftVertex()) || right.contains(pair.getRightVertex())) {
        if (!matching.getEdges().contains(edge)) {
          Edge matchingEdge = getMatchingEdge(edge);
          if (matchingEdge != null && edgesWithLeftWings.contains(matchingEdge)) {
            if (!ignoreRightVertices.contains(edge.getLeftVertex()) && !ignoreRightVertices.contains(edge.getRightVertex())) {
              if(isPairFree(pair)) {
                ignoreRightVertices.add(edge.getLeftVertex());
                ignoreRightVertices.add(edge.getRightVertex());
                disjointRightWings.add(edge);
              }
            }
          }
        }
      }
    }
    return disjointRightWings;
  }

  private Set<String> filterVertices(Set<AugmentingPath> augmentingPaths) throws Exception {
    Set<String> checkedVertices = new HashSet<>();
    for (Edge edge : edgesWithLeftWings) {
      checkedVertices.add(edge.getLeftVertex());
      checkedVertices.add(edge.getRightVertex());
    }
    for (AugmentingPath path : augmentingPaths) {
      checkedVertices.addAll(removeAugmentingWings(path));
    }
    Iterator<Edge> iterator = getStreamHead();
    while (iterator.hasNext()) {
      Edge edge = iterator.next();
    }


    return checkedVertices;
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
              if (ignoreVertices.contains(matchingEdge.getLeftVertex()) || ignoreVertices.contains(matchingEdge.getRightVertex())) {
                contains = true;
              }

              if (ignoreVertices.contains(edge.getLeftVertex()) || ignoreVertices.contains(edge.getRightVertex())) {
                contains = true;
              }

              if (matchingEdge.getLeftVertex().equals(edge.getLeftVertex()) || matchingEdge.getLeftVertex().equals(edge.getRightVertex())) {
                if (!contains) {
                  matchingPairs.add(new MatchingLeftWingPair(matchingEdge, edge, matchingEdge.getRightVertex()));
                }
              } else if (matchingEdge.getRightVertex().equals(edge.getLeftVertex()) || matchingEdge.getRightVertex().equals(edge.getRightVertex())) {
                if (!contains) {
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
    for (AugmentingPath augmentingPath : augmentingPaths) {
      matching.getEdges().remove(augmentingPath.getToReplace());
      matching.addEdge(augmentingPath.leftEdge);
      matching.addEdge(augmentingPath.rightEdge);
    }
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

  private void addEdgeVerticesToIgnore(Edge edge) {
    String leftVertexName = edge.getLeftVertex();
    String rightVertexName = edge.getRightVertex();
    ignoreVertices.add(leftVertexName);
    ignoreVertices.add(rightVertexName);
  }

  private boolean ignoreContainsEdgeVertices(Edge edge) {
    String leftVertexName = edge.getLeftVertex();
    String rightVertexName = edge.getRightVertex();
    boolean leftContains = ignoreVertices.contains(leftVertexName);
    boolean rightContains = ignoreVertices.contains(rightVertexName);
    return leftContains || rightContains;
  }

  private void addMatchingEdgeToChecked(Edge edge) {
    String leftVertexName = edge.getLeftVertex();
    String rightVertexName = edge.getRightVertex();
    Edge leftEdge = matching.getVertexEdgeMap().get(leftVertexName);
    Edge rightEdge = matching.getVertexEdgeMap().get(rightVertexName);
    if (leftEdge != null) {
      edgesWithLeftWings.add(leftEdge);
      return;
    }
    if (rightEdge != null) {
      edgesWithLeftWings.add(rightEdge);
    }
  }

  private Edge getMatchingEdge(Edge edge) {
    String leftVertexName = edge.getLeftVertex();
    String rightVertexName = edge.getRightVertex();
    Edge leftEdge = matching.getVertexEdgeMap().get(leftVertexName);
    Edge rightEdge = matching.getVertexEdgeMap().get(rightVertexName);
    if (leftEdge != null) {
      return leftEdge;
    } else if (rightEdge != null) {
      return rightEdge;
    } else {
      return null;
    }
  }

  private boolean areVerticesIgnored(Edge edge) {
    String leftVertexName = edge.getLeftVertex();
    String rightVertexName = edge.getRightVertex();
    boolean isLeftIgnored = globalIgnore.contains(leftVertexName);
    boolean isRightIgnored = globalIgnore.contains(rightVertexName);
    return isLeftIgnored || isRightIgnored;
  }

  private Set<AugmentingPath> createAugmentingPaths(Set<Edge> leftEdges, Set<Edge> rightEdges) {
    Set<AugmentingPath> augmentingPaths = new HashSet<>();
    for (Edge left : leftEdges) {
      for (Edge right : rightEdges) {
        AugmentingPath path = createAugmentingPath(left, right);
        if (path != null) {
          augmentingPaths.add(path);
        }
      }
    }
    return augmentingPaths;
  }

  private AugmentingPath createAugmentingPath(Edge left, Edge right) {
    if (!left.equals(right)) {
      Edge leftMatching = getMatchingEdge(left);
      Edge rightMatching = getMatchingEdge(right);
      if (leftMatching != null && leftMatching.equals(rightMatching)) {
        return new AugmentingPath(leftMatching, left, right);
      }
    }
    return null;
  }

  private Set<String> removeAugmentingWings(AugmentingPath augmentingPath) {
    Set<String> wingTips = new HashSet<>();
    Edge matchingEdge = augmentingPath.getToReplace();
    if (matchingEdge.getLeftVertex().equals(augmentingPath.getLeftEdge().getLeftVertex()) || matchingEdge.getRightVertex().equals(augmentingPath.getLeftEdge().getLeftVertex())) {
      wingTips.add(augmentingPath.getLeftEdge().getRightVertex());
    } else {
      wingTips.add(augmentingPath.getLeftEdge().getLeftVertex());
    }
    if (matchingEdge.getLeftVertex().equals(augmentingPath.getRightEdge().getLeftVertex()) || matchingEdge.getRightVertex().equals(augmentingPath.getRightEdge().getLeftVertex())) {
      wingTips.add(augmentingPath.getRightEdge().getRightVertex());
    } else {
      wingTips.add(augmentingPath.getRightEdge().getLeftVertex());
    }
    return wingTips;
  }

  private boolean isPairFree(VertexPair pair) {
    boolean isLeftFree = isFreeVertex(pair.getLeftVertex().getName());
    boolean isRightFree = isFreeVertex(pair.getRightVertex().getName());
    return isLeftFree || isRightFree;
  }

  private boolean isFreeVertex(String vertex) {
    Edge edge = matching.getVertexEdgeMap().get(vertex);
    if(edge == null) {
      return true;
    }
    return false;
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
  @EqualsAndHashCode
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
