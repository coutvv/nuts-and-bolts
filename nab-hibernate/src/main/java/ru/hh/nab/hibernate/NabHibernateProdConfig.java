package ru.hh.nab.hibernate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.hh.nab.common.settings.NabSettings;
import ru.hh.nab.common.settings.TypesafeConfigLoader;
import ru.hh.nab.datasource.NabDataSourceProdConfig;

@Configuration
@Import({NabHibernateCommonConfig.class, NabDataSourceProdConfig.class})
public class NabHibernateProdConfig {

  @Bean
  NabSettings hibernateSettings() {
    return new NabSettings(TypesafeConfigLoader.fromConfig("hibernate.properties"));
  }
}
