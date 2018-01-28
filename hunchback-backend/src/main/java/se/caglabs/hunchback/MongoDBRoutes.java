package se.caglabs.hunchback;

import com.mongodb.MongoClient;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb3.MongoDbComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.util.jndi.JndiContext;

//@Component
public class MongoDBRoutes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        JndiContext jndiContext = new JndiContext();
        jndiContext.bind("myDb", new MongoClient("127.0.0.1", 27017));
        ModelCamelContext context = new DefaultCamelContext(jndiContext);
        context.addComponent("mongodb3", new MongoDbComponent());
//        MongoDbComponent mongoDbComponent = new MongoDbComponent();

                from("restlet:http://0.0.0.0:8080/game/score?restletMethods=PUT")
                .routeId("register-user")
                .to("mongodb3:myDb?database=highScoreDb&collection=scoreCollection&operation=insert")
                .to("mock:resultInsert");
        from("direct:findOneByQuery")
                .to("mongodb3:myDb?database=highScore&collection=scoreCollection&operation=findOneByQuery")
                .to("mock:resultFindOneByQuery");

        from("direct:findAll")
                .to("mongodb3:myDb?database=highScore&collection=scoreCollection&operation=findAll")
                .to("mock:resultFindOneByQuery");

        from("direct:registerScore")
                .to("mongodb3:myDb?database=highScore&collection=scoreCollection&operation=insert")
                .to("mock:resultInsert");

    }
}
