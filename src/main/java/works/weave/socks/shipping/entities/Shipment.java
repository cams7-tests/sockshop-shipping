package works.weave.socks.shipping.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Shipment {

  @Schema(example = "151d6260-1854-49fb-9afe-ba5c5969aef5", description = "Shipment id")
  private String id;

  @Schema(example = "57a98d98e4b00679b4a830b2", description = "Customer id")
  private String name;
}
