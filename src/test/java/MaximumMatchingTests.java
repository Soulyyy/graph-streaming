import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

import computation.Algorithm;
import graph.Edge;
import graph.Graph;
import graph.Matching;
import computation.MatchingAlgorithm;
import graph.Vertex;
import utils.Cache;
import utils.GraphToMap;
import utils.JSONtoGraph;
import utils.NotBipartiteException;

import static org.junit.Assert.*;

public class MaximumMatchingTests {

  @Test
  public void testCorrectnessSmallmatching() throws FileNotFoundException, NotBipartiteException {
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

}
