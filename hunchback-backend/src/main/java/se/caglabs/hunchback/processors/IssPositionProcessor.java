package se.caglabs.hunchback.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class IssPositionProcessor implements Processor{

  public void process(Exchange exchange) throws Exception {
// Get input from exchange
    String msg = exchange.getIn().getBody(String.class);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actualObj = mapper.readTree(msg);

    JsonNode message = actualObj.get("message");
    JsonNode issPosition = actualObj.get("iss_position");
    JsonNode latitude = issPosition.get("latitude");
    JsonNode longitude = issPosition.get("longitude");
//        System.out.println("message.textValue() = " + message.textValue());
//        System.out.println("latitude.textValue() = " + latitude.textValue());
//        System.out.println("longitude.textValue() = " + longitude.textValue());
//        // set output in exchange
//        System.out.println("msg = " + msg);
//        -32.6937,20.2942
//        exchange.getOut().setHeader("latitude", "-32.6937");
//        exchange.getOut().setHeader("longitude", "20.2942");
    exchange.getOut().setHeader("latitude", latitude.textValue());
    exchange.getOut().setHeader("longitude", longitude.textValue());
    exchange.getOut().setBody( msg);
  }
}