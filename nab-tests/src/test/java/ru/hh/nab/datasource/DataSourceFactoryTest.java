package ru.hh.nab.datasource;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.junit.BeforeClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import ru.hh.nab.common.settings.NabSettings;
import ru.hh.nab.common.settings.Settings;
import ru.hh.nab.common.settings.TypesafeConfigLoader;
import ru.hh.nab.datasource.monitoring.NabMetricsTrackerFactoryProvider;
import ru.hh.nab.datasource.monitoring.StatementTimeoutDataSource;
import ru.hh.nab.metrics.StatsDSender;
import ru.hh.nab.testbase.postgres.embedded.EmbeddedPostgresDataSourceFactory;

import static ru.hh.nab.datasource.DataSourceSettings.MONITORING_LONG_CONNECTION_USAGE_MS;
import static ru.hh.nab.datasource.DataSourceSettings.MONITORING_SEND_SAMPLED_STATS;
import static ru.hh.nab.datasource.DataSourceSettings.MONITORING_SEND_STATS;
import static ru.hh.nab.datasource.DataSourceSettings.STATEMENT_TIMEOUT_MS;
import static ru.hh.nab.testbase.NabTestConfig.TEST_SERVICE_NAME;

public class DataSourceFactoryTest {
  private static final String TEST_DATA_SOURCE_TYPE = DataSourceType.MASTER;

  private static EmbeddedPostgres testDb;
  private static DataSourceFactory dataSourceFactory;

  @BeforeClass
  public static void setUpClass() {
    testDb = EmbeddedPostgresDataSourceFactory.getEmbeddedPostgres();
    dataSourceFactory = new DataSourceFactory(new NabMetricsTrackerFactoryProvider(TEST_SERVICE_NAME, mock(StatsDSender.class)));
  }

  @Test
  public void testCreateDataSourceWithIncompleteSettings() {
    try {
      createTestDataSource(createIncompleteTestSettings());
      fail();
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("master.pool"));
    }
  }

  @Test
  public void testCreateDataSource() {
    HikariDataSource dataSource = (HikariDataSource) createTestDataSource(createTestSettings());
    assertEquals(TEST_DATA_SOURCE_TYPE, dataSource.getPoolName());
  }

  @Test
  public void testCreateStatementTimeoutDataSource() {
    var settings = createTestSettings()
      .withProperty(getProperty(STATEMENT_TIMEOUT_MS), "100");

    assertTrue(createTestDataSource(settings) instanceof StatementTimeoutDataSource);
  }

  @Test
  public void testCreateDataSourceWithMetrics() {
    var settings = createTestSettings()
      .withProperty(getProperty(MONITORING_SEND_STATS), "true")
      .withProperty(getProperty(MONITORING_LONG_CONNECTION_USAGE_MS), "10")
      .withProperty(getProperty(MONITORING_SEND_SAMPLED_STATS), "true");

    HikariDataSource dataSource = (HikariDataSource) createTestDataSource(settings);
    assertNotNull(dataSource.getMetricsTrackerFactory());
  }

  private static DataSource createTestDataSource(Settings settings) {
    return dataSourceFactory.create(TEST_DATA_SOURCE_TYPE, false, new NabSettings(settings));
  }

  private static Settings createTestSettings() {
    return createIncompleteTestSettings()
      .withProperty(getProperty(DataSourceSettings.POOL_SETTINGS_PREFIX + ".maximumPoolSize"), "2");
  }

  private static Settings createIncompleteTestSettings() {
    final StringSubstitutor jdbcUrlParamsSubstitutor = new StringSubstitutor(Map.of(
            "port", testDb.getPort(),
            "host", "localhost",
            "user", EmbeddedPostgresDataSourceFactory.DEFAULT_USER
    ));

    return new NabSettings(TypesafeConfigLoader.fromMap(Map.of(
      getProperty(DataSourceSettings.JDBC_URL), jdbcUrlParamsSubstitutor.replace(EmbeddedPostgresDataSourceFactory.DEFAULT_JDBC_URL),
      getProperty(DataSourceSettings.USER), EmbeddedPostgresDataSourceFactory.DEFAULT_USER,
      getProperty(DataSourceSettings.PASSWORD), EmbeddedPostgresDataSourceFactory.DEFAULT_USER
    )));
  }

  private static String getProperty(String propertyName) {
    return String.format("%s.%s", TEST_DATA_SOURCE_TYPE, propertyName);
  }

  private static String getMetricName(String metricName) {
    return String.format("%s.%s.%s", TEST_SERVICE_NAME, TEST_DATA_SOURCE_TYPE, metricName);
  }
}
