package dev.spiti.utilities.dbconnector;

import java.util.List;
import java.util.Map;

/**
 * Created by naresh.g@spititech.com on 2019-04-22
 */
public interface DBConnector {
  
  List<String> getKey(String statement, Object args);
  List<Map<String, Object>> getData(String statement, Object args);
  void closeConnection();
}
