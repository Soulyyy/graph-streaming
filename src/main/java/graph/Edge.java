package graph;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class Edge {

  @Getter
  @Setter
  @SerializedName("left")
  private String leftVertex;

  @Getter
  @Setter
  @SerializedName("right")
  private String rightVertex;

  @Override
  public String toString() {
    return "(" + leftVertex + ", " + rightVertex + ")";
  }
}
