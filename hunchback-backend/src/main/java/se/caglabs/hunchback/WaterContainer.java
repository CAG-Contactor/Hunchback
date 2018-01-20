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
@Named("waterContainerBean")
public class WaterContainer {
    private int level = 1000;
    private static final int DEFALT_WATER_LEVEL = 1000;
    private static final String MESSAGE_TYPE = "waterlevel";

    @Handler
    public void addWater(@Body Message message, @Headers Map headers){
        level += message.getBody(Integer.class);
        headers.put("WaterLevel", level);
        message.setBody(this.toJSON());
    }

    @Handler
    public void removeWater(@Body Message message, @Headers Map headers){
        level -= message.getBody(Integer.class);
        level = level < 0 ? 0 : level;
        headers.put("waterLevel", level);
        message.setBody(this.toJSON());
    }

    @Handler
    public void resetWater(@Body Message message, @Headers Map headers){
        level = DEFALT_WATER_LEVEL;
        headers.put("waterLevel", level);
        message.setBody(this.toJSON());
    }

    public String toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("messageType", MESSAGE_TYPE);
        rootNode.put("level", level);
        try {
            return mapper.writer().writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

}
