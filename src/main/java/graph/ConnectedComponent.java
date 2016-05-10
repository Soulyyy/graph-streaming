package graph;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ConnectedComponent {

  public ConnectedComponent(Vertex vertex) {
    this.elements = new ArrayList<>();
    addElement(vertex);
  }

  @Getter
  @Setter
  private List<Vertex> elements = new ArrayList<>();

  public void flipElementSigns() {
    elements.forEach(Vertex::flipSign);
  }

  public void addComponent(ConnectedComponent component) {
    component.getElements().forEach(this::addElement);
  }

  public void addElement(Vertex vertex) {
    this.elements.add(vertex);
  }

  @Override
  public String toString() {
    return "ConnectedComponent(" +
        elements +
        ')';
  }
}
