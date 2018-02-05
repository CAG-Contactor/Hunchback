package se.caglabs.hunchback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Headers;
import org.apache.camel.Message;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
@Named("stateBean")
public class GameState {
    public static final long PLAY_TIME = 60L;
    private static GameState instance = new GameState();
    static int points = 0;

    public static GameState getInstance() {
        return instance;
    }

    public void resetPointsIndicator(List<PointIndicator> pointIndicators) {
        this.pointIndicators.addAll(pointIndicators);
    }

    private enum State {
        RUNNING,
        FINISHED,
        ARMED
    }

    private Long time = System.currentTimeMillis();
    private List<PointIndicator> pointIndicators = new ArrayList<>();
    private State state = State.FINISHED;


    public boolean isStarted() {
        return state.equals(State.RUNNING);
    }

    public void start() {
        if (state == State.ARMED) {
            state = State.RUNNING;
            time = System.currentTimeMillis();
        }
    }

    public void arm() {
        state = State.ARMED;
    }

    public void tick() {
        if (state == State.RUNNING) {
            if (System.currentTimeMillis() - time >= PLAY_TIME * 1000) {
                state = State.FINISHED;
            }
        } else {
            time = System.currentTimeMillis();
        }
    }

    void removePointIndicator(PointIndicator pointIndicatorInCollision) {
        if (pointIndicatorInCollision.pointIndicatorType.equals("PLUS")) {
            points += 100;
        } else {
            points -= 150;
            points = points < 0 ? 0 : points;
        }
        this.pointIndicators.remove(pointIndicatorInCollision);
    }

    public List<PointIndicator> getPointIndicators() {
        return pointIndicators;
    }

    ObjectNode toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("state", state.name());
        rootNode.put("time", getTimeLeftInSeconds());
        rootNode.put("points", points);
        ArrayNode pointIndicatorsAsArrayNode = rootNode.putArray("pointIndicators");

        pointIndicators.forEach(pointIndicator -> {
            ObjectNode pointIndicatorAsObject = pointIndicatorsAsArrayNode.addObject();
            pointIndicatorAsObject.put("x", pointIndicator.position.x);
            pointIndicatorAsObject.put("y", pointIndicator.position.y);
            pointIndicatorAsObject.put("pointIndicatorType", pointIndicator.pointIndicatorType);
        });

        return rootNode;
    }

    private String getTimeLeftInSeconds() {
        return "" + (PLAY_TIME - getTimeInSeconds());
    }

    private long getTimeInSeconds() {
        return state.equals(State.FINISHED) ? PLAY_TIME : ((System.currentTimeMillis() - time) / 1000);
    }
}
