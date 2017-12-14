package se.caglabs.hunchback;

import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Headers;
import org.apache.camel.Message;

import javax.inject.Named;
import javax.inject.Singleton;
import java.awt.*;
import java.util.Map;

/**
 * Project:Hunchback
 * User: fredrik
 * Date: 2017-10-14
 * Time: 11:32
 */

@Singleton
@Named("positionBean")
public class Position {
    private String direction = "up";
    private Point position = new Point(0,0);
    private Point minPosition = new Point(0,0);
    private Point maxPosition = new Point(100,1000);

    @Handler
    public void move(@Body Message message, @Headers Map headers){
        String direction = message.getBody(String.class);
        int steps = 1;
        switch (direction){
            case "up":
                position.setLocation(position.x, position.y + steps);
                break;
            case "down":
                position.setLocation(position.x, position.y - steps);
                break;
            case "right":
                position.setLocation(position.x + steps, position.y);
                break;
            case "left":
                position.setLocation(position.x - steps, position.y);
                break;
        }
        message.getHeaders().put("x",position.x);
        message.getHeaders().put("y",position.y);
        System.out.println("position = " + position);
    }


}
