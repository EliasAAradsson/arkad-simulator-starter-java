import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.json.*;

public class App {

  private static String readFromURL(String urlString) throws MalformedURLException {
    URL url = new URL(urlString);

    StringBuilder resultStringBuilder = new StringBuilder();
    try (Scanner myReader = new Scanner(url.openStream())) {
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        resultStringBuilder.append(data).append("\n");
      }
      myReader.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultStringBuilder.toString();
  }

  public static void main(String[] args) throws MalformedURLException {

    // Start of data parsing
    String jsonString = readFromURL("https://student-graphathon.ey.r.appspot.com/api/data/lth");

    JSONObject obj = new JSONObject(jsonString);

    JSONObject jsonNodes = obj.getJSONObject("nodes");
    JSONObject jsonRelationships = obj.getJSONObject("relationships");

    Map<String, Node> nodes = new HashMap<String, Node>();
    Iterator<String> nodeKeys = jsonNodes.keys();
    nodeKeys.forEachRemaining(key -> {
      JSONObject node = jsonNodes.getJSONObject(key);
      nodes.put(key, new Node(node.getString("name"), node.getInt("swag"), node.getInt("timePrice")));
    });

    Map<String, List<Relationship>> relationships = new HashMap<String, List<Relationship>>();
    Iterator<String> relationshipKeys = jsonRelationships.keys();
    relationshipKeys.forEachRemaining(key -> {
      JSONArray relationshipsArray = jsonRelationships.getJSONArray(key);
      List<Relationship> relationshipsList = new ArrayList<Relationship>();
      for (int i = 0; i < relationshipsArray.length(); i++) {
        JSONObject relationship = relationshipsArray.getJSONObject(i);
        relationshipsList.add(new Relationship(relationship.getString("to"), relationship.getInt("timePrice")));
      }
      relationships.put(key, relationshipsList);
    });
    // End of data parsing

    final Integer TIMEBUDGET = 4500;
    final String STARTING_COMPANY_ID = "company25";

    String currentCompany = STARTING_COMPANY_ID;
    Integer timeLeft = TIMEBUDGET;
    Set<String> visited = new HashSet<String>();
    StringBuilder resultBuilder = new StringBuilder();

    while (timeLeft > 0) {
      Integer costToVisit = nodes.get(currentCompany).timePrice;

      if (!visited.contains(currentCompany) && timeLeft >= costToVisit) {
        visited.add(currentCompany);
        timeLeft -= costToVisit;
        resultBuilder.append("(" + nodes.get(currentCompany).name + ":collect)-->");
      } else {
        resultBuilder.append("(" + nodes.get(currentCompany).name + ")-->");
      }

      List<Relationship> possiblePaths = relationships.get(currentCompany);

      Relationship nextPath = possiblePaths.get((int) Math.floor(Math.random() * possiblePaths.size()));

      timeLeft -= nextPath.timePrice;
      currentCompany = nextPath.to;
    }
    resultBuilder.delete(resultBuilder.length() - 3, resultBuilder.length());
    System.out.println(resultBuilder.toString());
  }

  static public class Node {
    public String name;
    public Integer swag;
    public Integer timePrice;

    public Node(String name, Integer swag, Integer timePrice) {
      this.name = name;
      this.swag = swag;
      this.timePrice = timePrice;
    }
  }

  static public class Relationship {
    public String to;
    public Integer timePrice;

    public Relationship(String to, Integer timePrice) {
      this.to = to;
      this.timePrice = timePrice;
    }
  }

}
