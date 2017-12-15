package se.caglabs.hunchback.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PositionToPlaceProcessor implements Processor{

  public void process(Exchange exchange) throws Exception {
// Get input from exchange
    String msg = exchange.getIn().getBody(String.class);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actualObj = mapper.readTree(msg);

    Map<String, Object> headers = exchange.getIn().getHeaders();

    if(actualObj.get("status").textValue().equals("OK")) {
      JsonNode issPosition = actualObj.get("results").elements().next().get("formatted_address");
      headers.put("position", issPosition.textValue());
      exchange.getOut().setBody(issPosition.textValue());
    } else {
      headers.put("position", "Iternationellt vatten");
      exchange.getOut().setBody("Iternationellt vatten");

    }
    exchange.getOut().setHeaders(headers);
  }
}
