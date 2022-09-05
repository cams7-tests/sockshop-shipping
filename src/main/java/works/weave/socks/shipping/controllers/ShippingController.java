package works.weave.socks.shipping.controllers;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import works.weave.socks.shipping.entities.Shipment;

@Tag(name = "Shipping Service")
@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping(path = "/shipping")
public class ShippingController {

  @Value("${shipping.rabbitmq.queue}")
  private String queueName;

  private final RabbitTemplate rabbitTemplate;

  @Operation(description = "Add shipment to RabbitMQ queue")
  @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
  @ResponseStatus(CREATED)
  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  Shipment addShipment(@RequestBody Shipment shipment) {
    log.info("Adding shipment to queue...");
    try {
      rabbitTemplate.convertAndSend(queueName, shipment);
    } catch (Exception e) {
      log.error(
          "Unable to add to queue (the queue is probably down). Accepting anyway. Don't do this for real!",
          e);
    }
    return shipment;
  }
}
