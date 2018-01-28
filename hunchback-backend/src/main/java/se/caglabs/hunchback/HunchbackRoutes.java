package se.caglabs.hunchback;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.websocket.WebsocketComponent;
import org.apache.camel.impl.DefaultCamelContext;
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
    @Named("pointsBean")
    private Object pointsBean;
    @Inject
    @Named("positionBean")
    private Object position;
    @Inject
    @Named("windBean")
    private Object wind;
    @Inject
    @Named("mapBean")
    private Object mapBean;


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
                .to("jms:queue:addPoints");
        from("restlet:http://0.0.0.0:8080/direction/right?restletMethods=GET")
                .routeId("right-rest")
                .setHeader("Access-Control-Allow-Headers", constant("Content-Type"))
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .transform()
                .simple("right")
                .to("jms:queue:direction")
                .setBody(simple("10"))
                .to("jms:queue:addPoints");
        from("restlet:http://0.0.0.0:8080/direction/up?restletMethods=GET")
                .routeId("up-rest")
                .setHeader("Access-Control-Allow-Headers", constant("Content-Type"))
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .transform()
                .simple("up")
                .to("jms:queue:direction")
                .setBody(simple("10"))
                .to("jms:queue:addPoints");
        from("restlet:http://0.0.0.0:8080/direction/down?restletMethods=GET")
                .routeId("down-rest")
                .setHeader("Access-Control-Allow-Headers", constant("Content-Type"))
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .transform()
                .constant("down")
                .to("jms:queue:direction")
                .setBody(simple("10"))
                .to("jms:queue:addPoints");
        from("restlet:http://0.0.0.0:8080/map")
                .routeId("map-rest")
                .setHeader("Access-Control-Allow-Headers", constant("Content-Type"))
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .bean(mapBean, "getMap");

        from("restlet:http://0.0.0.0:8080/game/restart")
                .routeId("reset-game")
                .setHeader("Access-Control-Allow-Headers", constant("Content-Type"))
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .to("jms:queue:resetPosition")
                .to("jms:queue:resetPoints");

        from("timer:position?period=100")
                .routeId("update-position")
                .bean(position,"getPosition")
                .to("websocket:hunchback?sendToAll=true");

        from("jms:queue:step")
                .log("From JMS:${body}")
                .setBody(simple("10000"))
                .to("jms:queue:addPoints")
                .bean(position,"move");

        from("jms:queue:direction")
                .log("From JMS:${body}")
                .bean(position,"move");
//                .to("websocket:hunchback?sendToAll=true");

        from("jms:queue:resetPosition")
                .log("From JMS reset position")
                .bean(position,"resetPosition");

        from("jms:queue:pulseValue")
                .log("From JMS:${body}");

        from("jms:queue:addPoints")
                .routeId("add-points-queue")
                .log("From JMS:${body}")
                .bean(pointsBean, "add")
                .log("Points:${headers.points}")
                .to("websocket:hunchback?sendToAll=true");

        from("jms:queue:removePoints")
                .routeId("remove-points-queue")
                .log("From JMS removeWater:${body}")
                .bean(pointsBean, "remove")
                .log("points:${headers.points}")
                .to("websocket:hunchback?sendToAll=true");

        from("jms:queue:resetPoints")
                .routeId("reset-points-queue")
                .log("points:${headers.points}")
                .bean(pointsBean, "reset")
                .log("points reset:${headers.points}")
                .to("websocket:hunchback?sendToAll=true");

        from("jms:queue:position")
                .log("From JMS:${body}");

        from("timer:pointleak?period=5s")
                .routeId("pointleak-timer")
                .setBody(simple("10"))
                .to("jms:queue:removePoints");

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
