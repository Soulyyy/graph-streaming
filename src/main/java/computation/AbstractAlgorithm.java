package computation;

import java.util.Map;

import graph.Matching;
import graph.Vertex;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractAlgorithm {

  //Spatial complexity of M, number of Vertices
  protected final Map<String, Vertex> lookupTable;


  protected Matching matching;
}
