
package se.caglabs.hunchback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Headers;
import org.apache.camel.Message;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
@Named("pointsBean")
public class Points {

    private static final String MESSAGE_TYPE = "points";

    private GameState stateBean = GameState.getInstance();

    @Handler
    public void add(@Body Message message, @Headers Map headers){
        if (stateBean.isStarted()) {
            GameState.points += message.getBody(Integer.class);
        }
        headers.put("points", GameState.points);
        message.setBody(this.toJSON());
    }

    @Handler
    public void remove(@Body Message message, @Headers Map headers){
        if (stateBean.isStarted()) {
            GameState.points -= message.getBody(Integer.class);
            GameState.points = GameState.points < 0 ? 0 : GameState.points;
        }
        headers.put("points", GameState.points);
        message.setBody(this.toJSON());
    }

    @Handler
    public void reset(@Body Message message, @Headers Map headers){
        GameState.points = 0;
        headers.put("points", GameState.points);
        message.setBody(this.toJSON());
    }

    public String toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("messageType", MESSAGE_TYPE);
        rootNode.put("points", GameState.points);
        try {
            return mapper.writer().writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

}

