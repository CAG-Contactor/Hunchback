package se.caglabs.hunchback;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.websocket.WebsocketComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.stereotype.Component;

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

        from("timer:foo?period=60000")
            .streamCaching()
            .to("http4://api.open-notify.org/iss-now.json")
            .log("iss-data:${body}")
            .to("websocket:camel-iss?sendToAll=true");

        from("restlet:http://0.0.0.0:8080/step?restletMethods=GET")
                .routeId("step-rest")
                .transform().simple("1")
                .to("jms:queue:step");
        from("restlet:http://0.0.0.0:8080/direction/left?restletMethods=GET")
                .routeId("left-rest")
                .transform()
                .simple("left")
                .to("jms:queue:direction");
        from("restlet:http://0.0.0.0:8080/direction/right?restletMethods=GET")
                .routeId("right-rest")
                .transform()
                .simple("right")
                .to("jms:queue:direction");
        from("restlet:http://0.0.0.0:8080/direction/up?restletMethods=GET")
                .routeId("up-rest")
                .transform()
                .simple("up")
                .to("jms:queue:direction");
        from("restlet:http://0.0.0.0:8080/direction/down?restletMethods=GET")
                .routeId("down-rest")
                .transform()
                .constant("down")
                .to("jms:queue:direction");

        from("jms:queue:step")
                .log("From JMS:${body}")
                .setBody(simple("10000"))
                .to("jms:queue:addWater")
                .bean(position,"move");

        from("jms:queue:direction")
                .log("From JMS:${body}")
                .bean(position,"move")
                .to("websocket:hunchback?sendToAll=true");

        from("jms:queue:pulseValue")
                .log("From JMS:${body}");
        from("jms:queue:addWater")
                .routeId("add-water-queue")
                .log("From JMS:${body}")
                .bean(waterContainerBean, "addWater")
                .log("water level:${headers.waterLevel}");
        from("jms:queue:removeWater")
                .routeId("remove-water-queue")
                .log("From JMS removeWater:${body}")
                .bean(waterContainerBean, "removeWater")
                .log("water level:${headers.waterLevel}");
        from("jms:queue:position")
                .log("From JMS:${body}");

        from("timer:waterleak?period=5s")
                .routeId("waterleak-timer")
                .setBody(simple("10"))
                .to("jms:queue:removeWater");
    }
}
