package ru.hh.nab.starter.jersey;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import ru.hh.nab.starter.filters.ResourceInformationFilter;

public class MyFeature implements DynamicFeature {
  @Override
  public void configure(ResourceInfo resourceInfo, FeatureContext context) {
    context.register(ResourceInformationFilter.class);
  }
}
