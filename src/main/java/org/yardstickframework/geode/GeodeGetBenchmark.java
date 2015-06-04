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

import static org.yardstickframework.BenchmarkUtils.println;

import java.util.Map;

/**
 * Geode benchmark that performs get operations.
 */
public class GeodeGetBenchmark extends GeodeAbstractBenchmark {
  /** */
  public GeodeGetBenchmark() {
  }

  /** {@inheritDoc} */
  @Override
  public boolean test(Map<Object, Object> ctx) throws Exception {
    int key = nextRandom(args.range());

    testRegion.get(key);
    println("Get with key " + key);

    return true;
  }
}
