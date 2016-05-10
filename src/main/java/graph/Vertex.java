package graph;

import lombok.Getter;
import lombok.Setter;

public class Vertex {

  @Getter
  @Setter
  private String name;

  @Getter
  @Setter
  private boolean sign;

  @Getter
  @Setter
  private Edge leftEdge;

  @Getter
  @Setter
  private Edge rightEdge;
}
