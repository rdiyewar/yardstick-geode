<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

# Yardstick Geode Benchmarks
Yardstick Apache Geode is a set of <a href="http://geode.incubator.apache.org/">Gemfire</a> benchmarks written on top of Yardstick framework.

## Yardstick Framework
Visit <a href="https://github.com/gridgain/yardstick" target="_blank">Yardstick Repository</a> for detailed information on how to run Yardstick benchmarks and how to generate graphs.

The documentation below describes configuration parameters in addition to standard Yardstick parameters.

## Installation
1. Create a local clone of repository
2. Run `mvn package` command for Yardstick Apache Ignite POM

## Provided Benchmarks
The following benchmarks are provided:

1. `GetBenchmark` - benchmarks atomic distributed cache get operation
2. `PutBenchmark` - benchmarks atomic distributed cache put operation
3. `PutGetBenchmark` - benchmarks atomic distributed cache put and get operations together

## Writing Apache Geode Benchmarks
All benchmarks extend `GeodeAbstractBenchmark` class. A new benchmark should also extend this abstract class and implement `test` method. This is the method that is actually benchmarked.

## Running Apache Geode Benchmarks
Before running Apache Geode benchmarks, run `mvn package` command. This command will compile the project and also will unpack scripts from `yardstick-resources.zip` file to `bin` directory.

### Properties And Command Line Arguments
> Note that this section only describes configuration parameters specific to Apache Ignite benchmarks, and not for Yardstick framework. To run Apache Ignite benchmarks and generate graphs, you will need to run them using Yardstick framework scripts in `bin` folder.

> Refer to [Yardstick Documentation](https://github.com/gridgain/yardstick) for common Yardstick properties and command line arguments for running Yardstick scripts.

The following Apache Geode benchmark properties can be defined in the benchmark configuration:

* `-nn <num>` or `--nodeNumber <num>` - Number of nodes (automatically set in `benchmark.properties`), used to wait for the specified number of nodes to start
* `-b <num>` or `--backups <num>` - Number of backups for every key (UNUSED)
* `-gfcfg <path>` or `--gfConfig <path>` - Path to Geode configuration file
* `-gfclicfg <path>` or `--gfClientConfig <path>` - Path to Geode client configuration file
* `-gfacfg <path>` or `--gfAccessorConfig <path>` - Path to Geode accessor configuration file
* `-cm` or `--clientMode` - Flag indicating whether Hazelcast client is used
* `-r <num>` or `--range` - Range of keys that are randomly generated for cache operations

For example if we need to run 2 `GeodeNode` servers on localhost with `GeodePutBenchmark` benchmark on localhost then the following configuration should be specified in `benchmark.properties` file:

```
SERVER_HOSTS=localhost,localhost
    
# Note that -dn and -sn, which stand for data node and server node, are 
# native Yardstick parameters and are documented in Yardstick framework.
CONFIGS="-dn GeodePutBenchmark -sn GeodeNode"
```

## Issues
Use GitHub [issues](https://github.com/rdiyewar/yardstick-geode/issues) to file bugs.

## License
Yardstick Geode is available under [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Open Source license.
