package ru.hh.nab.common.settings;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static ru.hh.nab.common.settings.NabSettings.SETINGS_DIR_PROPERTY;

import org.junit.Before;
import org.junit.Test;

public class TypesafeConfigLoaderTest {
  private static final String TMP_DIR = System.getProperty("java.io.tmpdir");
  private Path propertiesFile;

  @Before
  public void setUp() throws Exception {
    System.setProperty(SETINGS_DIR_PROPERTY, TMP_DIR);
    propertiesFile = Files.createTempFile("settings", ".properties");
  }

  @After
  public void tearDown() throws Exception {
    System.clearProperty(SETINGS_DIR_PROPERTY);
    Files.deleteIfExists(propertiesFile);
  }

  @Test
  public void fromFilesInSettingsDirShouldLoadProperties() throws Exception {
    String testKey = "testProperty";
    String testValue = "123";
    Files.write(propertiesFile, String.format("%s=%s", testKey, testValue).getBytes());

    TypesafeConfigLoader loader = TypesafeConfigLoader.fromConfig(propertiesFile.getFileName().toString());

    assertEquals(testValue, loader.getString(testKey).get());
  }
}
