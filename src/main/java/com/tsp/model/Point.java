package com.tsp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Point {
    private Double x;
    private Double y;
    private String address;
    
    @Embedded
    private Coordinates coordinates;

    public Point() {}

    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Double x, Double y, String address, Coordinates coordinates) {
        this.x = x;
        this.y = y;
        this.address = address;
        this.coordinates = coordinates;
    }

    public double distanceTo(Point other) {
        if (this.x == null || this.y == null || other.x == null || other.y == null) {
            return 0.0;
        }
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
        // Synchronize x/y values with coordinates when they're set
        if (coordinates != null) {
            this.x = coordinates.getLng();
            this.y = coordinates.getLat();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Point(%.2f, %.2f)", x, y);
    }
}