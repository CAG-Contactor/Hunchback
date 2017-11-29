package se.caglabs.hunchback;
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
    private int level;

    @Handler
    public void addWater(@Body Message message, @Headers Map headers){
        level += message.getBody(Integer.class);
        headers.put("waterLevel", level);
    }

    @Handler
    public void removeWater(@Body Message message, @Headers Map headers){
        level -= message.getBody(Integer.class);
        level = level < 0 ? 0 : level;
        headers.put("waterLevel", level);
    }
}
