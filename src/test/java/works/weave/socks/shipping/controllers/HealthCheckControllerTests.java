package works.weave.socks.shipping.controllers;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static works.weave.socks.shipping.template.DomainTemplateLoader.GET_APP_HEALTH;
import static works.weave.socks.shipping.template.DomainTemplateLoader.GET_RABBITMQ_HEALTH;
import static works.weave.socks.shipping.template.DomainTemplateLoader.GET_RABBITMQ_HEALTH_WITH_ERROR;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import works.weave.socks.shipping.entities.HealthCheck;
import works.weave.socks.shipping.template.DomainTemplateLoader;

@WebMvcTest(controllers = HealthCheckController.class)
public class HealthCheckControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockBean private RabbitTemplate rabbitTemplate;

  @BeforeAll
  static void loadTemplates() {
    FixtureFactoryLoader.loadTemplates(getPackageName(DomainTemplateLoader.class));
  }

  @Test
  void whenGetHealth_thenReturns200() throws Exception {
    HealthCheck appHealth = from(HealthCheck.class).gimme(GET_APP_HEALTH);
    HealthCheck rabbitmqHealth = from(HealthCheck.class).gimme(GET_RABBITMQ_HEALTH);

    given(rabbitTemplate.execute(any())).willReturn("version");

    mockMvc
        .perform(get("/health"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.health[0].service", is(appHealth.getService())))
        .andExpect(jsonPath("$.health[0].status", is(appHealth.getStatus())))
        .andExpect(jsonPath("$.health[0].date", notNullValue()))
        .andExpect(jsonPath("$.health[1].service", is(rabbitmqHealth.getService())))
        .andExpect(jsonPath("$.health[1].status", is(rabbitmqHealth.getStatus())))
        .andExpect(jsonPath("$.health[1].date", notNullValue()));

    then(rabbitTemplate).should(times(1)).execute(any());
  }

  @Test
  void whenGetHealthWithError_thenReturns200() throws Exception {
    HealthCheck appHealth = from(HealthCheck.class).gimme(GET_APP_HEALTH);
    HealthCheck rabbitmqHealth = from(HealthCheck.class).gimme(GET_RABBITMQ_HEALTH_WITH_ERROR);

    given(rabbitTemplate.execute(any())).willThrow(AmqpException.class);

    mockMvc
        .perform(get("/health"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.health[0].service", is(appHealth.getService())))
        .andExpect(jsonPath("$.health[0].status", is(appHealth.getStatus())))
        .andExpect(jsonPath("$.health[0].date", notNullValue()))
        .andExpect(jsonPath("$.health[1].service", is(rabbitmqHealth.getService())))
        .andExpect(jsonPath("$.health[1].status", is(rabbitmqHealth.getStatus())))
        .andExpect(jsonPath("$.health[1].date", notNullValue()));

    then(rabbitTemplate).should(times(1)).execute(any());
  }
}
