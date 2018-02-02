package se.caglabs.hunchback;

import java.awt.*;

import static se.caglabs.hunchback.Map.MINUS;
import static se.caglabs.hunchback.Map.PLUS;

class PointIndicator {

    Rectangle position;
    String pointIndicatorType;

    PointIndicator(Rectangle position, String pointIndicatorType) {
        this.position = position;
        this.pointIndicatorType = pointIndicatorType;
    }

    static String getPointIndicatorType(Long column) {
        if (column.equals(PLUS)) {
            return "PLUS";
        } else if (column.equals(MINUS)) {
            return "MINUS";
        } else {
            throw new IllegalArgumentException("Finns ej stöd för: " + column.intValue());
        }
    }
}
