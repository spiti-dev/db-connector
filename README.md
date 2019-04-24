# DB-Connector : Simple way of dealing with databases

## Contents

*  [Key Features](#kf)
*  [Usages](#use)
*  [Limitations](#lims)
*  [Examples](#ex)
    * [Cassandra](#c*)
    * [Graph](#gr)

<a name="kf"></a>
## Key Features

- Reads data from databases (Currently supports Cassandra and Neo4J)
- Executes queries and converts result set to List<> of Map<String, Object>
- Read data as List<String> when only one column is being read

<a name="use"></a>
## Usages

- Integrate into automation frameworks for data validation
- Connect with database and run `SELECT` queries
- Easy to use data in validations while dealing just with `List` and `Map` objects

<a name="lims"></a>
## Limitations

- Currently supports **_CASSANDRA_ AND _GRAPH_ DATABASES ONLY**
- Currently supports to execute retrieve queries only. Add/Update/Delete support to follow.

<a name="ex"></a>
## Examples

<a name="c*"></a>
### *CASSANDRA*
#### Create connection and session

```
Connector dbConnector = new Cassandra(contactPoints,keyspace, username, password);
```
-   Creates a connection to specified keyspace
-   Session is available on dbConnector object to execute queries

#### Read from *CASSANDRA*
For a query like `SELECT * FROM <TableName>`
```
List<String> args = new ArrayList<>();
List<Map<String, Object>> data = dbConnector.getData(statement, args);
```
-   Passes an empty args list to library and returns each row as a map
-   For queries that has filtering conditions, add variables in same sequence as query to the args list

For a query like `SELECT <ColumnName> from <TableName>`
```
List<Map<String, Object>> data = dbConnector.getKey(statement, args);
```
-   Returns the values from the queried column in a list, each value is read as a String

<a name="gr"></a>
### *GRAPH*
#### Create connection and session

```
Connector dbConnector = new Graph(url,username,password);
```
-   Creates a connection to specified database box in the url. Incase of proxy, connection is made to master
-   Session is available on dbConnector object to execute queries

#### Read from *GRAPH*
