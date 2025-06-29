package com.tsp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class RoutePoint extends Point {
    
    @Column(name = "x_coordinate")
    private Double x;
    
    @Column(name = "y_coordinate") 
    private Double y;
    
    @Column(name = "route_order")
    private Integer order;
    
    @Column(name = "segment_distance")
    private Double segmentDistance;
    
    @Column(name = "accumulated_distance")
    private Double accumulatedDistance;

    public RoutePoint() {
        super();
    }

    public RoutePoint(Point point, Integer order) {
        super(point.getX(), point.getY(), point.getAddress(), point.getCoordinates());
        this.x = point.getX();
        this.y = point.getY();
        this.order = order;
        
        // Ensure coordinates are properly set
        if (point.getCoordinates() != null) {
            this.setCoordinates(point.getCoordinates());
        }
    }

    public RoutePoint(Double x, Double y, Integer order) {
        super(x, y);
        this.x = x;
        this.y = y;
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
    
    public Double getSegmentDistance() {
        return segmentDistance;
    }
    
    public void setSegmentDistance(Double segmentDistance) {
        this.segmentDistance = segmentDistance;
    }
    
    public Double getAccumulatedDistance() {
        return accumulatedDistance;
    }
    
    public void setAccumulatedDistance(Double accumulatedDistance) {
        this.accumulatedDistance = accumulatedDistance;
    }
    
    @Override
    public Double getX() {
        return x;
    }
    
    @Override
    public void setX(Double x) {
        this.x = x;
        super.setX(x);
    }
    
    @Override
    public Double getY() {
        return y;
    }
    
    @Override
    public void setY(Double y) {
        this.y = y;
        super.setY(y);
    }

    @Override
    public String toString() {
        return String.format("RoutePoint(%.2f, %.2f, order=%d, segmentDist=%.2f, accumulatedDist=%.2f)", 
                           getX(), getY(), order, segmentDistance, accumulatedDistance);
    }
}