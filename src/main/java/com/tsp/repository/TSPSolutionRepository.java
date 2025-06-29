package com.tsp.repository;

import com.tsp.model.TSPSolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TSPSolutionRepository extends JpaRepository<TSPSolution, String> {
    
    List<TSPSolution> findByCreatedAtAfter(LocalDateTime after);
    
    List<TSPSolution> findByAlgorithm(String algorithm);
    
    @Query("SELECT s FROM TSPSolution s ORDER BY s.createdAt DESC")
    List<TSPSolution> findAllOrderByCreatedAtDesc();
    
    long countByPointCountGreaterThan(int pointCount);
}