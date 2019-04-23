package dev.spiti.utilities.dbconnector.db;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import dev.spiti.utilities.dbconnector.DBConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by naresh.g@spititech.com on 2019-04-22
 */
public class Cassandra implements DBConnector {
  
  private Session session;
  private Cluster cluster;
  private String contactPoints;
  private String username;
  private String password;
  private String keyspace;
  
  /**
   * Sets the required attributes and creates a session with database
   *
   * @param contactPoints Cassandra contact points in environemnt, comma seperated
   * @param keyspace keyspace of the schema being queried
   * @param username
   * @param password
   */
  public Cassandra(String contactPoints, String keyspace, String username, String password) {
    this.contactPoints = contactPoints;
    this.username = username;
    this.password = password;
    this.keyspace = keyspace;
    this.session = createSession();
  }
  
  private Cluster createCluster(){
    AuthProvider authProvider = new PlainTextAuthProvider(username, password);
    String[] contacts = contactPoints.split(",");
    
    cluster = Cluster
            .builder()
            .addContactPoints(contacts)
            .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
            .withLoadBalancingPolicy(
                    new TokenAwarePolicy(new DCAwareRoundRobinPolicy.Builder().build()))
            .withAuthProvider(authProvider)
            .build();
    return cluster;
    
  }
  
  private Session createSession(){
    return createCluster().connect(keyspace);
  }
  
  /**
   * Accepts the statement as String and args as List<String> object.
   * @param statement Select statement as a string, returning only one column
   * @param args all arguments as Object of List<String>
   * @return Returns all items from requested one column as list, all values are read as strings
   */
  @Override
  public List<String> getKey(String statement, Object args) {
    List<String> queryArgs = (List<String>) args;
    List<String> keys = new ArrayList<>();
    PreparedStatement preparedStatement = session.prepare(statement);
    BoundStatement bound = preparedStatement.bind(queryArgs.stream().toArray(String[]::new));
    ResultSet resultSet = session.execute(bound);
    for(Row row : resultSet) {
      keys.add(String.valueOf(row.getObject(0)));
    }
    return keys;
  }
  
  @Override
  public List<Map<String, Object>> getData(String statement, Object args) {
    List<String> queryArgs = (List<String>) args;
    PreparedStatement preparedStatement = session.prepare(statement);
    BoundStatement bound = preparedStatement.bind(queryArgs.stream().toArray(String[]::new));
    ResultSet resultSet = session.execute(bound);
    return normalizeResults(resultSet);
  }
  
  @Override
  public void closeConnection() {
    cluster.close();
    session.close();
  }
  
  private List<Map<String, Object>> normalizeResults(ResultSet resultSet){
    List<Map<String, Object>> list = new ArrayList<>();
    
    List<String> columnNames = getColumnNames(resultSet);
    
    for (Row row : resultSet) {
      Map<String, Object> map = new HashMap<>();
      columnNames.forEach(column->{
        map.put(column,row.getObject(column));
      });
      list.add(map);
    }
    return list;
  }
  
  private List<String> getColumnNames(ResultSet resultSet){
    return(resultSet.getColumnDefinitions().asList().stream()
            .map(cl -> cl.getName())
            .collect(Collectors.toList()));
  }
}
