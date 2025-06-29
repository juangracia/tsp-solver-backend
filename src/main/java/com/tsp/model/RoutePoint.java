package com.tsp.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class RoutePoint extends Point {
    private Integer order;

    public RoutePoint() {
        super();
    }

    public RoutePoint(Point point, Integer order) {
        super(point.getX(), point.getY(), point.getAddress(), point.getCoordinates());
        this.order = order;
    }

    public RoutePoint(Double x, Double y, Integer order) {
        super(x, y);
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return String.format("RoutePoint(%.2f, %.2f, order=%d)", getX(), getY(), order);
    }
}