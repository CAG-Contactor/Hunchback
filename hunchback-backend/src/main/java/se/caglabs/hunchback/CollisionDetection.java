package se.caglabs.hunchback;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.awt.*;
import java.util.Optional;

import static se.caglabs.hunchback.Map.tileSize;

@Singleton
@Named("collisionDetectionBean")
public class CollisionDetection {

    @Inject
    @Named("mapBean")
    private Map mapBean;

    Optional<Rectangle> hasCollided(Point position) {
        Rectangle currentPosition = new Rectangle(position.x,position.y, tileSize, tileSize);
        return mapBean.coordinatesOfObstacles.stream()
                .filter(currentPosition::intersects)
                .findFirst();
    }
}

