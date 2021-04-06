package ru.hh.jclient.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hh.nab.starter.NabCommonConfig;

import javax.inject.Named;

@Configuration
class TestConfig {

  @Bean
  ScheduledExecutorService scheduledExecutorService() {
    return Executors.newScheduledThreadPool(1);
  }

  @Named(NabCommonConfig.SERVICE_NAME_PROPERTY)
  @Bean
  String serviceName() {
    return "test";
  }

  @Named(NabCommonConfig.DATACENTER_NAME_PROPERTY)
  @Bean
  String datacenter() {
    return "test";
  }

  @Named(NabCommonConfig.NODE_NAME_PROPERTY)
  @Bean
  String nodeName() {
    return "test";
  }
}
