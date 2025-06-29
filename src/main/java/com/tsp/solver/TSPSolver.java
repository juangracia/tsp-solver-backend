package com.tsp.solver;

import com.tsp.model.Point;
import com.tsp.model.RoutePoint;

import java.util.List;

public interface TSPSolver {
    TSPResult solve(List<Point> points);
    
    class TSPResult {
        private final List<RoutePoint> route;
        private final double totalDistance;
        private final long executionTimeMs;
        private final String algorithm;

        public TSPResult(List<RoutePoint> route, double totalDistance, long executionTimeMs, String algorithm) {
            this.route = route;
            this.totalDistance = totalDistance;
            this.executionTimeMs = executionTimeMs;
            this.algorithm = algorithm;
        }

        public List<RoutePoint> getRoute() {
            return route;
        }

        public double getTotalDistance() {
            return totalDistance;
        }

        public long getExecutionTimeMs() {
            return executionTimeMs;
        }

        public String getAlgorithm() {
            return algorithm;
        }
    }
}