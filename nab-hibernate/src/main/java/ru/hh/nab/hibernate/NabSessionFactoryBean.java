package ru.hh.nab.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.Service;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import ru.hh.nab.common.settings.NabSettings;
import ru.hh.nab.hibernate.interceptor.ControllerPassingInterceptor;
import ru.hh.nab.hibernate.interceptor.RequestIdPassingInterceptor;

import javax.sql.DataSource;

public final class NabSessionFactoryBean extends LocalSessionFactoryBean {

  private final Collection<ServiceSupplier<?>> serviceSuppliers;
  private final Collection<SessionFactoryCreationHandler> sessionFactoryCreationHandlers;

  public NabSessionFactoryBean(DataSource dataSource, NabSettings hibernateSettings, BootstrapServiceRegistryBuilder bootstrapServiceRegistryBuilder,
                               Collection<ServiceSupplier<?>> serviceSuppliers,
                               Collection<SessionFactoryCreationHandler> sessionFactoryCreationHandlers) {
    this.serviceSuppliers = new ArrayList<>(serviceSuppliers);
    this.sessionFactoryCreationHandlers = new ArrayList<>(sessionFactoryCreationHandlers);
    MetadataSources metadataSources = new MetadataSources(bootstrapServiceRegistryBuilder.build());
    setMetadataSources(metadataSources);

    setDataSource(dataSource);

    // if set to true, it slows down acquiring database connection on application start
    hibernateSettings = hibernateSettings.withProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");

    // used to retrieve natively generated keys after insert
    // if set to false, Hibernate will retrieve key directly from sequence
    // and can fail if GenerationType = IDENTITY and sequence name is non-standard
    hibernateSettings = hibernateSettings.withProperty("hibernate.jdbc.use_get_generated_keys", "true");

    setHibernateProperties(hibernateSettings.getProperties());

    configureAddToQuery(hibernateSettings);
  }

  private void configureAddToQuery(NabSettings hibernateSettings) {
    hibernateSettings.getString("hibernate.add_to_query").ifPresent(addToQueryValue -> {
      switch (addToQueryValue) {
        case "request_id":
          setEntityInterceptor(new RequestIdPassingInterceptor());
          break;
        // Request_id in sql query prevents reuse of prepared statements, because every sql query is different.
        // Controller does not prevent reuse of prepared statements, because same sql queries from the same controller can be reused.
        case "controller":
          setEntityInterceptor(new ControllerPassingInterceptor());
          break;
        default:
          throw new RuntimeException("unknown value of hibernate 'addToQuery' property");
      }
    });
  }

  @Override
  protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder sfb) {
    StandardServiceRegistryBuilder serviceRegistryBuilder = sfb.getStandardServiceRegistryBuilder();
    serviceSuppliers.forEach(serviceSupplier -> {
      Service service = serviceSupplier.get();
      serviceRegistryBuilder.addService(serviceSupplier.getClazz(), service);
    });
    return sfb.buildSessionFactory();
  }

  @Override
  public SessionFactory getObject() {
    SessionFactory sessionFactory = super.getObject();
    sessionFactoryCreationHandlers.forEach(handler ->  handler.accept(sessionFactory));
    return sessionFactory;
  }

  @FunctionalInterface
  public interface SessionFactoryCreationHandler extends Consumer<SessionFactory> {}

  public interface ServiceSupplier<T extends Service> extends Supplier<T> {
    Class<T> getClazz();
  }
}
