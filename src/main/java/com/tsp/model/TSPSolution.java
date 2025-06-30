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
    

    public TSPSolution() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.status = SolutionStatus.UPLOADED;
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



}