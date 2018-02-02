package se.caglabs.hunchback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static se.caglabs.hunchback.Map.tileSize;

@Singleton
@Named("stateBean")
class GameState {
    private static GameState instance = new GameState();

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

    private List<PointIndicator> pointIndicators = new ArrayList<>();
    private Long time = System.currentTimeMillis();
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
            if (System.currentTimeMillis() - time >= 60 * 1000) {
                state = State.FINISHED;
            }
        } else {
            time = System.currentTimeMillis();
        }
    }

    void removePointIndicator(PointIndicator pointIndicatorInCollision) {
        this.pointIndicators.remove(pointIndicatorInCollision);
    }

    ObjectNode toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("state", state.name());
        rootNode.put("time", getTimeLeftInSeconds());
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
        return "" + (60L - getTimeInSeconds());
    }

    private long getTimeInSeconds() {
        return ((System.currentTimeMillis() - time) / 1000);
    }
}
