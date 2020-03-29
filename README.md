# JMX attributes logging tool

A tiny utility which dumps JMX attributes in CSV format.

## Prerequisite

It is required to enable the JMX features in the target service.
Usually, this can be done by passing some options to JVM as follows:.

  ```bash
    -Dcom.sun.management.jmxremote
    -Dcom.sun.management.jmxremote.local.only=false
    -Dcom.sun.management.jmxremote.authenticate=false
    -Dcom.sun.management.jmxremote.port=$JMX_PORT
    -Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT
    -Djava.rmi.server.hostname=127.0.0.1
    -Dcom.sun.management.jmxremote.ssl=false
  ```

## Build

Run the following commands under the directory where this project is cloned.
The built jar will be created at `build/libs/jmx-exporter.jar`

```console
$ ./script/build.sh

$ java -jar build/libs/jmx-exporter.jar scripts/config.yaml

timestamp,maxThreads,activeSessions
2020-02-19T17:51:25.676,200,0
2020-02-19T17:51:30.810,200,0
2020-02-19T17:51:35.820,200,0
2020-02-19T17:51:40.833,200,0
2020-02-19T17:51:45.848,200,0
```

The built jar contains all dependencies.
So, you can run the above command in any environments where JDK6 or later is installed.

## Usage

### Sample configuration

```yaml
# Information of the JMX endpoint
endpoint:
  # Optional: JMX server type: 'weblogic' / 'generic' (Default: 'generic')
  # type: weblogic
  # Optional: JMX host server address (Default: localhost)
  host: localhost
  # Optional: JMX server port (Default: 9000)
  port: 9000
  # Optional: The credential information of the connecting user.
  #  (Default: User credential is not sent to the server)
  user: admin
  pass: weblogic1

# Settings for output data
output:
  # Optional: Interval between the data retrieval, in seconds. (Default: 5 seconds)
  interval: 5
  # Optional: How many times retrieve the metrics data. (Default: 10 times)
  rows: 5
  # Optional: Whether CRLF is used as the line separator or uses LF instead. (Default: true)
  usesCRLF: true
  # Optional: The format of the timestamp. (Default: 'yyyy-MM-dd'T'HH:mm:ss.SSSZ')
  timestamp: "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

# The list of JMX attributes
columns:
  # Required: Column name
- name: heapEdenSpaceUsage(committed)
  # Required: The MBean name
  type: java.lang:type=MemoryPool,name=Eden Space
  # Required: The MBean attribute name
  attribute: Usage
  # Optional: The property name of the attribute. (This is effective only if the attributes value is a Composite.)
  key: committed

- name: heapEdenSpaceUsage
  type: java.lang:type=MemoryPool,name=Eden Space
  attribute: Usage

- name: maxThreads
  type: Catalina:type=ThreadPool,name="http-apr-8080"
  attribute: maxThreads
```

### Run

JMX attributes are emitted to the standard output by default.

```console
$ java -jar jmx-exporter.jar config.yml

timestamp,maxThreads,activeSessions
2020-02-19T17:51:25.676,200,0
2020-02-19T17:51:30.810,200,0
2020-02-19T17:51:35.820,200,0
2020-02-19T17:51:40.833,200,0
2020-02-19T17:51:45.848,200,0
```
