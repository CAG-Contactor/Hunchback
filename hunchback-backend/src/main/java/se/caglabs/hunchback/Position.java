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
import java.util.*;
import java.util.Map;

import static se.caglabs.hunchback.Map.tileSize;

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

//    @Inject
//    @Named("stateBean")
    private GameState stateBean = GameState.getInstance();

    @Inject
    @Named("detectionHandler")
    private DetectionHandler detectionHandler;

    private static final int INERTIA_TIME_IN_SEC = 5;
    private double stepFrequency;
    private Point position = initPosition();
    private SortedMap<Long, String> steps = new TreeMap<>();
    private Point latestCollisionCoordinates = new Point();

    private Point initPosition() {
        return new Point(370, 304);
    }
    
    @Handler
    public void move(@Body Message message, @Headers Map headers) {
        stateBean.start();

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
        stateBean.arm();
        stateBean.resetPointsIndicator(se.caglabs.hunchback.Map.pointIndicators);
        steps.clear();
    }

    @Handler
    public void getPosition(@Body Message message, @Headers Map headers) {
        Point inertiaRelPos = getInertiaRelativePosition();
        Point windDrift = wind.getDrift();

        Point currentPos = new Point(position.x, position.y);

        // Current position with speed and wind
        if (stateBean.isStarted()) {
            position.x = position.x + inertiaRelPos.x + windDrift.x;
            position.y = position.y + inertiaRelPos.y + windDrift.y;
        }

        // remove wind
        if (latestCollisionCoordinates.x == currentPos.x && latestCollisionCoordinates.y == currentPos.y) {
            position.x = position.x - windDrift.x;
            position.y = position.y - windDrift.y;
        }

        // Hämtar det hinder vi krockar med utifrån position.x och position.y ifall vi krockar
        Optional<Rectangle> obstacleOptional = detectionHandler.fetchObstacleInCollision(position);
        if (obstacleOptional.isPresent()) {
            handleCollision(inertiaRelPos, windDrift, currentPos, obstacleOptional.get());
        }

        Optional<PointIndicator> touchedPointIndicatorOptional = detectionHandler.touchPointIndicatorDetection(currentPos, stateBean.getPointIndicators());
        touchedPointIndicatorOptional.ifPresent(this::handleTouching);

        WsPosition wsPosition = new WsPosition(position);
        stateBean.tick();
        message.setBody(wsPosition.toJSON());
    }

    private void handleTouching(PointIndicator touchedPointIndicator) {
        stateBean.removePointIndicator(touchedPointIndicator);
    }

    private void handleCollision(Point inertiaRelPos, Point windDrift, Point currentPos, Rectangle obstacleInCollision) {
        steps.clear();
        Point inertiaRelPosAndWindDrift = new Point(inertiaRelPos.x + windDrift.x, inertiaRelPos.y + windDrift.y);
        Rectangle currentPosWithOutWindAndSpeed = new Rectangle(currentPos.x, currentPos.y, tileSize, tileSize);

        // Hämtar position närmast det hindret vi krockar för att undvika att vi "går igenom" hindret.
        Point posClosestToObstacle = detectionHandler.fetchPositionClosestToObstacle(currentPosWithOutWindAndSpeed, obstacleInCollision, inertiaRelPosAndWindDrift, position);
        this.latestCollisionCoordinates.x = posClosestToObstacle.x;
        this.latestCollisionCoordinates.y = posClosestToObstacle.y;
        position.x = posClosestToObstacle.x;
        position.y = posClosestToObstacle.y;
    }

    private Point getInertiaRelativePosition() {
        steps = steps.tailMap(System.currentTimeMillis() - INERTIA_TIME_IN_SEC * 1000);
        stepFrequency = (double) steps.size() / INERTIA_TIME_IN_SEC;
        Collection<String> values = steps.values();
        long xSpeed = values.stream().filter(d -> d.equals("right")).count()
                - values.stream().filter(d -> d.equals("left")).count();
        long ySpeed = values.stream().filter(d -> d.equals("down")).count()
                - values.stream().filter(d -> d.equals("up")).count();
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
            rootNode.put("stepFrequency", stepFrequency);

            ObjectNode positionNode = mapper.createObjectNode();
            positionNode.put("x", postion.x);
            positionNode.put("y", postion.y);
            rootNode.set("position", positionNode);

            ObjectNode windNode = mapper.createObjectNode();
            Point windDrift = wind.getDrift();
            windNode.put("x", windDrift.x);
            windNode.put("y", windDrift.y);
            windNode.put("windDirection", wind.getWindDirection());
            rootNode.set("wind", windNode);

            ObjectNode inertiaNode = mapper.createObjectNode();
            Point  inertia = getInertiaRelativePosition();
            inertiaNode.put("x", inertia.x);
            inertiaNode.put("y", inertia.y);
            rootNode.set("inertia", inertiaNode);
            rootNode.set("gameState", stateBean.toJSON());

            try {
                return mapper.writer().writeValueAsString(rootNode);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "";
            }
        }


    }

}