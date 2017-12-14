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
                position.y = getNewY( position.y + steps);
                position.setLocation(position.x, position.y);
                break;
            case "down":
                position.y = getNewY( position.y - steps);
                position.setLocation(position.x, position.y);
                break;
            case "right":
                position.x = getNewX( position.x + steps);
                position.setLocation(position.x, position.y);
                break;
            case "left":
                position.x = getNewX( position.x - steps);
                position.setLocation(position.x, position.y);
                break;
        }
        WsPosition wsPosition = new WsPosition(position);
        message.setBody(wsPosition.toString());
        System.out.println("position = " + position);
    }
     private int getNewY(int y){
        if(y < minPosition.y){
            return minPosition.y;
        }
        if(y > maxPosition.y){
            return maxPosition.y;
        }
        return y;
     }

     private int getNewX(int x){
        if(x < minPosition.x){
            return minPosition.x;
        }
        if(x > maxPosition.x){
            return maxPosition.x;
        }
        return x;
     }

     class WsPosition {

        String messageType = "Position";

        Point postion;

         public WsPosition(Point postion) {
             this.postion = postion;
         }

         @Override
        public String toString() {
            return "{" +
                    "\"messageType\":" + messageType +
                    "\"position\": {" +
                    "\"x\":" + position.x +
                    "\"y\":" + position.y +
                    "}" +
                    "}";
        }


     }

}
