package com.tsp.dto;

import com.tsp.model.TSPSolution;

import java.util.List;

public class SolutionsResponse {
    private List<TSPSolution> solutions;
    
    public SolutionsResponse(List<TSPSolution> solutions) {
        this.solutions = solutions;
    }
    
    public List<TSPSolution> getSolutions() {
        return solutions;
    }
    
    public void setSolutions(List<TSPSolution> solutions) {
        this.solutions = solutions;
    }
}