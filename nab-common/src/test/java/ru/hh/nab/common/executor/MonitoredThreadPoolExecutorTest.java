package ru.hh.nab.common.executor;

import org.junit.Test;
import ru.hh.nab.common.settings.NabSettings;
import ru.hh.nab.common.settings.TypesafeConfigLoader;
import ru.hh.nab.metrics.StatsDSender;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class MonitoredThreadPoolExecutorTest {
  @Test
  public void testRejecting() {
    var settingsLoader = TypesafeConfigLoader.fromMap(Map.of(
      "minSize", 4,
      "maxSize", 4
    ));

    var tpe = MonitoredThreadPoolExecutor.create(new NabSettings(settingsLoader), "test", mock(StatsDSender.class), "test");

    tpe.execute(TASK);
    tpe.execute(TASK);
    tpe.execute(TASK);
    tpe.execute(TASK);

    var rejected = false;

    try {
      IntStream.range(0, 5).forEach(i -> tpe.execute(TASK));
      fail("RejectedExecutionException not thrown");
    } catch (RejectedExecutionException e) {
      rejected = true;
    }

    assertTrue(rejected);
    LATCH.countDown();
  }

  private static final CountDownLatch LATCH = new CountDownLatch(1);
  private static final Runnable TASK = () -> {
    try {
      LATCH.await();
    } catch (InterruptedException e) {
      //
    }
  };
}
