package se.caglabs.hunchback;

import javax.inject.Named;
import javax.inject.Singleton;
import java.awt.*;
import java.util.Optional;

import static se.caglabs.hunchback.Map.tileSize;

@Singleton
@Named("detectionHandler")
class DetectionHandler {

    Optional<Rectangle> fetchObstacleInCollision(Point position) {
        Rectangle currentPosition = new Rectangle(position.x,position.y, tileSize, tileSize);
        return Map.coordinatesOfObstacles.stream()
                .filter(currentPosition::intersects)
                .findFirst();
    }

    Point fetchPositionClosestToObstacle(Rectangle currentPosWithOutWindAndSpeed, Rectangle obstacleInCollision, Point inertiaRelPosAndWindDrift, Point currentPosWithSpeedAndWind) {
        Rectangle collisionResult = currentPosWithOutWindAndSpeed.intersection(obstacleInCollision);

        if (isCollisionEastOfCurrentePos(inertiaRelPosAndWindDrift, collisionResult)) {
            // Räknar ut x positionen vänster om obstacle
            currentPosWithSpeedAndWind.x = currentPosWithOutWindAndSpeed.x - collisionResult.width;
            if (isCollisionOnLowerCorner(currentPosWithSpeedAndWind)) {
                currentPosWithSpeedAndWind.y = currentPosWithOutWindAndSpeed.y;
            }
        }
        if (isCollisionSouthOfCurrentePos(inertiaRelPosAndWindDrift, collisionResult)) {
            // Räknar ut y positionen ovanför obstacle
            currentPosWithSpeedAndWind.y = currentPosWithOutWindAndSpeed.y - collisionResult.height;
        }
        if (isCollisionWestOfCurrentePos(inertiaRelPosAndWindDrift, collisionResult)) {
            // Räknar ut x positionen höger om obstacle
            currentPosWithSpeedAndWind.x = currentPosWithOutWindAndSpeed.x + collisionResult.width;
            if (isCollisionOnLowerCorner(currentPosWithSpeedAndWind)) {
                currentPosWithSpeedAndWind.y = currentPosWithOutWindAndSpeed.y;
            }
        }
        if (isCollisionNorthOfCurrentePos(inertiaRelPosAndWindDrift, collisionResult)) {
            // Räknar ut y positionen nedanför obstacle
            currentPosWithSpeedAndWind.y = currentPosWithOutWindAndSpeed.y + collisionResult.height;
            if (isCollisionOnUpperCorner(currentPosWithSpeedAndWind)) {
                currentPosWithSpeedAndWind.x = currentPosWithOutWindAndSpeed.x;
            }
        }

        return currentPosWithSpeedAndWind;
    }

    private boolean isCollisionOnUpperCorner(Point position) {
        return fetchObstacleInCollision(new Point(position.x + tileSize, position.y)).isPresent();
    }

    private boolean isCollisionOnLowerCorner(Point position) {
        return fetchObstacleInCollision(new Point(position.x, position.y + tileSize)).isPresent();
    }

    private boolean isCollisionNorthOfCurrentePos(Point inertiaRelPosAndWindDrift, Rectangle collisionResult) {
        return collisionResult.height <= 0 && (inertiaRelPosAndWindDrift.y) < 0;
    }

    private boolean isCollisionWestOfCurrentePos(Point inertiaRelPosAndWindDrift, Rectangle collisionResult) {
        return collisionResult.width <= 0 && (inertiaRelPosAndWindDrift.x) < 0;
    }

    private boolean isCollisionSouthOfCurrentePos(Point inertiaRelPosAndWindDrift, Rectangle collisionResult) {
        return collisionResult.height <= 0 && (inertiaRelPosAndWindDrift.y) > 0;
    }

    private boolean isCollisionEastOfCurrentePos(Point inertiaRelPosAndWindDrift, Rectangle collisionResult) {
        return collisionResult.width <= 0 && (inertiaRelPosAndWindDrift.x) > 0;
    }

    Optional<PointIndicator> touchPointIndicatorDetection(Point currentPos, java.util.List<PointIndicator> pointIndicators) {
        Rectangle currentPosition = new Rectangle(currentPos.x, currentPos.y, tileSize, tileSize);
        return pointIndicators.stream()
                .filter(pointIndicator -> !pointIndicator.position.intersection(currentPosition).isEmpty())
                .findFirst();
    }
}

