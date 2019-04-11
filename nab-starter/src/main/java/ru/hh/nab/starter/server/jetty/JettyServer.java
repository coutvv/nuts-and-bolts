package ru.hh.nab.starter.server.jetty;

import javax.servlet.ServletContext;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import ru.hh.nab.common.settings.NabSettings;
import ru.hh.nab.starter.server.logging.StructuredRequestLogger;

public final class JettyServer {
  public static final String JETTY = "jetty";
  public static final String PORT = "port";
  public static final String JETTY_PORT = String.join(".", JETTY, PORT);

  private static final int DEFAULT_CONNECTION_IDLE_TIMEOUT_MS = 3_000;
  private static final int DEFAULT_ACCEPT_QUEUE_SIZE = 50;
  private static final int DEFAULT_STOP_TIMEOUT_MS = 5_000;

  private static final Logger LOGGER = LoggerFactory.getLogger(JettyServer.class);

  private final NabSettings jettySettings;
  private final Server server;
  private final ServletContextHandler servletContextHandler;

  JettyServer(ThreadPool threadPool, NabSettings jettySettings, ServletContextHandler servletContextHandler) {
    this.jettySettings = jettySettings;

    server = new Server(threadPool);
    configureConnector();
    configureRequestLogger();
    configureStopTimeout();
    this.servletContextHandler = servletContextHandler;
    server.setHandler(servletContextHandler);
  }

  public void start() throws JettyServerException {
    try {
      server.start();
      server.setStopAtShutdown(true);

      LOGGER.info("Jetty started on port {}", getPort());
    } catch (Exception e) {
      stopSilently();
      String msg = jettySettings.getInteger("port").filter(port -> port != 0).map(port -> ", port=" + port).orElse("");
      throw new JettyServerException("Unable to start Jetty server" + msg, e);
    }
  }

  public void stop() throws JettyServerException {
    try {
      server.stop();
    } catch (Exception e) {
      throw new JettyServerException("Unable to stop Jetty server", e);
    }
  }

  public int getPort() {
    Optional<ServerConnector> serverConnector = getServerConnector();
    if (!serverConnector.isPresent()) {
      LOGGER.warn("Unable to obtain port number - server connector is not present");
      return 0;
    }
    return serverConnector.get().getLocalPort();
  }

  public boolean isRunning() {
    return server.isRunning();
  }

  private void configureConnector() {
    ServerConnector serverConnector = new HHServerConnector(
      server,
      jettySettings.getInteger("acceptors").orElse(-1),
      jettySettings.getInteger("selectors").orElse(-1),
      createHttpConnectionFactory());

    jettySettings.getString("host").ifPresent(serverConnector::setHost);
    jettySettings.getInteger(PORT).ifPresent(serverConnector::setPort);
    serverConnector.setIdleTimeout(jettySettings.getInteger("connectionIdleTimeoutMs").orElse(DEFAULT_CONNECTION_IDLE_TIMEOUT_MS));
    serverConnector.setAcceptQueueSize(jettySettings.getInteger("acceptQueueSize").orElse(DEFAULT_ACCEPT_QUEUE_SIZE));

    server.addConnector(serverConnector);
  }

  private void configureRequestLogger() {
    server.setRequestLog(new StructuredRequestLogger());
  }

  private void configureStopTimeout() {
    server.setStopTimeout(jettySettings.getInteger("stopTimeoutMs").orElse(DEFAULT_STOP_TIMEOUT_MS));
  }

  private static HttpConnectionFactory createHttpConnectionFactory() {
    final HttpConfiguration httpConfiguration = new HttpConfiguration();
    httpConfiguration.setSecurePort(8443);
    httpConfiguration.setOutputBufferSize(65536);
    httpConfiguration.setRequestHeaderSize(16384);
    httpConfiguration.setResponseHeaderSize(65536);
    httpConfiguration.setSendServerVersion(false);
    httpConfiguration.setBlockingTimeout(5000);
    return new HttpConnectionFactory(httpConfiguration);
  }

  private Optional<ServerConnector> getServerConnector() {
    Connector[] connectors = server.getConnectors();
    for (Connector connector : connectors) {
      if (connector instanceof ServerConnector) {
        return Optional.of((ServerConnector) connector);
      }
    }
    return Optional.empty();
  }

  private void stopSilently() {
    try {
      server.stop();
    } catch (Exception e) {
      // ignore
    }
  }

  public Server getServer() {
    return server;
  }

  public ServletContext getServletContext() {
    return servletContextHandler.getServletContext();
  }
}
