package ru.hh.nab.starter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class NabApplicationTest {

  @Test
  public void runShouldStartJetty() {
    NabApplicationContext context = (NabApplicationContext) NabApplication.run(NabTestConfig.class);

    assertTrue(context.isServerRunning());
    assertEquals(NabTestConfig.TEST_SERVICE_NAME, context.getBean("serviceName"));
  }
}
