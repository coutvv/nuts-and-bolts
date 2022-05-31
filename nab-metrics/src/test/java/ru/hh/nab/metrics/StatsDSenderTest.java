package ru.hh.nab.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class StatsDSenderTest {
  @Test
  public void testGetTagStringNullTags() {
    assertEquals("metricName", StatsDSender.getFullMetricName("metricName", null));
  }

  @Test
  public void testGetTagStringNoTags() {
    assertEquals("metricName", StatsDSender.getFullMetricName("metricName", new Tag[]{}));
  }

  @Test
  public void testGetTagStringOneTag() {
    assertEquals("metricName.label_is_right", StatsDSender.getFullMetricName("metricName", new Tag[]{new Tag("label", "right")}));
  }

  @Test
  public void testGetTagStringTwoTags() {
    assertEquals(
      "metricName.label_is_right.answer_is_42",
      StatsDSender.getFullMetricName(
        "metricName",
        new Tag[]{new Tag("label", "right"), new Tag("answer", "42")}
      )
    );
  }

  @Test
  public void testGetTagStringTwoTagsAnotherOrder() {
    assertEquals(
      "metricName.answer_is_42.label_is_right",
      StatsDSender.getFullMetricName(
        "metricName",
        new Tag[]{new Tag("answer", "42"), new Tag("label", "right")}
      )
    );
  }

  @Test
  public void testGetTagStringSkipNullTagName() {
    assertEquals(
      "metricName",
      StatsDSender.getFullMetricName("metricName", new Tag[]{new Tag(null, "42")})
    );
  }

  @Test
  public void testGetTagStringSkipAllNullTagName() {
    assertEquals(
        "metricName.answer_is_42.label_is_right",
        StatsDSender.getFullMetricName(
          "metricName",
          new Tag[]{
            new Tag(null, null),
            new Tag("answer", "42"),
            new Tag(null, "43"),
            new Tag("label", "right"),
            new Tag(null, "44")
          })
    );
  }

  @Test
  public void testGetTagStringNullTagValue() {
    assertEquals(
        "metricName.answer_is_null",
        StatsDSender.getFullMetricName("metricName", new Tag[]{new Tag("answer", null)})
    );
  }
}
