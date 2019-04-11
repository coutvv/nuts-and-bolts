package ru.hh.nab.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import static ru.hh.nab.datasource.DataSourceSettings.DEFAULT_VALIDATION_TIMEOUT_INCREMENT_MS;
import static ru.hh.nab.datasource.DataSourceSettings.JDBC_URL;
import static ru.hh.nab.datasource.DataSourceSettings.MONITORING_SEND_STATS;
import static ru.hh.nab.datasource.DataSourceSettings.PASSWORD;
import static ru.hh.nab.datasource.DataSourceSettings.POOL_SETTINGS_PREFIX;
import static ru.hh.nab.datasource.DataSourceSettings.STATEMENT_TIMEOUT_MS;
import static ru.hh.nab.datasource.DataSourceSettings.USER;

import ru.hh.nab.common.settings.NabSettings;
import ru.hh.nab.datasource.monitoring.MetricsTrackerFactoryProvider;
import ru.hh.nab.datasource.monitoring.StatementTimeoutDataSource;

public class DataSourceFactory {
  private final MetricsTrackerFactoryProvider metricsTrackerFactoryProvider;

  public DataSourceFactory(MetricsTrackerFactoryProvider metricsTrackerFactoryProvider) {
    this.metricsTrackerFactoryProvider = metricsTrackerFactoryProvider;
  }

  public DataSource create(String dataSourceName, boolean isReadonly, NabSettings settings) {
    return createDataSource(dataSourceName, isReadonly, settings.getSubSettings(dataSourceName));
  }

  public DataSource create(HikariConfig hikariConfig, NabSettings dataSourceSettings) {
    boolean sendStats = dataSourceSettings.getBoolean(MONITORING_SEND_STATS).orElse(false);
    if (sendStats && metricsTrackerFactoryProvider != null) {
      hikariConfig.setMetricsTrackerFactory(metricsTrackerFactoryProvider.create(dataSourceSettings));
    }

    DataSource baseHikariDataSource = new HikariDataSource(hikariConfig);

    DataSource hikariDataSource = dataSourceSettings.getInteger(STATEMENT_TIMEOUT_MS)
      .filter(statementTimeoutMs -> statementTimeoutMs > 0)
      .map(statementTimeoutMs -> (DataSource) new StatementTimeoutDataSource(baseHikariDataSource, statementTimeoutMs))
      .orElse(baseHikariDataSource);

    checkDataSource(hikariDataSource, hikariConfig.getPoolName());

    return hikariDataSource;
  }

  protected DataSource createDataSource(String dataSourceName, boolean isReadonly, NabSettings dataSourceSettings) {
    HikariConfig hikariConfig = createBaseHikariConfig(dataSourceName, isReadonly, dataSourceSettings);
    return create(hikariConfig, dataSourceSettings);
  }

  private static HikariConfig createBaseHikariConfig(String dataSourceName, boolean isReadonly, NabSettings dataSourceSettings) {
    NabSettings poolSettings = dataSourceSettings.getSubSettings(POOL_SETTINGS_PREFIX);
    if (poolSettings.isEmpty()) {
      throw new RuntimeException(String.format(
        "Exception during %1$s pooled datasource initialization: could not find %1$s.%2$s settings in config file. " +
        "To prevent misconfiguration application startup will be aborted.",
        dataSourceName, POOL_SETTINGS_PREFIX
      ));
    }

    HikariConfig config = new HikariConfig(poolSettings.getProperties());
    dataSourceSettings.getString(JDBC_URL).ifPresent(config::setJdbcUrl);
    dataSourceSettings.getString(USER).ifPresent(config::setUsername);
    dataSourceSettings.getString(PASSWORD).ifPresent(config::setPassword);
    config.setPoolName(dataSourceName);
    config.setReadOnly(isReadonly);
    config.setValidationTimeout(config.getConnectionTimeout() + DEFAULT_VALIDATION_TIMEOUT_INCREMENT_MS);
    return config;
  }

  private static void checkDataSource(DataSource dataSource, String dataSourceName) {
    try (Connection connection = dataSource.getConnection()) {
      if (!connection.isValid(1000)) {
        throw new RuntimeException("Invalid connection to " + dataSourceName);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to check data source " + dataSourceName + ": " + e.toString());
    }
  }
}
