package works.weave.socks.shipping.template;

import static br.com.six2six.fixturefactory.Fixture.of;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.shipping.template.DomainTemplateLoader.SHIPMENT;

import br.com.six2six.fixturefactory.Rule;
import lombok.NoArgsConstructor;
import works.weave.socks.shipping.entities.Shipment;

@NoArgsConstructor(access = PRIVATE)
public class ShipmentTemplate {

  public static final String SHIPMENT_ID = "b7df8514-8486-4ed2-b4ee-065fa9a15709";
  public static final String CUSTOMER_ID = "57a98d98e4b00679b4a830b2";

  public static void loadTemplates() {
    of(Shipment.class)
        .addTemplate(
            SHIPMENT,
            new Rule() {
              {
                add("id", SHIPMENT_ID);
                add("name", CUSTOMER_ID);
              }
            });
  }
}
