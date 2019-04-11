package ru.hh.nab.starter;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

import org.eclipse.jetty.servlet.FilterHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.hh.nab.common.settings.NabSettings;
import ru.hh.nab.common.settings.TypesafeConfigLoader;
import ru.hh.nab.metrics.StatsDSender;

import static ru.hh.nab.starter.server.cache.HttpCacheFilterFactory.createCacheFilterHolder;

@Configuration
@Import({NabCommonConfig.class})
public class NabProdConfig {
  static final String PROPERTIES_FILE_NAME = "service.properties";
  static final String DATACENTER_NAME_PROPERTY = "datacenter";

  @Bean
  NabSettings nabSettings() {
    return new NabSettings(TypesafeConfigLoader.fromConfig(PROPERTIES_FILE_NAME));
  }

  @Bean
  String datacenter(NabSettings fileSettings) {
    return fileSettings.getString(DATACENTER_NAME_PROPERTY)
      .orElseThrow(() -> new RuntimeException(String.format("'%s' property is not found in file settings", DATACENTER_NAME_PROPERTY)));
  }

  @Bean
  StatsDClient statsDClient() {
    return new NonBlockingStatsDClient(null, "localhost", 8125, 10000);
  }

  @Bean
  FilterHolder cacheFilter(NabSettings nabSettings, String serviceName, StatsDSender statsDSender) {
    return createCacheFilterHolder(nabSettings, serviceName, statsDSender);
  }
}
