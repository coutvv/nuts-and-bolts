package ru.hh.nab.hibernate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.hh.nab.common.settings.NabSettings;
import ru.hh.nab.common.settings.TypesafeConfigLoader;
import ru.hh.nab.datasource.DataSourceFactory;
import ru.hh.nab.datasource.DataSourceType;
import ru.hh.nab.hibernate.model.TestEntity;
import ru.hh.nab.testbase.hibernate.NabHibernateTestBaseConfig;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@Import({
  NabHibernateCommonConfig.class,
  NabHibernateTestBaseConfig.class
})
public class HibernateTestConfig {
  static final String TEST_PACKAGE = "ru.hh.nab.hibernate.model.test";

  @Bean
  MappingConfig mappingConfig() {
    MappingConfig mappingConfig = new MappingConfig(TestEntity.class);
    mappingConfig.addPackagesToScan(TEST_PACKAGE);
    return mappingConfig;
  }

  @Bean
  NabSettings nabSettings() {
    return new NabSettings(TypesafeConfigLoader.fromMap(Map.of("master.pool.maximumPoolSize", 2)));
  }

  @Bean
  DataSource dataSource(DataSourceFactory dataSourceFactory, NabSettings nabSettings) {
    return dataSourceFactory.create(DataSourceType.MASTER, false, nabSettings);
  }
}
