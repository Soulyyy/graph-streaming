import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import graph.*;
import org.junit.Test;

import computation.Algorithm;
import computation.MatchingAlgorithm;
import utils.Cache;
import utils.GraphToMap;
import utils.JSONtoGraph;

import static org.junit.Assert.*;

public class MaximumMatchingTests {

  @Test
  public void testCorrectnessSmallmatching() throws Exception {
    Graph graph = JSONtoGraph.createVertexList("verticesfailmatching.json");
    Map<String, Vertex> lookupTable = GraphToMap.createGraphLookup(graph);
    Cache.setEdgeStreamFileName("edgesfailmatching.json");
    Algorithm algorithm = new Algorithm(lookupTable);
    Matching matching = algorithm.createMaximalMatching();
    MatchingAlgorithm matchingAlgorithm = new MatchingAlgorithm(lookupTable, matching);
    matching = matchingAlgorithm.findUnweightedMatching(0.05);
    Set<Edge> referenceSet = new HashSet<>();
    referenceSet.add(new Edge("a", "c"));
    referenceSet.add(new Edge("b", "d"));
    Matching referencematching = new Matching(referenceSet);
    assertEquals(referencematching, matching);
  }

  @Test
  public void testFindAugTest() throws Exception {
    Map<String, Vertex> lookupTable = createLookupTable();
    Algorithm algorithm = new Algorithm(lookupTable);
    Bipartition bipartition = algorithm.bipartition();
    Matching matching = algorithm.createMaximalMatching();
    MatchingAlgorithm matchingAlgorithm  = new MatchingAlgorithm(lookupTable, matching);
    matchingAlgorithm.findAugmentedPaths(bipartition, 0.05);

  }

  private Map<String, Vertex> createLookupTable() throws Exception {
    Graph graph = JSONtoGraph.createVertexList("verticesfailmatching.json");
    Map<String, Vertex> lookupTable = GraphToMap.createGraphLookup(graph);
    Cache.setEdgeStreamFileName("edgesfailmatching.json");
    return lookupTable;
  }
}
