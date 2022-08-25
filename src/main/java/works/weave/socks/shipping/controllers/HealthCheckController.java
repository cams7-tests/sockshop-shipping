package works.weave.socks.shipping.controllers;

import static org.springframework.http.HttpStatus.OK;

import com.rabbitmq.client.Channel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import works.weave.socks.shipping.entities.HealthCheck;

@Tag(name = "Health Check Service")
@RequiredArgsConstructor
@RestController
public class HealthCheckController {

  private static final String STATUS_OK = "OK";

  @Autowired private RabbitTemplate rabbitTemplate;

  @Operation(description = "Get health")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Ok")})
  @ResponseStatus(OK)
  @GetMapping(path = "/health")
  @ResponseBody
  Map<String, List<HealthCheck>> getHealth() {
    var map = new HashMap<String, List<HealthCheck>>();
    var healthChecks = new ArrayList<HealthCheck>();

    var app = new HealthCheck("shipping", STATUS_OK);
    var rabbitmq = new HealthCheck("shipping-rabbitmq", STATUS_OK);

    try {
      rabbitTemplate.execute(
          new ChannelCallback<String>() {
            @Override
            public String doInRabbit(Channel channel) throws Exception {
              Map<String, Object> serverProperties = channel.getConnection().getServerProperties();
              return serverProperties.get("version").toString();
            }
          });
    } catch (AmqpException e) {
      rabbitmq.setStatus("err");
    }

    healthChecks.add(app);
    healthChecks.add(rabbitmq);

    map.put("health", healthChecks);
    return map;
  }
}
