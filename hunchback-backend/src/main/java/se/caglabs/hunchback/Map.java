package se.caglabs.hunchback;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.NotFoundException;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
@Named("mapBean")
public class Map {

    List<Rectangle> coordinatesOfObstacles;
    private static final String FILE_NAME_OF_MAP = "map.txt";
    private static final Long OBSTACLE = 1L;
    private static final Integer tileSize = 32;

    public Map() {
        coordinatesOfObstacles = new ArrayList<>(getCoordinatesOfAllObstacles());
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<ArrayList<Long>> getMapasListFromFile() {
        final FileReader fileReader;
        try {
            fileReader = new FileReader(new ClassPathResource(FILE_NAME_OF_MAP).getFile().getPath());
        } catch (IOException e) {
            throw new NotFoundException("Can not find file: " + FILE_NAME_OF_MAP, e);
        }

        JSONParser jsonParser = new JSONParser();
        JSONArray mapAsJsonArray;
        try {
            mapAsJsonArray = (JSONArray) jsonParser.parse(fileReader);
        } catch (IOException | ParseException e) {
            throw new Error("Failed to parse file: " + FILE_NAME_OF_MAP, e);
        }

        return new ArrayList<ArrayList<Long>>(mapAsJsonArray);
    }

    private List<Rectangle> getCoordinatesOfAllObstacles() {
        ArrayList<ArrayList<Long>> mapAsJsonArray = getMapasListFromFile();
        return findAllObstacleCoordinates(mapAsJsonArray);
    }

    private List<Rectangle> findAllObstacleCoordinates(ArrayList<ArrayList<Long>> map) {
        List<Rectangle> coordinatesOfObstacles = new ArrayList<>();
        AtomicInteger rowIndexGen = new AtomicInteger();
        map.forEach(row -> {
            int rowIndex = rowIndexGen.getAndIncrement();
            AtomicInteger colIndexGen = new AtomicInteger();
            row.forEach(column -> {
                int colIndex = colIndexGen.getAndIncrement();
                if (column.equals(OBSTACLE)) {
                    coordinatesOfObstacles.add(new Rectangle(colIndex * tileSize, rowIndex * tileSize, tileSize, tileSize));
                }
            });
        });
        return coordinatesOfObstacles;
    }
}


