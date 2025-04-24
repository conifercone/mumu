/*
 * Copyright (c) 2024-2025, the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package baby.mumu.jmh;

import com.github.yitter.idgen.YitIdHelper;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Yitter#nextId基准测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.10.0
 */
@SuppressWarnings("unused")
// 吞吐量模式，单位时间内执行次数
@BenchmarkMode(Mode.Throughput)
// 输出结果单位
@OutputTimeUnit(TimeUnit.MILLISECONDS)
// 每个线程一个状态实例
@State(Scope.Thread)
// 每个 benchmark fork 一次 JVM
@Fork(1)
// 预热3次，每次1秒
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
// 正式执行5次，每次1秒
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
public class YitterNextIdBenchmark {

  @Benchmark
  public long nextId() {
    return YitIdHelper.nextId();
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
      .include(YitterNextIdBenchmark.class.getSimpleName())
      .result("./benchmark-history/YitterNextIdBenchmark.json")
      .resultFormat(ResultFormatType.JSON).build();
    new Runner(opt).run();
  }
}
