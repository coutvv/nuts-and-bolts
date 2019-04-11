package ru.hh.nab.common.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class NabSettingsTest {
  private static NabSettings nabSettings;

  @BeforeClass
  public static void setUpClass() {
    TypesafeConfigLoader loader = TypesafeConfigLoader.fromMap(Map.of(
      "strProperty", "value",
      "intProperty1", 0,
      "intProperty2", 123,
      "namespace.boolProperty1", true,
      "namespace.boolProperty2", false,
      "listProperty", List.of("value1", "value2", "value3")
    ));

    nabSettings = new NabSettings(loader);
  }

  @Test
  public void testGetString() {
    assertEquals("value", nabSettings.getString("strProperty").get());
    assertTrue(nabSettings.getString("missingKey").isEmpty());
  }

  @Test
  public void testGetInteger() {
    assertEquals(0, nabSettings.getInteger("intProperty1").get().intValue());
    assertEquals(123, nabSettings.getInteger("intProperty2").get().intValue());
  }

  @Test
  public void testGetLong() {
    assertEquals(0L, nabSettings.getLong("intProperty1").get().longValue());
    assertEquals(123L, nabSettings.getLong("intProperty2").get().longValue());
  }

  @Test
  public void testGetBoolean() {
    assertTrue(nabSettings.getBoolean("namespace.boolProperty1").get());
    assertFalse(nabSettings.getBoolean("namespace.boolProperty2").get());
  }

  @Test
  public void testGetSubSettings() {
    NabSettings subSettings = nabSettings.getSubSettings("namespace");

    assertTrue(subSettings.getString("boolProperty1").isPresent());
    assertTrue(subSettings.getString("boolProperty2").isPresent());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetSubSettingsThrowsExceptionIfPrefixIsEmpty() {
    nabSettings.getSubSettings("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetSubSettingsThrowsExceptionIfPrefixIsNull() {
    nabSettings.getSubSettings(null);
  }

  @Test
  public void testGetStringList() {
    List<String> settings = nabSettings.getStringList("listProperty");

    assertEquals(3, settings.size());
    assertEquals("value1", settings.get(0));
    assertEquals("value2", settings.get(1));
    assertEquals("value3", settings.get(2));
  }

  @Test
  public void testGetProperties() {
    Properties properties = nabSettings.getProperties();
    assertEquals("value", properties.getProperty("strProperty"));

    properties.setProperty("strProperty", "newValue");
    assertEquals("newValue", properties.getProperty("strProperty"));
    assertEquals("value", nabSettings.getString("strProperty").get());
  }
}
