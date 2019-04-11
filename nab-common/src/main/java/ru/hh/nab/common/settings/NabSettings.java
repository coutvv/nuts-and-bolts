package ru.hh.nab.common.settings;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.springframework.util.Assert.hasLength;

public final class NabSettings implements Settings<NabSettings> {
  public static final String SETINGS_DIR_PROPERTY = "settingsDir";

  private final Settings<?> loader;

  public NabSettings(Settings<?> loader) {
    this.loader = loader;
  }

  @Override
  public Optional<String> getString(String key) {
    return loader.getString(key);
  }

  @Override
  public Optional<Integer> getInteger(String key) {
    return loader.getInteger(key);
  }

  @Override
  public Optional<Long> getLong(String key) {
    return loader.getLong(key);
  }

  @Override
  public Optional<Boolean> getBoolean(String key) {
    return loader.getBoolean(key);
  }

  @Override
  public List<String> getStringList(String key) {
    return loader.getStringList(key);
  }

  @Override
  public boolean isEmpty() {
    return loader.isEmpty();
  }

  @Override
  public Properties getProperties() {
    return loader.getProperties();
  }

  @Override
  public NabSettings getSubSettings(String prefix) {
    hasLength(prefix, "prefix should not be null or empty");
    return new NabSettings(loader.getSubSettings(prefix));
  }

  @Override
  public NabSettings withProperty(String key, Object value) {
    return new NabSettings(loader.withProperty(key, value));
  }

  public Settings getLoader() {
    return loader;
  }
}
