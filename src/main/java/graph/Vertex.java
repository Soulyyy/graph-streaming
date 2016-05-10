package graph;

import com.google.gson.annotations.SerializedName;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class Vertex {

  @Getter
  @Setter
  @SerializedName("name")
  private String name;

  @Getter
  @Setter
  private boolean sign;

  public Vertex(String name) {
    this.name = name;
    this.sign = false;
  }

  @Override
  public String toString() {
    return "(" + name + ", " + sign + ")";
  }

  public void flipSign() {
    this.sign = !sign;
  }
}
