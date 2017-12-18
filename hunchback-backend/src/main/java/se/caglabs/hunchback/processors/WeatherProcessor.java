package se.caglabs.hunchback.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class WeatherProcessor implements Processor{
  /*
  {
    "coord": {
      "lon": -137.37,
      "lat": 27.2
    },
    "weather": [
      {
        "id": 804,
        "main": "Clouds",
        "description": "overcast clouds",
        "icon": "04n"
      }
    ],
    "base": "stations",
    "main": {
      "temp": 22.74,
      "pressure": 1028.1,
      "humidity": 96,
      "temp_min": 22.74,
      "temp_max": 22.74,
      "sea_level": 1028.05,
      "grnd_level": 1028.1
    },
    "wind": {
      "speed": 7.46,
      "deg": 174.003
    },
    "clouds": {
      "all": 92
    },
    "dt": 1511101941,
    "sys": {
      "message": 0.4374,
      "sunrise": 1511105611,
      "sunset": 1511144189
    },
    "id": 0,
    "name": "",
    "cod": 200
  }
   */
  public void process(Exchange exchange) throws Exception {
// Get input from exchange
    String msg = exchange.getIn().getBody(String.class);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actualObj = mapper.readTree(msg);

    JsonNode weatherDescription = actualObj.get("weather").elements().next().get("description");
    JsonNode mainTemp = actualObj.get("main").get("temp");
    JsonNode windSpeed = actualObj.get("wind").get("speed");
    JsonNode windDirection = actualObj.get("wind").get("deg");
//        System.out.println("message.textValue() = " + weatherDescription);
//        System.out.println("latitude.textValue() = " + mainTemp);
//        System.out.println("longitude.textValue() = " + windSpeed);
    // set output in exchange
    exchange.getOut().setHeader("weatherDescription", weatherDescription);
    exchange.getOut().setHeader("mainTemp", mainTemp);
    exchange.getOut().setHeader("windSpeed", windSpeed);
    exchange.getOut().setHeader("windDirection", windDirection);
  }
}
