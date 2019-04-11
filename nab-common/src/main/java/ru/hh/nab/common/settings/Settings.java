package ru.hh.nab.common.settings;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

public interface Settings<T extends Settings> {
  Optional<String> getString(String key);
  Optional<Integer> getInteger(String key);
  Optional<Long> getLong(String key);
  Optional<Boolean> getBoolean(String key);
  List<String> getStringList(String key);

  boolean isEmpty();

  Properties getProperties();
  T getSubSettings(String path);
  T withProperty(String key, Object value);
}
