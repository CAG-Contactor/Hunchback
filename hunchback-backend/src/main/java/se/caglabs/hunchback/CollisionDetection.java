package se.caglabs.hunchback;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
@Named("collisionDetectionBean")
public class CollisionDetection {

    @Inject
    @Named("mapBean")
    private Map mapBean;

    boolean hasCollided(Point position) {
        Rectangle currentPosition = new Rectangle(position.x,position.y, 32, 32);
        return mapBean.coordinatesOfObstacles.stream()
                .anyMatch(obstacle -> obstacle.getBounds2D().intersects(currentPosition.getBounds2D()));
    }
}

