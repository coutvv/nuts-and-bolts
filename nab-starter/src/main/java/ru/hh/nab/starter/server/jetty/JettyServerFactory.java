package ru.hh.nab.starter.server.jetty;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import ru.hh.nab.common.properties.FileSettings;
import ru.hh.nab.metrics.StatsDSender;
import ru.hh.nab.starter.servlet.WebAppInitializer;

import java.time.Duration;

import static java.util.Optional.ofNullable;
import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;
import static ru.hh.nab.starter.server.jetty.JettyServer.JETTY;

public final class JettyServerFactory {

  private static final int DEFAULT_IDLE_TIMEOUT_MS = (int) Duration.ofMinutes(1).toMillis();

  public static JettyServer create(FileSettings fileSettings, ThreadPool threadPool, WebAppInitializer webAppInitializer) {
    FileSettings jettySettings = fileSettings.getSubSettings(JETTY);
    ServletContextHandler contextHandler = createWebAppContextHandler(jettySettings, webAppInitializer);
    return new JettyServer(threadPool, jettySettings, contextHandler);
  }

  private static ServletContextHandler createWebAppContextHandler(FileSettings jettySettings, WebAppInitializer webAppInitializer) {
    boolean sessionEnabled = ofNullable(jettySettings.getBoolean("session-manager.enabled")).orElse(Boolean.FALSE);
    WebAppContext webAppContext = new WebAppContext(
      null, null, null, null, null, null, sessionEnabled ? SESSIONS : 0
    );
    webAppInitializer.configureWebApp(webAppContext);
    webAppContext.setThrowUnavailableOnStartupException(true);
    return webAppContext;
  }

  public static MonitoredQueuedThreadPool createJettyThreadPool(FileSettings jettySettings,
                                                                String serviceName, StatsDSender statsDSender) throws Exception {
    int minThreads = ofNullable(jettySettings.getInteger("minThreads")).orElse(4);
    int maxThreads = ofNullable(jettySettings.getInteger("maxThreads")).orElse(12);
    int queueSize = ofNullable(jettySettings.getInteger("queueSize")).orElse(maxThreads);
    int idleTimeoutMs = ofNullable(jettySettings.getInteger("threadPoolIdleTimeoutMs")).orElse(DEFAULT_IDLE_TIMEOUT_MS);

    MonitoredQueuedThreadPool threadPool = new MonitoredQueuedThreadPool(
      maxThreads, minThreads, idleTimeoutMs, new BlockingArrayQueue<>(queueSize), serviceName, statsDSender
    );
    threadPool.start();
    return threadPool;
  }

  private JettyServerFactory() {
  }
}
