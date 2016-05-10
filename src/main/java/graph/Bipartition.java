package graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Bipartition {

  @Getter
  @Setter
  private ConnectedComponent firstPartition;

  @Getter
  @Setter
  private ConnectedComponent secondPartition;

  @Override
  public String toString() {
    return "Bipartition{" +
        "first[" + firstPartition +
        "], second[" + secondPartition +
        "]}";
  }
}
