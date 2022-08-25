package works.weave.socks.shipping.template;

import br.com.six2six.fixturefactory.loader.TemplateLoader;

public class DomainTemplateLoader implements TemplateLoader {

  public static final String GET_APP_HEALTH = "GET_APP_HEALTH";
  public static final String GET_RABBITMQ_HEALTH = "GET_RABBITMQ_HEALTH";
  public static final String GET_RABBITMQ_HEALTH_WITH_ERROR = "GET_RABBITMQ_HEALTH_WITH_ERROR";
  public static final String SHIPMENT = "SHIPMENT";

  @Override
  public void load() {
    HealthCheckTemplate.loadTemplates();
    ShipmentTemplate.loadTemplates();
  }
}
