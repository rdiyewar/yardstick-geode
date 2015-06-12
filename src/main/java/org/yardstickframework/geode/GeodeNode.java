/*
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package org.yardstickframework.geode;

import static org.yardstickframework.BenchmarkUtils.jcommander;
import static org.yardstickframework.BenchmarkUtils.println;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Set;

import org.yardstickframework.BenchmarkConfiguration;
import org.yardstickframework.BenchmarkServer;
import org.yardstickframework.BenchmarkUtils;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.server.CacheServer;
import com.gemstone.gemfire.distributed.DistributedMember;

/**
 * Standalone Geode node.
 */
public class GeodeNode implements BenchmarkServer {
  /** */
  public GeodeNode() {
    // No-op.
  }

  /** Geode cache */
  private Cache gemCache;

  /** {@inheritDoc} */
  @Override
  public void start(BenchmarkConfiguration cfg) throws Exception {
    GeodeBenchmarkArguments benchArgs = new GeodeBenchmarkArguments();
    jcommander(cfg.commandLineArguments(), benchArgs, "<geode-node>");
    println(cfg, "Geode benchmark config: [" + benchArgs + "].");

    configureAndStart(benchArgs, cfg);
    waitForNodes(benchArgs);

    println(cfg, "All Geode members started.");
  }

  /**
   * Configure Geode .
   *
   * @param benchArgs
   *          Arguments.
   */
  private void configureAndStart(GeodeBenchmarkArguments benchArgs,
      BenchmarkConfiguration cfg) {
    if (gemCache == null) {
      String pid = ManagementFactory.getRuntimeMXBean().getName().replace('@','-');
      gemCache = new CacheFactory()
          .set("mcast-port", "10111") 
          .set("statistic-archive-file", "stat-server-" + pid + ".gfs")
          .set("cache-xml-file", benchArgs.configuration())
          .create();
      
      //start cache server in client mode
      if(benchArgs.clientMode()){
        CacheServer cServer = gemCache.addCacheServer();
        int port = benchArgs.serverPort();
        int tryCnt = 0;
        boolean started = false;        
        while (!started && tryCnt < 100){
          try {
            cServer.setPort(port);
            cServer.start();
            started=true;
          }
          catch (IOException e) {
            println(cfg, "Unable to start cache server on port=" + port);
            port ++;
            tryCnt++;
          }  
        }
      } 
    }
  }

  /** {@inheritDoc} */
  @Override
  public void stop() throws Exception {
    gemCache.close();
    println("Stopped server node");
  }

  /** {@inheritDoc} */
  @Override
  public String usage() {
    return BenchmarkUtils.usage(new GeodeBenchmarkArguments());
  }

  /**
   * @throws Exception
   *           If failed.
   */
  private void waitForNodes(GeodeBenchmarkArguments benchArgs) throws Exception {
    boolean allStarted = false;
    while (!allStarted) {
      Set<DistributedMember> otherMembers = gemCache.getDistributedSystem()
          .getAllOtherMembers();
      int expNodes = benchArgs.clientMode() ? benchArgs.nodes() - 2 : benchArgs.nodes() - 1;
      if (otherMembers.size() >= expNodes) {
        allStarted = true;
      }
      else {
        println("Waiting for " + (benchArgs.nodes() - 1)
            + " server nodes to start...");
        Thread.sleep(1000);
      }
    }
  }
}
