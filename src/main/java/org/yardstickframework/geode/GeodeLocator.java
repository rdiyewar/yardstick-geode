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

import org.yardstickframework.BenchmarkUtils;

import com.gemstone.gemfire.distributed.LocatorLauncher;
import com.gemstone.gemfire.distributed.LocatorLauncher.LocatorState;

public class GeodeLocator {

  private LocatorLauncher locatorLauncher;

  public GeodeLocator() {
    locatorLauncher = new LocatorLauncher.Builder().setMemberName("locator1")
        .setPort(13489).build();
  }

  public LocatorState startLocator() {
    LocatorState state = locatorLauncher.start();
    locatorLauncher.waitOnLocator();
    BenchmarkUtils.println("Started Geode locator " + locatorLauncher.getId());
    return state;
  }

  public LocatorState stopLocator() {
    return locatorLauncher.stop();
  }
}
