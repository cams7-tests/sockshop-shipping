package works.weave.socks.shipping.controllers;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static works.weave.socks.shipping.template.DomainTemplateLoader.SHIPMENT;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import works.weave.socks.shipping.entities.Shipment;
import works.weave.socks.shipping.template.DomainTemplateLoader;

@WebMvcTest(controllers = ShippingController.class)
public class ShippingControllerTests {
  @MockBean private RabbitTemplate rabbitTemplate;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @BeforeAll
  static void loadTemplates() {
    FixtureFactoryLoader.loadTemplates(getPackageName(DomainTemplateLoader.class));
  }

  @Test
  public void whenAddShipmentToQueue_thenReturns200() throws Exception {
    Shipment shipment = from(Shipment.class).gimme(SHIPMENT);

    doNothing().when(rabbitTemplate).convertAndSend(anyString(), any(Shipment.class));

    mockMvc
        .perform(
            post("/shipping")
                .content(objectMapper.writeValueAsString(shipment))
                .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id", is(shipment.getId())))
        .andExpect(jsonPath("$.name", is(shipment.getName())));

    then(rabbitTemplate).should(times(1)).convertAndSend(eq("shipping-task"), eq(shipment));
  }

  @Test
  public void whenNoQueue_thenReturns201() throws Exception {
    Shipment shipment = from(Shipment.class).gimme(SHIPMENT);

    doThrow(AmqpException.class)
        .when(rabbitTemplate)
        .convertAndSend(anyString(), any(Shipment.class));

    mockMvc
        .perform(
            post("/shipping")
                .content(objectMapper.writeValueAsString(shipment))
                .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id", is(shipment.getId())))
        .andExpect(jsonPath("$.name", is(shipment.getName())));

    then(rabbitTemplate).should(times(1)).convertAndSend(eq("shipping-task"), eq(shipment));
  }
}
