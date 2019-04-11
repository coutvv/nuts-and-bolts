package ru.hh.nab.testbase.hibernate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hh.nab.common.settings.NabSettings;
import ru.hh.nab.common.settings.TypesafeConfigLoader;
import ru.hh.nab.datasource.DataSourceFactory;
import ru.hh.nab.testbase.postgres.embedded.EmbeddedPostgresDataSourceFactory;

@Configuration
public class NabHibernateTestBaseConfig {
  @Bean
  DataSourceFactory dataSourceFactory() {
    return new EmbeddedPostgresDataSourceFactory();
  }

  @Bean
  NabSettings hibernateSettings() {
    return new NabSettings(TypesafeConfigLoader.fromResource("hibernate-test.properties"));
  }
}
