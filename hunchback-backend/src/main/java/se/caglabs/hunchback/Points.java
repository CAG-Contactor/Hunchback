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
    private int points = 0;
    private static final String MESSAGE_TYPE = "points";

    private GameState stateBean = GameState.getInstance();

    @Handler
    public void add(@Body Message message, @Headers Map headers){
        if (stateBean.isStarted()) {
            points += message.getBody(Integer.class);
        }
        headers.put("points", points);
        message.setBody(this.toJSON());
    }

    @Handler
    public void remove(@Body Message message, @Headers Map headers){
        if (stateBean.isStarted()) {
            points -= message.getBody(Integer.class);
            points = points < 0 ? 0 : points;
        }
        headers.put("points", points);
        message.setBody(this.toJSON());
    }

    @Handler
    public void reset(@Body Message message, @Headers Map headers){
        points = 0;
        headers.put("points", points);
        message.setBody(this.toJSON());
    }

    public String toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("messageType", MESSAGE_TYPE);
        rootNode.put("points", points);
        try {
            return mapper.writer().writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

}
