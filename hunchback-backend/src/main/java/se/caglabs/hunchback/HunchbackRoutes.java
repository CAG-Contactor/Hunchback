package se.caglabs.hunchback;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.websocket.WebsocketComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;
import se.caglabs.hunchback.processors.IssPositionProcessor;
import se.caglabs.hunchback.processors.PositionToPlaceProcessor;
import se.caglabs.hunchback.processors.WeatherProcessor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.ConnectionFactory;

@Component
public class HunchbackRoutes extends RouteBuilder {
    @Inject
    @Named("waterContainerBean")
    private Object waterContainerBean;
    @Inject
    @Named("positionBean")
    private Object position;
    @Inject
    @Named("windBean")
    private Object wind;

    private IssPositionProcessor issPositionProcessor = new IssPositionProcessor();
    private PositionToPlaceProcessor positionToPlaceProcessor = new PositionToPlaceProcessor();
    private WeatherProcessor weatherProcessor = new WeatherProcessor();

    @Override
    public void configure() throws Exception {
        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("vm://localhost");
        CamelContext context = new DefaultCamelContext();
        context.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        // setup Camel web-socket component on the port we have defined
        WebsocketComponent wc = getContext().getComponent("websocket", WebsocketComponent.class);
        wc.setPort(7890);
        // we can serve static resources from the classpath: or file: system
        wc.setStaticResources("classpath:.");

        // MongoDb route example. Setup a database and try it. Read more here: http://camel.apache.org/mongodb.html
//        from("direct:findById")
//            .to("mongodb:myDb?database=flights&collection=tickets&operation=findById")
//            .to("mock:resultFindById");

        from("timer:foo?period=60000")
            .streamCaching()
            .to("http4://api.open-notify.org/iss-now.json")
            .log("iss-data:${body}")
            .to("websocket:camel-iss?sendToAll=true");

        from("restlet:http://0.0.0.0:8080/direction/left?restletMethods=GET")
                .routeId("left-rest")
                .setHeader("Access-Control-Allow-Headers", constant("Content-Type"))
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .transform()
                .simple("left")
                .to("jms:queue:direction")
                .setBody(simple("10"))
                .to("jms:queue:addWater");
        from("restlet:http://0.0.0.0:8080/direction/right?restletMethods=GET")
                .routeId("right-rest")
                .setHeader("Access-Control-Allow-Headers", constant("Content-Type"))
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .transform()
                .simple("right")
                .to("jms:queue:direction")
                .setBody(simple("10"))
                .to("jms:queue:addWater");
        from("restlet:http://0.0.0.0:8080/direction/up?restletMethods=GET")
                .routeId("up-rest")
                .setHeader("Access-Control-Allow-Headers", constant("Content-Type"))
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .transform()
                .simple("up")
                .to("jms:queue:direction")
                .setBody(simple("10"))
                .to("jms:queue:addWater");
        from("restlet:http://0.0.0.0:8080/direction/down?restletMethods=GET")
                .routeId("down-rest")
                .setHeader("Access-Control-Allow-Headers", constant("Content-Type"))
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .transform()
                .constant("down")
                .to("jms:queue:direction")
                .setBody(simple("10"))
                .to("jms:queue:addWater");

        from("timer:position?period=100")
                .routeId("update-position")
                .bean(position,"getPosition")
//                .log("send pos:${body}")
                .to("websocket:hunchback?sendToAll=true");

        from("jms:queue:step")
                .log("From JMS:${body}")
                .setBody(simple("10000"))
                .to("jms:queue:addWater")
                .bean(position,"move");

        from("jms:queue:direction")
                .log("From JMS:${body}")
                .bean(position,"move");
//                .to("websocket:hunchback?sendToAll=true");

        from("jms:queue:pulseValue")
                .log("From JMS:${body}");

        from("jms:queue:addWater")
                .routeId("add-water-queue")
                .log("From JMS:${body}")
                .bean(waterContainerBean, "addWater")
                .log("water level:${headers.waterLevel}")
                .to("websocket:hunchback?sendToAll=true");

        from("jms:queue:removeWater")
                .routeId("remove-water-queue")
                .log("From JMS removeWater:${body}")
                .bean(waterContainerBean, "removeWater")
                .log("water level:${headers.waterLevel}")
                .to("websocket:hunchback?sendToAll=true");

        from("jms:queue:position")
                .log("From JMS:${body}");

        from("timer:waterleak?period=5s")
                .routeId("waterleak-timer")
                .setBody(simple("10"))
                .to("jms:queue:removeWater");

        from("timer:foo?period=15000")
            .routeId("get-ISS-poition-and-wind")
            .to("http4://api.open-notify.org/iss-now.json").streamCaching()
            .log("rest headers: ${headers}")
            .process(issPositionProcessor)
            .log(LoggingLevel.INFO,"${header.latitude}")
            .log(LoggingLevel.INFO,"${header.longitude}")
            .recipientList(simple("https4://maps.googleapis.com/maps/api/geocode/json?latlng=${header.latitude},${header.longitude}&sensor=false"), "false")
            .process(positionToPlaceProcessor)
            .log(LoggingLevel.INFO, "${body}")
            .log(LoggingLevel.INFO, "${headers}")
            .recipientList(simple("https4://api.openweathermap.org/data/2.5/weather?lat=${header.latitude}&lon=${header.longitude}&appid=93e711f5c2bb6f3e6dfaffc3f431858c&units=metric"), "false")
            .process(weatherProcessor)
            .bean(wind,"setWind")
            .log(LoggingLevel.INFO, "${headers}");

    }
}
