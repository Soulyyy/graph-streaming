package graph;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Edge {

  @Getter
  @Setter
  private List<Vertex> vertices = new ArrayList<>();

}
