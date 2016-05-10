package graph;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;

import lombok.AllArgsConstructor;
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
    for (int i = 1; i < ceil(log(6 * epsilon) / log(8 / 9)); i++) {
      double sigma = epsilon / (2 - 3 * epsilon);
      findAugmentingPaths(bipartition, sigma);
    }

  }

  public void findAugmentingPaths(Bipartition bipartition, double sigma) throws FileNotFoundException {
    Iterator<Edge> iterator = JSONtoGraph.createEdgeStream(Cache.getEdgeStreamFileName()).iterator();
    while(iterator.hasNext()) {
      Edge edge = iterator.next();

    }
  }

  private void findDisjointLeftWings() {

  }

  private void findDisjointRightWings() {

  }
}
