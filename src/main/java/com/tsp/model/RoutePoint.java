package com.tsp.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tsp.config.RoutePointSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@JsonSerialize(using = RoutePointSerializer.class)
public class RoutePoint extends Point {
    @Column(name = "route_order")
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