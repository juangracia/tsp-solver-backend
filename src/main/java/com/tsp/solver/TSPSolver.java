package com.tsp.solver;

import com.tsp.model.Point;
import com.tsp.model.RoutePoint;

import java.util.List;

/**
 * Interface for algorithms that solve the Traveling Salesman Problem (TSP).
 * <p>
 * The Traveling Salesman Problem is a classic algorithmic problem in computer science:
 * Given a list of cities (points) and the distances between each pair of cities, 
 * find the shortest possible route that visits each city exactly once and returns 
 * to the origin city.
 * </p>
 * <p>
 * Implementations of this interface provide different algorithms to solve the TSP
 * with varying trade-offs between solution quality and computational efficiency.
 * </p>
 */
public interface TSPSolver {
    /**
     * Solves the Traveling Salesman Problem for the given set of points.
     *
     * @param points List of points representing locations to visit
     * @return A TSPResult object containing the optimized route, total distance, execution time, and algorithm name
     */
    TSPResult solve(List<Point> points);
    
    /**
     * Container class for the result of a TSP solution.
     * <p>
     * This class encapsulates all the important information about the solution,
     * including the calculated route, total distance, execution time, and the
     * algorithm used to find the solution.
     * </p>
     */
    class TSPResult {
        private final List<RoutePoint> route;
        private final double totalDistance;
        private final long executionTimeMs;
        private final String algorithm;

        /**
         * Constructs a new TSP result with the given parameters.
         *
         * @param route List of RoutePoints in visitation order
         * @param totalDistance The total distance of the route
         * @param executionTimeMs Time taken to compute the solution in milliseconds
         * @param algorithm Name of the algorithm used to generate the solution
         */
        public TSPResult(List<RoutePoint> route, double totalDistance, long executionTimeMs, String algorithm) {
            this.route = route;
            this.totalDistance = totalDistance;
            this.executionTimeMs = executionTimeMs;
            this.algorithm = algorithm;
        }

        /**
         * Gets the ordered list of route points representing the solution path.
         * 
         * @return List of RoutePoints in visitation order
         */
        public List<RoutePoint> getRoute() {
            return route;
        }

        /**
         * Gets the total distance of the route.
         * 
         * @return Total distance in the same units as the input points
         */
        public double getTotalDistance() {
            return totalDistance;
        }

        /**
         * Gets the execution time of the algorithm.
         * 
         * @return Time taken to compute the solution in milliseconds
         */
        public long getExecutionTimeMs() {
            return executionTimeMs;
        }

        /**
         * Gets the name of the algorithm used to generate the solution.
         * 
         * @return Algorithm name as a string
         */
        public String getAlgorithm() {
            return algorithm;
        }
    }
}