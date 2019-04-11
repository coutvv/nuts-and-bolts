package ru.hh.nab.common.settings;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static ru.hh.nab.common.settings.NabSettings.SETINGS_DIR_PROPERTY;

public class TypesafeConfigLoader implements Settings {

  private final Config config;

  private TypesafeConfigLoader(Config config) {
    this.config = config;
  }

  public static TypesafeConfigLoader fromConfig(String configName) {
    String settingsDir = System.getProperty(SETINGS_DIR_PROPERTY);
    File configFile = new File(settingsDir, configName);

    return new TypesafeConfigLoader(ConfigFactory.parseFileAnySyntax(configFile));
  }

  public static TypesafeConfigLoader fromMap(Map<String, Object> configMap) {
    return new TypesafeConfigLoader(ConfigFactory.parseMap(configMap));
  }

  public static TypesafeConfigLoader fromResource(String resourceName) {
    return new TypesafeConfigLoader(ConfigFactory.load(resourceName));
  }

  @Override
  public TypesafeConfigLoader withProperty(String path, Object value) {
    return new TypesafeConfigLoader(config.withValue(path, ConfigValueFactory.fromAnyRef(value)));
  }

  @Override
  public Optional<String> getString(String path) {
    return config.hasPath(path) ? of(config.getString(path)) : empty();
  }

  @Override
  public Optional<Integer> getInteger(String path) {
    return config.hasPath(path) ? of(config.getInt(path)) : empty();
  }

  @Override
  public Optional<Long> getLong(String path) {
    return config.hasPath(path) ? of(config.getLong(path)) : empty();
  }

  @Override
  public Optional<Boolean> getBoolean(String path) {
    return config.hasPath(path) ? of(config.getBoolean(path)) : empty();
  }

  @Override
  public List<String> getStringList(String path) {
    return config.hasPath(path) ? config.getStringList(path) : emptyList();
  }

  @Override
  public boolean isEmpty() {
    return config.isEmpty();
  }

  @Override
  public Properties getProperties() {
    Properties properties = new Properties();
    config.entrySet().forEach(
      item -> properties.put(item.getKey().replaceAll("\"", ""), String.valueOf(item.getValue().unwrapped()))
    );
    return properties;
  }

  @Override
  public TypesafeConfigLoader getSubSettings(String path) {
    return config.hasPath(path) ? new TypesafeConfigLoader(config.getConfig(path)) : new TypesafeConfigLoader(ConfigFactory.empty());
  }
}
