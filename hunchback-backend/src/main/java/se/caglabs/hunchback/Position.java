package se.caglabs.hunchback;

import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Headers;
import org.apache.camel.Message;

import javax.inject.Named;
import javax.inject.Singleton;
import java.awt.*;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
    private Point position = new Point(0, 0);
    private Point minPosition = new Point(0, 0);
    private Point maxPosition = new Point(499, 499);
    private SortedMap<Long, String> steps = new TreeMap<>();

    @Handler
    public void move(@Body Message message, @Headers Map headers) {
        String direction = message.getBody(String.class);
        switch (direction) {
            case "up":
                steps.put(System.currentTimeMillis(), "up");
//                position.y = getNewY(position.y + steps);
//                position.setLocation(position.x, position.y);
                break;
            case "down":
                steps.put(System.currentTimeMillis(), "down");
//                position.setLocation(position.x, position.y);
                break;
            case "right":
                steps.put(System.currentTimeMillis(), "right");
//                position.x = getNewX(position.x + steps);
//                position.setLocation(position.x, position.y);
                break;
            case "left":
                steps.put(System.currentTimeMillis(), "left");
//                position.x = getNewX(position.x - steps);
//                position.setLocation(position.x, position.y);
                break;
        }
//        WsPosition wsPosition = new WsPosition(position);
//        message.setBody(wsPosition.toString());
//        System.out.println("position = " + position);
    }

    @Handler
    public void getPosition(@Body Message message, @Headers Map headers) {
        Point inertiaPos = getInertiaPosition();
        position.x = getNewX(position.x + inertiaPos.x);
        position.y = getNewX(position.y + inertiaPos.y);
        WsPosition wsPosition = new WsPosition(position);
        message.setBody(wsPosition.toString());
    }

    private int getNewY(int y) {
        if (y < minPosition.y) {
            return minPosition.y;
        }
        if (y > maxPosition.y) {
            return maxPosition.y;
        }
        return y;
    }

    private int getNewX(int x) {
        if (x < minPosition.x) {
            return minPosition.x;
        }
        if (x > maxPosition.x) {
            return maxPosition.x;
        }
        return x;
    }

    private Point getInertiaPosition(){
        SortedMap<Long, String> validSteps = steps.tailMap(System.currentTimeMillis() - 5 * 1000);
        System.out.println("longStringSortedMap = " + validSteps);
        steps = validSteps;
        Collection<String> values = steps.values();
        long xSpeed = values.stream().filter(d -> d.equals("right")).count()
                - values.stream().filter(d -> d.equals("left")).count();
        long ySpeed = values.stream().filter(d -> d.equals("up")).count()
                - values.stream().filter(d -> d.equals("down")).count();
        System.out.println("speed (" + xSpeed + ", " + ySpeed + ")");
        return new Point((int)xSpeed, (int)ySpeed);
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
                    "\"messageType\":\"" + messageType + "\"," +
                    "\"position\": {" +
                    "\"x\":" + position.x +
                    ", \"y\":" + position.y +
                    "}" +
                    "}";
        }


    }

}