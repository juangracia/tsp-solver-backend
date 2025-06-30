package com.tsp.solver;

import com.tsp.model.Point;
import com.tsp.model.RoutePoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Implementation of the TSP Solver that uses exact algorithms to find optimal solutions.
 * <p>
 * This solver uses two different exact approaches based on the problem size:
 * <ul>
 *   <li>For small instances (≤ 8 points): Brute force approach that examines all permutations</li>
 *   <li>For larger instances: Dynamic programming approach based on Held-Karp algorithm</li>
 * </ul>
 * </p>
 * <p>
 * Both approaches guarantee an optimal solution but have exponential time complexity,
 * making them suitable only for relatively small problem instances.
 * </p>
 */
@Component
public class ExactTSPSolver implements TSPSolver {
    
    private static final Logger logger = LoggerFactory.getLogger(ExactTSPSolver.class);
    
    /**
     * Solves the TSP problem using exact algorithms.
     * <p>
     * Chooses between brute force and dynamic programming based on the problem size.
     * For problems with 8 or fewer points, uses brute force; otherwise uses
     * dynamic programming approach.
     * </p>
     *
     * @param points List of points to visit
     * @return TSP solution containing the optimal route
     */
    @Override
    public TSPResult solve(List<Point> points) {
        long startTime = System.currentTimeMillis();
        
        if (points.size() <= 8) {
            return solveBruteForce(points, startTime);
        } else {
            return solveDynamicProgramming(points, startTime);
        }
    }
    
    /**
     * Solves the TSP using a brute force approach by checking all possible permutations.
     * <p>
     * This method has O(n!) time complexity where n is the number of points.
     * It's only practical for very small instances (typically ≤ 10 points).
     * </p>
     *
     * @param points List of points to visit
     * @param startTime Start time of the algorithm for time measurement
     * @return The optimal TSP solution
     */
    private TSPResult solveBruteForce(List<Point> points, long startTime) {
        int n = points.size();
        
        // Handle trivial cases of 0 or 1 points
        if (n < 2) {
            // For 0 or 1 points, simply create a route with no travel needed
            List<RoutePoint> route = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                route.add(new RoutePoint(points.get(i), i));
            }
            TSPSolver.calculateRouteDistances(route);
            return new TSPResult(route, 0.0, System.currentTimeMillis() - startTime, "BRUTE_FORCE");
        }
        
