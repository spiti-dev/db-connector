package dev.spiti.utilities.dbconnector.db;

import dev.spiti.utilities.dbconnector.DBConnector;
import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by naresh.g@spititech.com on 2019-04-22
 */
public class Neo4J implements DBConnector {
  
  private Session session;
  private String url, username, password;
  
  public Neo4J(String url, String username, String password) {
    this.url = url;
    this.username = username;
    this.password = password;
    this.session = createSession();
  }
  
  private Session createSession() {
    Driver driver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
    return driver.session();
  }
  
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
  
  @Override
  public List<Map<String, Object>> getData(String statement, Object args) {
    List<Map<String, Object>> list = new ArrayList<>();
    getRecords(statement, (Map<String, Object>)args).stream().forEach(record -> list.add(record.asMap()));
    return list;
  }
  
  public List<Map<String, Object>> getNodes(String statement, Map<String, Object> args) {
    List<Map<String, Object>> list = new ArrayList<>();
    getRecords(statement, args).stream().forEach(record -> list.add(record.get(0).asMap()));
    return list;
  }
  
  private List<Record> getRecords(String statement, Map<String, Object> args) {
    StatementResult result = session.run(statement, args);
    return result.list();
  }
  
  @Override
  public void closeConnection() {
    session.close();
  }
  
  
  
}
