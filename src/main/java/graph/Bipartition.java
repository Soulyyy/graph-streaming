package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Bipartition {

  public Bipartition() {
    this.firstPartition = new HashSet<>();
    this.secondPartition = new HashSet<>();
  }

  @Getter
  @Setter
  private Set<Vertex> firstPartition;

  @Getter
  @Setter
  private Set<Vertex> secondPartition;

  @Override
  public String toString() {
    return "Bipartition{" +
        "first[" + firstPartition +
        "], second[" + secondPartition +
        "]}";
  }
}
