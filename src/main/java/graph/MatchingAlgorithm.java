package graph;

import java.util.Map;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import utils.Algorithm;
import utils.NotBipartiteException;

import static java.lang.Math.*;

@AllArgsConstructor
@Slf4j
public class MatchingAlgorithm {

  //Spatial complexity of M, number of Vertices
  private final Map<String, Vertex> lookupTable;
  //Stream, look at one edge at once
  private Stream<Edge> edgeStream;

  public void findUnweightedMatching(double epsilon) throws NotBipartiteException {
    Algorithm algorithm = new Algorithm(lookupTable, edgeStream);
    Bipartition bipartition = algorithm.bipartition();
    for (int i = 1; i < ceil(log(6 * epsilon) / log(8 / 9)); i++) {
      double sigma = epsilon / (2 - 3 * epsilon);
      findAugmentingPaths(sigma);
    }

  }

  public void findAugmentingPaths(double sigma) {

  }

  private void findDisjointLeftWings() {

  }

  private void findDisjointRightWings() {

  }
}
