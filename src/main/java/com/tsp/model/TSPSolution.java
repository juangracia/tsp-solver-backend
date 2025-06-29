package com.tsp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tsp_solutions")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TSPSolution {
    
    @Id
    private String id;
    
    @Column(name = "file_name")
    private String fileName;
    
    @ElementCollection
    @CollectionTable(name = "tsp_solution_points")
    private List<Point> originalPoints;
    
    @ElementCollection
    @CollectionTable(name = "tsp_solution_route")
    private List<RoutePoint> route;
    
    @Column(name = "total_distance")
    private Double totalDistance;
    
    @Column(name = "real_world_distance")
    private String realWorldDistance;
    
    @Column(name = "estimated_drive_time")
    private String estimatedDriveTime;
    
    @Column(name = "algorithm")
    private String algorithm;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SolutionStatus status;
    
    @Column(name = "execution_time_ms")
    private Long executionTimeMs;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "point_count")
    private Integer pointCount;
    
    @Column(name = "real_world_demo")
    private Boolean realWorldDemo;
    
    @Column(name = "map_url")
    private String mapUrl;
    
    @ElementCollection
    @CollectionTable(name = "tsp_solution_addresses")
    private List<AddressInfo> addresses;

    public TSPSolution() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.status = SolutionStatus.UPLOADED;
        this.realWorldDemo = false;
    }

    public TSPSolution(List<Point> originalPoints, String fileName) {
        this();
        this.originalPoints = originalPoints;
        this.fileName = fileName;
        this.pointCount = originalPoints != null ? originalPoints.size() : 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Point> getOriginalPoints() {
        return originalPoints;
    }

    public void setOriginalPoints(List<Point> originalPoints) {
        this.originalPoints = originalPoints;
        this.pointCount = originalPoints != null ? originalPoints.size() : 0;
    }

    public List<RoutePoint> getRoute() {
        return route;
    }

    public void setRoute(List<RoutePoint> route) {
        this.route = route;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getRealWorldDistance() {
        return realWorldDistance;
    }

    public void setRealWorldDistance(String realWorldDistance) {
        this.realWorldDistance = realWorldDistance;
    }

    public String getEstimatedDriveTime() {
        return estimatedDriveTime;
    }

    public void setEstimatedDriveTime(String estimatedDriveTime) {
        this.estimatedDriveTime = estimatedDriveTime;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public SolutionStatus getStatus() {
        return status;
    }

    public void setStatus(SolutionStatus status) {
        this.status = status;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getPointCount() {
        return pointCount;
    }

    public void setPointCount(Integer pointCount) {
        this.pointCount = pointCount;
    }

    public Boolean getRealWorldDemo() {
        return realWorldDemo;
    }

    public void setRealWorldDemo(Boolean realWorldDemo) {
        this.realWorldDemo = realWorldDemo;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public List<AddressInfo> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressInfo> addresses) {
        this.addresses = addresses;
    }

    @Embeddable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddressInfo {
        private String address;
        
        @Embedded
        private Coordinates coordinates;
        
        private String placeId;

        public AddressInfo() {}

        public AddressInfo(String address, Coordinates coordinates, String placeId) {
            this.address = address;
            this.coordinates = coordinates;
            this.placeId = placeId;
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
        }

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }
    }
}