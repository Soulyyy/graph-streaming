package graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
public class Graph {

  @Setter
  @Getter
  private Vertex[] vertices;
}
