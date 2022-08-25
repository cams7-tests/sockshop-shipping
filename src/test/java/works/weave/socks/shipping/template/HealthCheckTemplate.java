package works.weave.socks.shipping.template;

import static br.com.six2six.fixturefactory.Fixture.of;
import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.shipping.template.DomainTemplateLoader.GET_APP_HEALTH;
import static works.weave.socks.shipping.template.DomainTemplateLoader.GET_RABBITMQ_HEALTH;
import static works.weave.socks.shipping.template.DomainTemplateLoader.GET_RABBITMQ_HEALTH_WITH_ERROR;

import br.com.six2six.fixturefactory.Rule;
import lombok.NoArgsConstructor;
import works.weave.socks.shipping.entities.HealthCheck;

@NoArgsConstructor(access = PRIVATE)
public class HealthCheckTemplate {

  public static void loadTemplates() {
    of(HealthCheck.class)
        .addTemplate(
            GET_APP_HEALTH,
            new Rule() {
              {
                add("service", "shipping");
                add("status", "OK");
                add("date", parse("2022-08-20T18:29:40.903361", ISO_DATE_TIME));
              }
            })
        .addTemplate(GET_RABBITMQ_HEALTH)
        .inherits(
            GET_APP_HEALTH,
            new Rule() {
              {
                add("service", "shipping-rabbitmq");
              }
            })
        .addTemplate(GET_RABBITMQ_HEALTH_WITH_ERROR)
        .inherits(
            GET_RABBITMQ_HEALTH,
            new Rule() {
              {
                add("status", "err");
              }
            });
  }
}
