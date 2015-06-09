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

import java.lang.management.ManagementFactory;
import java.util.concurrent.ThreadLocalRandom;

import org.yardstickframework.BenchmarkConfiguration;
import org.yardstickframework.BenchmarkDriverAdapter;
import org.yardstickframework.BenchmarkUtils;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.GemFireCache;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.client.PoolFactory;
import com.gemstone.gemfire.cache.client.PoolManager;


/**
 * Abstract class for Geode benchmarks.
 */
public abstract class GeodeAbstractBenchmark extends BenchmarkDriverAdapter {
    
    /** Arguments. */
    protected final GeodeBenchmarkArguments args = new GeodeBenchmarkArguments();

    /** Node. */
    private GeodeNode node;

    /** Cache. */
    protected GemFireCache gemCache;
    
    /** Gemfrie Region. */
    protected Region<Integer, SampleValue> testRegion;
    
    /**
     * @param cacheName Cache name.
     */
    protected GeodeAbstractBenchmark() {
    }

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        jcommander(cfg.commandLineArguments(), args, "<geode-driver>");
        configureGFClient(args, cfg);
        
        String mode = args.clientMode() ? "client" : "accessor";
        println("Geode " + mode + " configured. cache=" + gemCache.getName() + " region=" + testRegion.getFullPath());
        println(cfg, "Geode benchmark config: [" + cfg + "].");
    }

    /**
     * Configure Geode map.
     *
     * @param args Arguments.
     */
    private void configureGFClient(GeodeBenchmarkArguments args , BenchmarkConfiguration cfg) {
      String pid = ManagementFactory.getRuntimeMXBean().getName().replace('@','-');

      if(args.clientMode()){
        //client mode
        gemCache = new ClientCacheFactory()        
        .set("cache-xml-file", args.clientConfiguration())
        .set("statistic-archive-file", "stat-" + pid + ".gfs")
        .addPoolServer(serverHost(cfg), args.serverPort())
        .setPoolSubscriptionEnabled(true)
        .create();
        
      }else{
        //peer accessor mode
        gemCache = new CacheFactory()
        .set("mcast-port", "10111")        
        .set("cache-xml-file", args.accessorConfiguration())
        .set("statistic-archive-file", "stat-" + pid + ".gfs")
        .set("statistic-archive-file", "stat-" + pid + ".gfs")
        .create();
      } 
      
      testRegion = gemCache.getRegion("testRegion");
    }

    /** {@inheritDoc} */
    @Override public void tearDown() throws Exception {
      gemCache.close();
    }

    /** {@inheritDoc} */
    @Override public String description() {
        String desc = BenchmarkUtils.description(cfg, this);

        return desc.isEmpty() ?
            getClass().getSimpleName() + args.description() + cfg.defaultDescription() : desc;
    }

    /** {@inheritDoc} */
    @Override public String usage() {
        return BenchmarkUtils.usage(args);
    }

    /**
     * @param max Key range.
     * @return Next key.
     */
    protected int nextRandom(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    /**
     * @param min Minimum key in range.
     * @param max Maximum key in range.
     * @return Next key.
     */
    protected int nextRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min) + min;
    }
    
    /**
     * @return Server host name Client will connect to.
     * used only in client mode.
     */
    private String serverHost(BenchmarkConfiguration cfg) {
      String hosts = cfg.customProperties().get("SERVER_HOSTS");
      return hosts.split(",")[0];
    }
}
