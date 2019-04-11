package ru.hh.nab.starter.server.cache;

import org.eclipse.jetty.servlet.FilterHolder;
import ru.hh.nab.common.settings.NabSettings;
import ru.hh.nab.metrics.StatsDSender;

public class HttpCacheFilterFactory {
  private HttpCacheFilterFactory() {}

  public static FilterHolder createCacheFilterHolder(NabSettings nabSettings, String serviceName, StatsDSender statsDSender) {
    FilterHolder holder = new FilterHolder();
    nabSettings.getInteger("http.cache.sizeInMB")
      .ifPresent(size -> holder.setFilter(new CacheFilter(serviceName, size, statsDSender)));
    return holder;
  }
}
