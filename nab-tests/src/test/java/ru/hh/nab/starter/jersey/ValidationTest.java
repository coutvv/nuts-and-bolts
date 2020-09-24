package ru.hh.nab.starter.jersey;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.hh.nab.starter.NabApplication;
import ru.hh.nab.testbase.NabTestConfig;
import ru.hh.nab.testbase.ResourceHelper;
import ru.hh.nab.testbase.extensions.NabJunitWebConfig;
import ru.hh.nab.testbase.extensions.NabTestServer;
import ru.hh.nab.testbase.extensions.OverrideNabApplication;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@NabJunitWebConfig(NabTestConfig.class)
public class ValidationTest {
  @NabTestServer(overrideApplication = SpringCtxForJersey.class)
  ResourceHelper resourceHelper;

  @Test
  public void testValidation() {
    var response = resourceHelper.createRequest("/integerTest?arg=C").accept(APPLICATION_JSON).get();
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatusInfo().getStatusCode());
    response = resourceHelper.createRequest("/enumTest?arg=C").accept(APPLICATION_JSON).get();
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatusInfo().getStatusCode());
  }

  @Configuration
  @Import(ValidationResource.class)
  public static class SpringCtxForJersey implements OverrideNabApplication {
    @Override
    public NabApplication getNabApplication() {
      return NabApplication.builder().configureJersey(SpringCtxForJersey.class)
          .registerResources(ObjectMapperContextResolver.class)
          .bindToRoot().build();
    }
  }
  @Path("/")
  @Produces({APPLICATION_JSON})
  public static class ValidationResource {
    @Path("integerTest")
    @GET
    public String integerTest(@QueryParam("arg") Integer arg) {
      return "ok";
    }

    @Path("enumTest")
    @GET
    public String enumTest(@QueryParam("arg") Value arg) {
      return "ok";
    }
  }

  public enum Value {
    A, B
  }
}
