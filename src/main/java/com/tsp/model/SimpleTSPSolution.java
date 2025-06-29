package com.tsp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tsp_solutions")
public class SimpleTSPSolution {
    
    @Id
    private String id;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "original_points", columnDefinition = "TEXT")
    private String originalPointsJson;
    
    @Column(name = "route", columnDefinition = "TEXT")
    private String routeJson;
    
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

    public SimpleTSPSolution() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.status = SolutionStatus.UPLOADED;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getOriginalPointsJson() { return originalPointsJson; }
    public void setOriginalPointsJson(String originalPointsJson) { this.originalPointsJson = originalPointsJson; }
    
    public String getRouteJson() { return routeJson; }
    public void setRouteJson(String routeJson) { this.routeJson = routeJson; }
    
    public Double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }
    
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    
    public SolutionStatus getStatus() { return status; }
    public void setStatus(SolutionStatus status) { this.status = status; }
    
    public Long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Integer getPointCount() { return pointCount; }
    public void setPointCount(Integer pointCount) { this.pointCount = pointCount; }
}