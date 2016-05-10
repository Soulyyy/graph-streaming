package graph;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@AllArgsConstructor
public class Graph {

  @Setter
  @Getter
  private Vertex[] vertices;

  @Override
  public String toString() {
    return "Graph(" +
        Arrays.toString(vertices) +
        ')';
  }
}
