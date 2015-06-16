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

import org.yardstickframework.BenchmarkConfiguration;
import org.yardstickframework.BenchmarkServer;
import org.yardstickframework.BenchmarkUtils;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.control.RebalanceFactory;
import com.gemstone.gemfire.cache.control.RebalanceOperation;
import com.gemstone.gemfire.cache.control.RebalanceResults;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import com.gemstone.gemfire.cache.server.CacheServer;
import com.gemstone.gemfire.internal.cache.PartitionedRegion;

/**
 * Stand alone Geode node.
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
  }

  /**
   * Configure Geode .
   *
   * @param benchArgs
   *          Arguments.
   * @throws InterruptedException 
   */
  private void configureAndStart(GeodeBenchmarkArguments benchArgs,
      BenchmarkConfiguration cfg) throws InterruptedException {
    if (gemCache == null) {
      String pid = ManagementFactory.getRuntimeMXBean().getName().replace('@','-');
      gemCache = new CacheFactory()
          .set("mcast-port", "10111") 
          .set("statistic-archive-file", "stat-server-" + pid + ".gfs")
          .set("cache-xml-file", benchArgs.configuration())
          .create();
   
      startCacheServer(benchArgs);
      createBuckets();
      makeServerReady();
    }
  }

  private void startCacheServer(GeodeBenchmarkArguments benchArgs){
    CacheServer cServer = gemCache.addCacheServer();
    int port = benchArgs.serverPort();
    int tryCnt = 0;
    boolean started = false;
    while (!started && tryCnt < 100) {
      try {
        cServer.setPort(port);
        cServer.start();
        started = true;
      }
      catch (IOException e) {
        println("Unable to start cache server on port=" + port);
        port++;
        tryCnt++;
      }
    }
  }
  
  private void createBuckets() throws InterruptedException {
    if (gemCache != null) {
      for (Region r : gemCache.rootRegions()) {
        if (r.getAttributes().getDataPolicy().withPartitioning()) {
          println("Creating buckets for region " + r.getName());
          PartitionRegionHelper.assignBucketsToPartitions(r);
          RebalanceFactory rf = gemCache.getResourceManager()
              .createRebalanceFactory();
          RebalanceOperation ro = rf.start();
          RebalanceResults results = ro.getResults(); // blocking call
          println("Created buckets for region " + r.getName());
        }
        else {
          println("Skipping region " + r.getName() + " with data policy "
              + r.getAttributes().getDataPolicy());
        }
      }
    }
  }
  
  private void makeServerReady() {
    PartitionedRegion r = (PartitionedRegion)gemCache.getRegion("testRegion");
    String key = Constant.SERVER + gemCache.getDistributedSystem().getDistributedMember().getId();    
    r.put(key, new Integer(1));
    println("Server is ready " + key);
  }
  
  /** {@inheritDoc} */
  @Override
  public void stop() throws Exception {
    gemCache.close();
    println("Stopped server node ");
  }

  /** {@inheritDoc} */
  @Override
  public String usage() {
    return BenchmarkUtils.usage(new GeodeBenchmarkArguments());
  }
}
