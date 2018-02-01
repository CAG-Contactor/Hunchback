package se.caglabs.hunchback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named("stateBean")
public class GameState {
  private static GameState instance = new GameState();
  public static GameState getInstance() {
    return instance;
  }

  private enum State {
    RUNNING,
    FINISHED,
    ARMED
  }

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

  public ObjectNode toJSON() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put("state", state.name());
    rootNode.put("time",  getTimeLeftInSeconds());
    return rootNode;
  }

  private String getTimeLeftInSeconds() {
    return "" + (60L - getTimeInSeconds());
  }

  private long getTimeInSeconds() {
    return state.equals(State.FINISHED) ? 60L : ((System.currentTimeMillis() - time)/1000);
  }
}
