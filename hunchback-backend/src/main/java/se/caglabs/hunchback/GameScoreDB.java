package se.caglabs.hunchback;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Headers;
import org.apache.camel.Message;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Named("gameScoreBean")
public class GameScoreDB {

    // Sätta upp MongoDB lokalt för test:
    // Installera MongoDB på Mac: brew install MongoDB
    // Starta MongoDB: mongo --host=127.0.0.1:27017

    private MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

    @Handler
    public void addScore(@Body Message message, @Headers Map headers) {
        ScoreCard score = message.getBody(ScoreCard.class);
        getScoreCollection().insertOne(score.toBSON());
    }

    @Handler
    public void getScores(@Body Message message, @Headers Map headers) {
        MongoCursor<Document> iterator = getScoreCollection().find().iterator();
        List<ScoreCard> scoreCards = new ArrayList<>();
        while (iterator.hasNext()) {
            Document bsonScoreCard = iterator.next();
            scoreCards.add(new ScoreCard(bsonScoreCard.getString("userName"), bsonScoreCard.getInteger("score")));
        }
        message.setBody(toJSON(scoreCards));
    }

    private String toJSON(List<ScoreCard> scoreCards) {
        // TODO:
//        ObjectMapper objectMapper = new ObjectMapper();
        StringBuilder s = new StringBuilder();
         scoreCards.forEach(sc -> s.append(sc.toJSON()).append(","));
         if (s.length() > 0)
        s.deleteCharAt(s.length()-1);
        return "[" + s + "]";
    }

    private MongoCollection<Document> getScoreCollection() {
        return mongoClient.getDatabase("highScore").getCollection("scoreCollection");
    }
}
