package dev.spiti.utilities.dbconnector.db;

import dev.spiti.utilities.dbconnector.Connector;
import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by naresh.g@spititech.com on 2019-04-22
 */
public class Graph implements Connector {
  
  private Session session;
  private String url, username, password;
  
  
  
  /**
   * Sets the required attributes and creates a session with database
   * @param url
   * @param username
   * @param password
   */
  public Graph(String url, String username, String password) {
    this.url = url;
    this.username = username;
    this.password = password;
    this.session = createSession();
  }
  
  private Session createSession() {
    Driver driver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
    return driver.session();
  }
  
  /**
   * Accepts the statement as String and args as object of map of string and object.
   * @param statement Select statement as a string, returning only one column
   * @param args all arguments as Object of map of string and object
   * @return Returns all items from requested one column as list, all values are read as strings
   */
  @Override
  public List<String> getKey(String statement, Object args) {
    
    List<String> list = new ArrayList<>();
    StatementResult result = session.run(statement, (Map<String, Object>) args);
    
    while (result.hasNext()) {
      Record record = result.next();
      String entry = String.valueOf(record.get(0));
      list.add(entry.substring(1,entry.length()-1));
    }
    return list;
  }
  
  /**
   * Accepts the statement as String and args as object of map of string and object.
   * @param statement Select statement as a string, returning only one column
   * @param args all arguments as Object of map of string and object
   * @return Returns all items from requested one column as list, each row is read as map.
   */
  @Override
  public List<Map<String, Object>> getData(String statement, Object args) {
    List<Map<String, Object>> list = new ArrayList<>();
    getRecords(statement, (Map<String, Object>)args).stream().forEach(record -> list.add(record.asMap()));
    return list;
  }
  
  /**
   * Accepts the statement as String and args as object of map of string and object.
   * @param statement Select statement as a string, returning only one column
   * @param args all arguments as Object of map of string and object
   * @return Returns all items from requested one column as list, each node is read as map.
   */
  public List<Map<String, Object>> getNodes(String statement, Map<String, Object> args) {
    List<Map<String, Object>> list = new ArrayList<>();
    getRecords(statement, args).stream().forEach(record -> list.add(record.get(0).asMap()));
    return list;
  }
  
  private List<Record> getRecords(String statement, Map<String, Object> args) {
    StatementResult result = session.run(statement, args);
    return result.list();
  }
  
  /**
   * Close the session and connection
   */
  @Override
  public void closeConnection() {
    session.close();
  }
  
  
  
}
