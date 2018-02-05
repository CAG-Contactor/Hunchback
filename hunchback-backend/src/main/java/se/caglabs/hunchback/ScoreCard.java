package se.caglabs.hunchback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bson.Document;

import java.util.HashMap;

public class ScoreCard {

    private static final String MESSAGE_TYPE = "Score";
    private String userName;

    private int score;

    public ScoreCard() {
    }

    public ScoreCard(java.util.Map<String, Object> bsonMap) {
        this((String) bsonMap.get("userName"), (Integer) bsonMap.get("score"));
    }

    public ScoreCard(String userName, int score) {
        this.userName = userName;
        this.score = score;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("messageType", MESSAGE_TYPE);
        rootNode.put("userName", userName);
        rootNode.put("score", score);
        try {
            return mapper.writer().writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public ObjectNode toJSONNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("messageType", MESSAGE_TYPE);
        rootNode.put("userName", userName);
        rootNode.put("score", score);
        return rootNode;
    }

    public Document toBSON() {
        java.util.Map<String, Object> bsonMap = new HashMap<>();
        bsonMap.put("score", score);
        bsonMap.put("userName", userName);
        return new Document(bsonMap);
    }
}
