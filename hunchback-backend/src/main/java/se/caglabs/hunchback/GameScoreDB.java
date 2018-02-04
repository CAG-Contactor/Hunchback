package se.caglabs.hunchback;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@Named("gameScoreBean")
public class GameScoreDB {

    private static final String MESSAGE_TYPE = "HighScores";

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
        List<ScoreCard> sortedScoreCards = scoreCards.stream()
                .sorted(Comparator.comparingInt(ScoreCard::getScore).reversed()).collect(Collectors.toList());
        message.setBody(toJSON(sortedScoreCards));
    }

    private String toJSON(List<ScoreCard> scoreCards) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("messageType", MESSAGE_TYPE);
        ArrayNode mapAsArray = rootNode.putArray("highScores");
        rootNode.put("highScores", mapAsArray);
        scoreCards.forEach(sc -> mapAsArray.add(sc.toJSONNode()));

        try {
            return mapper.writer().writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }

    }

    private MongoCollection<Document> getScoreCollection() {
        return mongoClient.getDatabase("highScore").getCollection("scoreCollection");
    }
}