        // Step 1: We assume point 0 is always the starting point (fixed)
        // We'll permute all other points (1 to n-1)
        List<Integer> indices = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            indices.add(i); // Add points 1, 2, ..., n-1 to our permutation list
        }
        
        // Step 2: Track the best solution found so far
        double minDistance = Double.MAX_VALUE;
        List<Integer> bestRoute = null;
        
        // Step 3: Enumerate all permutations of points (except point 0)
        // The nextPermutation function generates the next lexicographically ordered permutation
        do {
            // Calculate the distance of the current permutation
            // The route is: 0 -> indices[0] -> indices[1] -> ... -> indices[n-2] -> 0
            double currentDistance = calculateRouteDistance(points, indices);
            
            // Update the best solution if current one is better
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                bestRoute = new ArrayList<>(indices); // Make a copy of the best route
            }
        } while (nextPermutation(indices)); // Generate the next permutation
        
        // Step 4: Convert the best sequence of indices to a complete route
        List<RoutePoint> route = buildRoute(points, bestRoute);
        TSPSolver.calculateRouteDistances(route);
        long executionTime = System.currentTimeMillis() - startTime;
        
        return new TSPResult(route, minDistance, executionTime, "BRUTE_FORCE");
    }
    
    /**
     * Solves the TSP using the Held-Karp dynamic programming approach.
     * <p>
     * This algorithm has O(n²·2ⁿ) time complexity where n is the number of points,
     * making it faster than brute force but still exponential. It's typically
     * practical for problems up to ~20-25 points.
     * </p>
     *
     * @param points List of points to visit
     * @param startTime Start time of the algorithm for time measurement
     * @return The optimal TSP solution
     */
    private TSPResult solveDynamicProgramming(List<Point> points, long startTime) {
        int n = points.size();
        
        // Handle trivial cases of 0 or 1 points
        if (n < 2) {
            List<RoutePoint> route = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                route.add(new RoutePoint(points.get(i), i));
            }
            TSPSolver.calculateRouteDistances(route);
            return new TSPResult(route, 0.0, System.currentTimeMillis() - startTime, "DYNAMIC_PROGRAMMING");
        }
        
        // Step 1: Precompute all pairwise distances between points
        // This avoids repeatedly calculating distances during the algorithm
        double[][] dist = buildDistanceMatrix(points);
        
        // Step 2: Initialize memory structures for dynamic programming
        // memo stores the minimum distance for each subproblem
        // parent stores the next point to visit in the optimal solution
        Map<String, Double> memo = new HashMap<>();
        Map<String, Integer> parent = new HashMap<>();
        
        // Step 3: Run the Held-Karp recursive algorithm with memoization
        // Start at point 0, with a mask of 1 (indicating point 0 is visited)
        double minCost = tspDP(0, 1, n, dist, memo, parent);
        
        // Step 4: Reconstruct the optimal path from the parent map
        // This traces backward from the parent pointers to build the full path
        List<Integer> path = reconstructPath(0, 1, n, parent);
        
        // Step 5: Convert the path of indices to a complete route of points
        List<RoutePoint> route = new ArrayList<>();
        for (int i = 0; i < path.size(); i++) {
            Point point = points.get(path.get(i));
            route.add(new RoutePoint(point, i));
        }
        
        // Step 6: Calculate distances for the route
        TSPSolver.calculateRouteDistances(route);
        
        // Step 7: Measure execution time and return the result
        long executionTime = System.currentTimeMillis() - startTime;
        return new TSPResult(route, minCost, executionTime, "DYNAMIC_PROGRAMMING");
    }
    
    /**
     * Core recursive function for the Held-Karp dynamic programming algorithm.
     * <p>
     * This method computes the shortest path that starts at point 0, visits all points in the mask,
     * and ends at position 'pos'. It uses memoization to avoid recomputing subproblems.
     * </p>
     *
     * @param pos Current position (point index)
     * @param mask Bitmask representing visited cities (1 bit = visited)
     * @param n Total number of cities
     * @param dist Distance matrix between cities
     * @param memo Memoization cache to store computed results
     * @param parent Parent map to reconstruct the optimal path
     * @return Length of the shortest path
     */
    private double tspDP(int pos, int mask, int n, double[][] dist, Map<String, Double> memo, Map<String, Integer> parent) {
        // Base case: If all cities are visited (mask has all bits set)
        // then just return to the starting point (point 0)
        if (mask == (1 << n) - 1) {
            return dist[pos][0]; // Return to starting point to complete the tour
        }
        
        // Create a unique key for this subproblem based on position and visited mask
        String key = pos + "," + mask;
        
        // Check if we've already solved this subproblem (memoization)
        if (memo.containsKey(key)) {
            return memo.get(key); // Return cached result
        }
        
        // Find the best point to visit next
        double result = Double.MAX_VALUE;
        int bestNext = -1;
        
        // Try each unvisited point as the next step
        for (int pointIndex = 0; pointIndex < n; pointIndex++) {
            // Check if point is not yet visited (bit is not set in mask)
            if ((mask & (1 << pointIndex)) == 0) {
                // Mark this point as visited
                int newMask = mask | (1 << pointIndex);
                
                // Calculate cost: distance to this point + best path from this point
                double cost = dist[pos][pointIndex] + tspDP(pointIndex, newMask, n, dist, memo, parent);
                
                // Update if this is a better path
                if (cost < result) {
                    result = cost;
                    bestNext = pointIndex;
                }
            }
        }
        
        // Save the result for this subproblem
        memo.put(key, result);
        
        // Save the best next point for path reconstruction
        if (bestNext != -1) {
            parent.put(key, bestNext);
        }
        
        return result;
    }
    
    /**
     * Reconstructs the optimal path found by the dynamic programming algorithm.
     * <p>
     * Uses the parent map built during the DP computation to trace back the sequence of cities.
     * </p>
     *
     * @param start Starting point index (typically 0)
     * @param mask Initial mask (typically 1 << start)
     * @param n Total number of cities
     * @param parent Parent map from DP computation
     * @return List of point indices in the optimal order
     */
    private List<Integer> reconstructPath(int start, int mask, int n, Map<String, Integer> parent) {
        // Initialize the path with the starting point
        List<Integer> path = new ArrayList<>();
        int current = start;
        int currentMask = mask;
        
        path.add(current); // Add the starting point (typically point 0)
        
        // Keep adding cities until all are visited (full mask would be 2^n - 1)
        while (currentMask != (1 << n) - 1) {
            // Look up the next point in the parent map
            String key = current + "," + currentMask;
            
            // Safety check in case of inconsistent parent data
            if (!parent.containsKey(key)) break;
            
            // Get the next point in the optimal path
            int next = parent.get(key);
            path.add(next); // Add to our path
            
            // Update our state:
            currentMask |= (1 << next); // Mark the next point as visited in the mask
            current = next; // Move to the next point
        }
        
        return path;
    }
    
    /**
     * Builds a distance matrix between all pairs of points.
     * <p>
     * Pre-computing distances improves performance by avoiding redundant distance calculations.
     * </p>
     *
     * @param points List of points
     * @return 2D array where dist[i][j] contains the distance from point i to point j
     */
    private double[][] buildDistanceMatrix(List<Point> points) {
        // Get the number of points
        int n = points.size();
        
        // Create an n×n matrix to hold all pairwise distances
        double[][] dist = new double[n][n];
        
        // Calculate the distance between each pair of points
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) { // Skip the distance from a point to itself (it's 0)
                    dist[i][j] = points.get(i).distanceTo(points.get(j));
                }
                // Note: dist[i][i] is implicitly left as 0.0
            }
        }
        
        return dist;
    }
    
    /**
     * Calculates the total distance of a route.
     * <p>
     * Includes the distance from the last point back to the starting point to complete the tour.
     * </p>
     *
     * @param points List of points
     * @param route List of point indices in visitation order
     * @return Total distance of the route
     */
    private double calculateRouteDistance(List<Point> points, List<Integer> route) {
        double distance = 0.0;
        
        // Step 1: Add distance from starting point (0) to the first point in route
        distance += points.get(0).distanceTo(points.get(route.get(0)));
        
        // Step 2: Add distances between consecutive cities in the route
        for (int i = 0; i < route.size() - 1; i++) {
            int currentCity = route.get(i);
            int nextCity = route.get(i + 1);
            distance += points.get(currentCity).distanceTo(points.get(nextCity));
        }
        
        // Step 3: Add distance from the last point back to the starting point (0)  
        // to complete the tour
        int lastCity = route.get(route.size() - 1);
        distance += points.get(lastCity).distanceTo(points.get(0));
        
        return distance;
    }
    
    /**
     * Builds the final route as a list of RoutePoint objects.
     * <p>
     * Converts from a list of point indices to a list of RoutePoint objects
     * that include both the point coordinates and the order in the route.
     * </p>
     *
     * @param points List of original points
     * @param bestRoute List of point indices in the optimal order
     * @return List of RoutePoint objects representing the final solution
     */
    private List<RoutePoint> buildRoute(List<Point> points, List<Integer> bestRoute) {
        // Create a new list to hold the complete route
        List<RoutePoint> route = new ArrayList<>();
        
        // Log original points for debugging
        logger.debug("Original points with coordinates:");
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            logger.debug("Point {}: x={}, y={}, coordinates={}", 
                i, p.getX(), p.getY(), 
                (p.getCoordinates() != null ? 
                    "lat=" + p.getCoordinates().getLat() + ", lng=" + p.getCoordinates().getLng() : "null"));
        }
        
        // Step 1: Add the starting point (point 0) as the first stop
        Point startPoint = points.get(0);
        RoutePoint startRoutePoint = new RoutePoint(startPoint, 0);
        route.add(startRoutePoint);
        logger.debug("Added start point to route: x={}, y={}, coordinates={}, order={}", 
            startRoutePoint.getX(), startRoutePoint.getY(),
            (startRoutePoint.getCoordinates() != null ? 
                "lat=" + startRoutePoint.getCoordinates().getLat() + ", lng=" + startRoutePoint.getCoordinates().getLng() : "null"),
            startRoutePoint.getOrder());
        
        // Step 2: Add each point in the best route with its position in the tour
        for (int i = 0; i < bestRoute.size(); i++) {
            // Get the point corresponding to the point index in bestRoute
            int pointIndex = bestRoute.get(i);
            Point point = points.get(pointIndex);
            
            // Create a RoutePoint with the point and its position (i+1 because point 0 is position 0)
            RoutePoint routePoint = new RoutePoint(point, i + 1);
            route.add(routePoint);
            
            logger.debug("Added route point {}: x={}, y={}, coordinates={}, order={}", 
                i+1, routePoint.getX(), routePoint.getY(),
                (routePoint.getCoordinates() != null ? 
                    "lat=" + routePoint.getCoordinates().getLat() + ", lng=" + routePoint.getCoordinates().getLng() : "null"),
                routePoint.getOrder());
        }
        
        // The route now contains a complete tour: point 0 -> bestRoute[0] -> bestRoute[1] -> ... -> point 0
        return route;
    }
    
    /**
     * Generates the next permutation of the array in lexicographic order.
     * <p>
     * This is an efficient algorithm to iterate through all permutations without
     * storing them all in memory at once. It modifies the input array in-place.
     * </p>
     *
     * @param arr The array to permute
     * @return True if a next permutation exists, false if we've reached the last permutation
     */
    private boolean nextPermutation(List<Integer> arr) {
        // This algorithm finds the next lexicographically greater permutation
        
        // Step 1: Find the largest index i such that arr[i] < arr[i+1]
        // If no such index exists, the permutation is the last one
        int i = arr.size() - 2;
        while (i >= 0 && arr.get(i) >= arr.get(i + 1)) {
            i--;
        }
        
        // If we reached the first element, this is the last permutation
        if (i < 0) return false;
        
        // Step 2: Find the largest index j > i such that arr[j] > arr[i]
        int j = arr.size() - 1;
        while (arr.get(j) <= arr.get(i)) {
            j--;
        }
        
        // Step 3: Swap arr[i] and arr[j]
        Collections.swap(arr, i, j);
        
        // Step 4: Reverse the subarray starting at arr[i+1] to the end
        // This ensures the next permutation is the smallest one that is greater than current
        Collections.reverse(arr.subList(i + 1, arr.size()));
        
        return true; // Successfully found the next permutation
    }
}