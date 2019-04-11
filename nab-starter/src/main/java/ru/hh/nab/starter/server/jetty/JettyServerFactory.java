package ru.hh.nab.starter.server.jetty;

import java.time.Duration;
import static ru.hh.nab.starter.server.jetty.JettyServer.JETTY;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.ThreadPool;
import ru.hh.nab.common.settings.NabSettings;
import ru.hh.nab.metrics.StatsDSender;
import ru.hh.nab.starter.servlet.WebAppInitializer;

public final class JettyServerFactory {

  private static final int DEFAULT_IDLE_TIMEOUT_MS = (int) Duration.ofMinutes(1).toMillis();

  public static JettyServer create(NabSettings nabSettings, ThreadPool threadPool, WebAppInitializer webAppInitializer) {
    NabSettings jettySettings = nabSettings.getSubSettings(JETTY);
    ServletContextHandler contextHandler = createWebAppContextHandler(jettySettings, webAppInitializer);
    return new JettyServer(threadPool, jettySettings, contextHandler);
  }

  private static ServletContextHandler createWebAppContextHandler(NabSettings jettySettings, WebAppInitializer webAppInitializer) {
    boolean sessionEnabled = jettySettings.getBoolean("session-manager.enabled").orElse(Boolean.FALSE);
    return new JettyWebAppContext(webAppInitializer, sessionEnabled);
  }

  public static MonitoredQueuedThreadPool createJettyThreadPool(NabSettings jettySettings,
                                                                String serviceName, StatsDSender statsDSender) throws Exception {
    int maxThreads = jettySettings.getInteger("maxThreads").orElse(12);
    int minThreads = jettySettings.getInteger("minThreads").orElse(maxThreads);
    int queueSize = jettySettings.getInteger("queueSize").orElse(maxThreads);
    int idleTimeoutMs = jettySettings.getInteger("threadPoolIdleTimeoutMs").orElse(DEFAULT_IDLE_TIMEOUT_MS);

    MonitoredQueuedThreadPool threadPool = new MonitoredQueuedThreadPool(
      maxThreads, minThreads, idleTimeoutMs, new BlockingArrayQueue<>(queueSize), serviceName, statsDSender
    );
    threadPool.start();
    return threadPool;
  }

  private JettyServerFactory() {
  }
}
