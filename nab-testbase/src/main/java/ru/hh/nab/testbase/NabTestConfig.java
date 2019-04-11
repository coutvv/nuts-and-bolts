package ru.hh.nab.testbase;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import ru.hh.nab.common.settings.NabSettings;
import ru.hh.nab.common.settings.TypesafeConfigLoader;
import ru.hh.nab.metrics.StatsDSender;
import ru.hh.nab.starter.NabCommonConfig;

import static ru.hh.nab.starter.server.jetty.JettyServerFactory.createJettyThreadPool;

@Configuration
@Import({NabCommonConfig.class})
public class NabTestConfig {
  public static final String TEST_SERVICE_NAME = "testService";

  @Bean
  NabSettings nabSettings() {
    return new NabSettings(TypesafeConfigLoader.fromResource("service-test.properties"));
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  ThreadPool jettyThreadPool(NabSettings nabSettings, String serviceName, StatsDSender statsDSender) throws Exception {
    return createJettyThreadPool(nabSettings.getSubSettings("jetty"), serviceName, statsDSender);
  }

  @Bean
  StatsDClient statsDClient() {
    return new NoOpStatsDClient();
  }
}
