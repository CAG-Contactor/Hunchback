package se.caglabs.hunchback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Headers;
import org.apache.camel.Message;

import javax.inject.Inject;
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
    @Inject
    @Named("windBean")
    private Wind wind;

    @Inject
    @Named("collisionDetectionBean")
    private CollisionDetection collisionDetection;

    private static final int INERTIA_TIME_IN_SEC = 5;
    private double stepFrequency;
    private Point position = initPosition();

    private Point initPosition() {
        return new Point(370, 304);
    }

    private SortedMap<Long, String> steps = new TreeMap<>();
    private Point latestNoneCollisionCoordinates = new Point();

    @Handler
    public void move(@Body Message message, @Headers Map headers) {
        String direction = message.getBody(String.class);
        if(direction.equals("up" )
            || direction.equals("down")
            || direction.equals("left")
            || direction.equals("right")){
            steps.put(System.currentTimeMillis(), direction);
        }
    }

    @Handler
    public void resetPosition(@Body Message message, @Headers Map headers){
        position = initPosition();
        steps.clear();
    }

    @Handler
    public void getPosition(@Body Message message, @Headers Map headers) {
        Point inertiaRelPos = getInertiaRelativePosition();
        Point windDrift = wind.getDrift();

        position.x = position.x + inertiaRelPos.x + windDrift.x;
        position.y = position.y + inertiaRelPos.y + windDrift.y;

        if (collisionDetection.hasCollided(position)) {
            position.x = latestNoneCollisionCoordinates.x;
            position.y = latestNoneCollisionCoordinates.y;
        } else {
            latestNoneCollisionCoordinates.setLocation(position);
        }

        WsPosition wsPosition = new WsPosition(position);
        message.setBody(wsPosition.toJSON());
    }

    private Point getInertiaRelativePosition() {
        steps = steps.tailMap(System.currentTimeMillis() - INERTIA_TIME_IN_SEC * 1000);
        stepFrequency = (double) steps.size() / INERTIA_TIME_IN_SEC;
        Collection<String> values = steps.values();
        long xSpeed = values.stream().filter(d -> d.equals("right")).count()
                - values.stream().filter(d -> d.equals("left")).count();
        long ySpeed = values.stream().filter(d -> d.equals("up")).count()
                - values.stream().filter(d -> d.equals("down")).count();
//        System.out.println("speed (" + xSpeed + ", " + ySpeed + ")");
        return new Point((int) xSpeed, (int) ySpeed);
    }

    class WsPosition {

        private static final String MESSAGE_TYPE = "Position";

        Point postion;

        public WsPosition(Point postion) {
            this.postion = postion;
        }

        public String toJSON() {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("messageType", MESSAGE_TYPE);
//            rootNode.put("stepFrequency", stepFrequency);

            ObjectNode positionNode = mapper.createObjectNode();
            positionNode.put("x", postion.x);
            positionNode.put("y", postion.y);

            rootNode.set("position", positionNode);

            try {
                return mapper.writer().writeValueAsString(rootNode);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "";
            }
        }


    }

}