package ru.hh.nab.starter.event;

import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.context.ApplicationEvent;

public class ApplicationStartedEvent extends ApplicationEvent {
  public ApplicationStartedEvent(WebAppContext appContext) {
    super(appContext);
  }
}
