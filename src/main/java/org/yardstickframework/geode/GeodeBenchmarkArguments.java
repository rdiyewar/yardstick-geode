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

import com.beust.jcommander.Parameter;

/**
 * Input arguments for geode benchmarks.
 */
@SuppressWarnings({ "UnusedDeclaration", "FieldCanBeLocal" })
public class GeodeBenchmarkArguments {
  /** */
  @Parameter(names = { "-nn", "--nodeNumber" }, description = "Node number")
  private int nodes = 1;

  /* unused */
  @Parameter(names = { "-b", "--backups" }, description = "Backups")
  private int backups;

  @Parameter(names = { "-gfcfg", "--gfConfig" }, description = "Configuration file")
  private String gfCfg = "config/geode-config.xml";

  @Parameter(names = { "-gfclientcfg", "--gfClientConfig" }, description = "Client configuration file")
  private String gfClientCfg = "config/geode-client-config.xml";

  @Parameter(names = { "-gfacfg", "--gfAccessorConfig" }, description = "Client configuration file")
  private String gfAccessorCfg = "config/geode-accessor-config.xml";
  
  /** */
  @Parameter(names = { "-cm", "--clientMode" }, description = "Client mode")
  private boolean clientMode = false;

  /** */
  @Parameter(names = { "-sp", "--serverPort" }, description = "Server port client will connect to")
  private int serverPort;

  
  /** */
  @Parameter(names = { "-r", "--range" }, description = "Key range")
  private int range = 1_000_000;

  /**
   * @return Client mode.
   */
  public boolean clientMode() {
    return clientMode;
  }
  
  /**
   * @return Server port client will connect to.
   */
  public int serverPort() {
    return serverPort;
  }
  
  /**
   * @return Backups.
   */
  public int backups() {
    return backups;
  }

  /**
   * @return Total number of nodes under test, server + driver nodes.
   */
  public int nodes() {
    return nodes;
  }

  /**
   * @return Key range, from {@code 0} to this number.
   */
  public int range() {
    return range;
  }

  /**
   * @return Configuration file.
   */
  public String configuration() {
    return gfCfg;
  }

  /**
   * @return Client Configuration file.
   */
  public String clientConfiguration() {
    return gfClientCfg;
  }

  /**
   * @return Accessor Configuration file.
   */
  public String accessorConfiguration() {
    return gfAccessorCfg;
  }
  
  /**
   * @return Description.
   */
  public String description() {
    return "-nn=" + nodes + "-b=" + backups + "-cm=" + clientMode;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    StringBuffer str = new StringBuffer(getClass().getSimpleName());
    str.append(" [nodes=" + nodes)
        .append(", backups=" + backups)
        .append(", clientMode=" + clientMode)
        .append(", gfConfig='" + gfCfg + '\'');

    if (clientMode) {
      str.append(", gfClientCfg='" + gfClientCfg + '\'')          
          .append(", serverPort=" + serverPort);
    }
    else {
      str.append(", gfAccessorConfig='" + gfAccessorCfg + '\'');
    }
    
    str.append(", range=" + range + ']');

    return str.toString();
  }
}
