package utils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import graph.Edge;
import graph.Graph;
import graph.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

public class JSONtoGraph {

  public static Graph createVertexList(String path) throws FileNotFoundException {
    File file = new File(JSONtoGraph.class.getClassLoader().getResource(path).getFile());
    JsonReader reader = new JsonReader(new FileReader(file));
    Gson gson = new Gson();
    Vertex[] vertices = gson.fromJson(reader, Vertex[].class);
    return new Graph(vertices);
  }

  public static List<Edge> createEdgeStream(String path) throws FileNotFoundException {
    File file = new File(JSONtoGraph.class.getClassLoader().getResource(path).getFile());
    JsonReader reader = new JsonReader(new FileReader(file));
    Gson gson = new Gson();
    return Arrays.asList(gson.fromJson(reader, Edge[].class));
  }
}
