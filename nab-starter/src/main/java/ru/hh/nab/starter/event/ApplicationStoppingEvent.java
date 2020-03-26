package ru.hh.nab.starter.event;

import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.context.ApplicationEvent;

public class ApplicationStoppingEvent extends ApplicationEvent {
  public ApplicationStoppingEvent(WebAppContext appContext) {
    super(appContext);
  }
}
