package graph;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Bipartition {

  public Bipartition() {
    this.firstPartition = new ArrayList<>();
    this.secondPartition = new ArrayList<>();
  }

  @Getter
  @Setter
  private List<Vertex> firstPartition;

  @Getter
  @Setter
  private List<Vertex> secondPartition;

  @Override
  public String toString() {
    return "Bipartition{" +
        "first[" + firstPartition +
        "], second[" + secondPartition +
        "]}";
  }
}
