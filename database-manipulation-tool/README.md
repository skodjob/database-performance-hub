# Database-manipulation-tool 
Java quarkus application used for idempotent upsertion of a single entity into multiple different databases.

Main functionality is the upsert with table creation/alteration. You send a http request with JSON body (example below)
and the tables will be either created or altered (columns will be added to the table) so the schema corresponds with the JSON. Then it proceeds to either insert or update the row into all the databases.

There is also a special profile used for load generation in performance testing of database-dependent systems.
# Classic and Performance profiles
There are two main use-cases for DMT and each uses a corresponding Quarkus profile.
### Classic profile
Classic profile is used for CRUD manipulation of databases when utilizing DMT for testing database dependent systems.\
Classic profile is the default DMT runtime profile and does not require any additional options.\
If you want to specify the profile even though you do not need to, use the `-Dquarkus.profile=classic` maven option or the `QUARKUS_PROFILE=classic` environment variable.
### Performance profile
Performance profile is used when utilizing DMT for load generation in performance testing database dependant systems.\
It currently does two changes to increase performance of DMT:
- Disables Prometheus metrics.
- Disables retrying of statement execution.  

When using DMT for load generation in performance testing, use the `performance` profile with the `-Dquarkus.profile=performance` maven option or the `QUARKUS_PROFILE=performance` environment variable.

## Working features

This is currently in alpha state so there are not many features completed and the ones that are finished aren't tested properly.

### Databases

| Database/feature        	       | Postgres 	 | Mongo 	 | Mysql 	 | Oracle 	 | SqlServer 	 | Db2 	 |
|---------------------------------|------------|---------|---------|----------|-------------|-------|
| Create table            	       | X    	     | X   	   | X   	   | 	        | 	           | 	     |
| Insert                  	       | X    	     | X	      | X   	   | 	        | 	           | 	     |
| Upsert and alter/create table 	 | X	         | X	      | X	      | 	        | 	           | 	     |
| Delete                  	       | 	          | 	       | 	       | 	        | 	           | 	     |
| Drop table        	             | X	         | X	      | X	      | 	        | 	           | 	     |
| Reset Database        	         | X	         | 	X      | X	      | 	        | 	           | 	     |

### Streams
| Streaming system | Redis | Pulsar | RabbitMQ |
|------------------|-------|--------|----------|
| Poll from stream | X     |        |          |
| Send to stream   | X     |        |          |
| Auth             |       |        |          |

### Redis configuration
- `data.source.redis.host` -- Redis host (`localhost`)
- `data.source.redis.port` -- Redis port (`6379`)
- `data.source.redis.pool.max` -- Amount of connections open to Redis (`10`)

## Json insertion schema
The Json schema is `DatabaseEntry` class from the DMT-schema project.  
```
{
    "name": "pepici",
    "primary": "name",
    "columnEntries": [
        {
            "columnName": "name",
            "dataType": "VarChar(255)",
            "value": "PepaZDepa5"
        },
        {
            "columnName": "age",
            "dataType": "Integer",
            "value": "30"
        },
        {
            "columnName": "money",
            "dataType": "Double",
            "value": "15.5"
        }
    ]
}
```

It is imperative to use the primary key so that the created tables can have primary key and can be correctly updated.
Primary keys must always be unique and once set cannot be changed. Therefore, I recommend using GUID or other system that ensures these properties.

When Creating tables, the `columnEntries` value determines the columns and their types.

You can reset all databases on DMT start with property `onstart.reset.database`.

## Current REST endpoints

<summary><code>POST</code> <code><b>/Main/Insert</b></code> <code>(Inserts json into all enabled databases)</code></summary>
<summary><code>POST</code> <code><b>/Main/CreateTable</b></code> <code>(Creates table/collection in every enabled database)</code></summary>
<summary><code>POST</code> <code><b>/Main/CreateTableAndUpsert</b></code> <code>(Upserts json into all databases and creates tables if they did not exist or adds columns so the json can be upserted)</code></summary>
<summary><code>DELETE</code> <code><b>/Main/DropTable</b></code> <code>(Drops table/collection in every enabled database)</code></summary>
<summary><code>GET</code> <code><b>/Main/ResetDatabase</b></code> <code>(Drops all databases/schemas and creates them again)</code></summary>
<summary><code>POST</code> <code><b>/Main/TimedInsert</b></code> <code>(Inserts json into all enabled dbs and returns timestamp of query execution)</code></summary>

<br />
<summary><code>GET</code> <code><b>/Utility/GetAll</b></code> <code>(Gets all created tables and their current state)</code></summary>
<summary><code>POST</code> <code><b>/Utility/TestSchema</b></code> <code>(Test if the input json schema is correct)</code></summary>
<br />
<summary><code>POST</code> <code><b>/Main/GenerateLoad?count={count}&maxRows={maxRows}</b></code> <code>(Creates set number of queries with maximum table size and upserts them into databases. Returns the time length of the whole request and just the execution of the queries)</code></summary>
<summary><code>POST</code> <code><b>/Main/GenerateBatchLoad?count={count}&maxRows={maxRows}</b></code> <code>(Same as GenerateLoad but uses batch statements for better performance)</code></summary>
<br />
<summary>
    <code>POST</code> 
    <code><b>/Redis/pollMessages?max=5</b></code> 
    <code>
        [
            "channel1",
            "channel2"
        ]
    </code>
    <code>Reads at most 'max' messages from specified channels</code>
</summary>
<summary>
    <code>POST</code> 
    <code><b>/Redis/sendMessage?channel=channel1</b></code> 
    <code>
        {
            "rider": "Alonso",
            "position": 1
        }
    </code>
    <code>Sends a message specified in json body to the redis stream channel 
    specified in as header argument</code>
</summary>
<summary>
    <code>GET</code> 
    <code><b>/Redis/reset</b></code>
    <code>Flush whole Redis instance</code>
</summary>
<summary>
    <code>GET</code> 
    <code><b>/Redis/readHash?hashKey=hash1</b></code>
    <code>Returns all messages stored under 'hashKey'</code>
</summary>

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/database-manipulation-tool-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Provided Code

### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
