package graph;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@AllArgsConstructor
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
