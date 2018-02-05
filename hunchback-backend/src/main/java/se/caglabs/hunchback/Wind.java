package se.caglabs.hunchback;

import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Headers;
import org.apache.camel.Message;

import javax.inject.Named;
import javax.inject.Singleton;
import java.awt.Point;
import java.util.Map;

@Singleton
@Named("windBean")
public class Wind {
  private static final String directions[] = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

  private String windDirection;
  private Point drift = new Point(0,0);

  @Handler
  public void setWind(@Body Message message, @Headers Map headers) {
    System.out.println("headers = " + headers);
    double windDirection = (double) headers.get("windDirection");
    double windSpeed = (double) headers.get("windSpeed");
    String direction = directions[(int) Math.round(windDirection / 45.0) % 8];
    setDrift(direction,windSpeed);
    this.windDirection = direction;

  }

  public Point getDrift(){
    return drift;
  }

  public String getWindDirection() {
    return windDirection;
  }

  private void setDrift(String direction, double windSpeed) {
    switch (direction) {
      case "N":
        drift.x = 0;
        drift.y = (int) Math.round(windSpeed);
        break;
      case "NE":
        drift.x = (int) Math.round(windSpeed);
        drift.y = (int) Math.round(windSpeed);
        break;
      case "E":
        drift.x = (int) Math.round(windSpeed);
        drift.y = 0;
        break;
      case "SE":
        drift.x = (int) Math.round(windSpeed);
        drift.y = -(int) Math.round(windSpeed);
        break;
      case "S":
        drift.x = 0;
        drift.y = -(int) Math.round(windSpeed);
        break;
      case "SW":
        drift.x = -(int) Math.round(windSpeed);
        drift.y = -(int) Math.round(windSpeed);
        break;
      case "w":
        drift.x = -(int) Math.round(windSpeed);
        drift.y = 0;
        break;
      case "NW":
        drift.x = -(int) Math.round(windSpeed);
        drift.y = (int) Math.round(windSpeed);
        break;
    }
    drift.x = drift.x/5;
    drift.y = drift.y/5;
  }

}

