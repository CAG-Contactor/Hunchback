package se.caglabs.hunchback;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;

//import com.mongodb.client.FindIterable;
//import org.bson.Document;

@Component
public class MongoDBRoutes extends RouteBuilder {

    @Inject
    @Named("gameScoreBean")
    private Object gameScoreBean;

    @Override
    public void configure() throws Exception {

        from("restlet:http://0.0.0.0:8080/scores")
                .routeId("scores-rest")
                .setHeader("Access-Control-Allow-Headers", constant("Content-Type"))
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .bean(gameScoreBean, "getScores");
        from("restlet:http://0.0.0.0:8080/score/add")
                .routeId("scores-add-rest")
                .setHeader("Access-Control-Allow-Headers", constant("Content-Type"))
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .process((exchange -> exchange.getOut().setBody(new ScoreCard((String) exchange.getIn().getHeader("userName"), Integer.parseInt((String) exchange.getIn().getHeader("score"))))))
                .bean(gameScoreBean, "addScore");

//        MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);
//        JndiContext jndiContext = new JndiContext();
//        jndiContext.bind("myDb", mongoClient);
//        FindIterable<Document> documents = mongoClient.getDatabase("highScore").getCollection("scoreCollection").find();
//        while (documents.iterator().hasNext()) {
//            Document doc = documents.iterator().next();
//        }
//        applicationContext.getBean("myDb");

//        CamelContext context = new SpringCamelContext(applicationContext);
//        context.addComponent("mongodb3", new MongoDbComponent());
//        MongoDbComponent mongoDbComponent = new MongoDbComponent();

//        from("direct:findOneByQuery")
//                .to("mongodb3:myDb?database=highScore&collection=scoreCollection&operation=findOneByQuery")
//                .to("mock:resultFindOneByQuery");
//
//        from("direct:findAll")
//                .to("mongodb3:myDb?database=highScore&collection=scoreCollection&operation=findAll")
//                .to("mock:resultFindOneByQuery");
//
//        from("direct:registerScore")
//                .to("mongodb3:myDb?database=highScore&collection=scoreCollection&operation=insert")
//                .to("mock:resultInsert");

    }
}
