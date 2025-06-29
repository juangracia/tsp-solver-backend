package com.tsp.solver;

import com.tsp.model.Point;
import com.tsp.model.RoutePoint;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Implementation of TSP Solver that uses heuristic approaches.
 * <p>
 * This solver uses a combination of two heuristic algorithms:
 * <ul>
 *   <li><b>Nearest Neighbor Construction:</b> Quickly builds an initial tour by always visiting
 *       the nearest unvisited city next</li>
 *   <li><b>2-Opt Improvement:</b> Refines the initial tour by repeatedly swapping pairs of edges
 *       when it reduces the total distance</li>
 * </ul>
 * </p>
 * <p>
 * This approach provides a good balance between solution quality and computational efficiency.
 * It typically produces solutions within 10-15% of optimal in polynomial time, making it suitable
 * for larger problem instances (hundreds or thousands of points).
 * </p>
 */
@Component
public class HeuristicTSPSolver implements TSPSolver {
    
    /**
     * Solves the TSP using a combined nearest neighbor and 2-opt improvement approach.
     * <p>
     * First constructs a tour using the nearest neighbor algorithm, then improves it
     * using the 2-opt local search algorithm until no further improvements can be made.
     * </p>
     *
     * @param points List of points to visit
     * @return TSP solution containing the optimized route
     */
    @Override
    public TSPResult solve(List<Point> points) {
        // Step 0: Record start time to measure the algorithm's performance
        long startTime = System.currentTimeMillis();
        
        if (points.size() < 2) {
            List<RoutePoint> route = new ArrayList<>();
            for (int i = 0; i < points.size(); i++) {
                route.add(new RoutePoint(points.get(i), i));
            }
            TSPSolver.calculateRouteDistances(route);
            return new TSPResult(route, 0.0, System.currentTimeMillis() - startTime, "NEAREST_NEIGHBOR_2OPT");
        }
        
        // Step 1: Build an initial tour using the Nearest Neighbor heuristic
        List<Integer> tour = nearestNeighborConstruction(points);
        
        // Step 2: Improve the initial tour using 2-opt local search
        tour = twoOptImprovement(points, tour);
        
        // Step 3: Convert the optimized tour (list of indices) to a list of RoutePoints
        List<RoutePoint> route = buildRoute(points, tour);
        
        // Step 4: Calculate the total distance of the final route
        double totalDistance = 0.0;
        
        // Calculate the total distance using the calculateTourDistance method
        // which already handles the complete tour distance
        totalDistance = calculateTourDistance(points, tour);
        
        // Step 5: Calculate distances for the route
        TSPSolver.calculateRouteDistances(route);
        
        // Step 6: Calculate execution time
        long executionTime = System.currentTimeMillis() - startTime;
        
        // Step 7: Return the final solution
        return new TSPResult(route, totalDistance, executionTime, "NEAREST_NEIGHBOR_2OPT");
    }
    
    /**
     * Constructs an initial TSP tour using the Nearest Neighbor algorithm.
     * <p>
     * This greedy algorithm starts from the first city and repeatedly visits
     * the nearest unvisited city until all cities have been visited.
     * </p>
     * <p>
     * Time complexity: O(n²) where n is the number of points.
     * </p>
     *
     * @param points List of points to visit
     * @return List of city indices in the constructed tour order
     */
    private List<Integer> nearestNeighborConstruction(List<Point> points) {
        int n = points.size();
        List<Integer> tour = new ArrayList<>();  // Will hold the ordered tour
        Set<Integer> visited = new HashSet<>();  // Tracks which cities have been visited
        
        // Handle edge case of empty input
        if (n == 0) return tour;
        
        // Step 1: Always start with the first city (index 0)
        int currentCity = 0;
        tour.add(currentCity);  // Add starting city to tour
        visited.add(currentCity);  // Mark as visited
        
        // Step 2: Iteratively find and add the nearest unvisited city
        // Continue until all cities are in the tour
        while (visited.size() < n) {
            double minDistance = Double.MAX_VALUE;
            int nearestCity = -1;
            
            // Step 2a: Find the nearest unvisited city to the current city
            for (int i = 0; i < n; i++) {
                // Skip cities we've already visited
                if (visited.contains(i)) continue;
                
                // Calculate distance to this unvisited city
                double distance = points.get(currentCity).distanceTo(points.get(i));
                
                // Update if this is the closest unvisited city so far
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestCity = i;
                }
            }
            
            // Step 2b: Add the nearest city to our tour
            if (nearestCity != -1) {
                tour.add(nearestCity);          // Add to tour
                visited.add(nearestCity);       // Mark as visited
                currentCity = nearestCity;      // Move to this city
            } else {
                // This should never happen if the graph is complete
                break;
            }
        }
        
        return tour;  // Return the completed tour
    }
    
    /**
     * Improves a tour using the 2-opt local search algorithm.
     * <p>
     * The 2-opt algorithm works by repeatedly finding pairs of edges (i,i+1) and (j,j+1)
     * such that replacing them with edges (i,j) and (i+1,j+1) would result in a shorter tour.
     * </p>
     * <p>
     * Time complexity: O(n²) per iteration, where n is the number of points.
     * The number of iterations varies but is typically small for most instances.
     * </p>
     *
     * @param points List of points
     * @param tour Initial tour to improve
     * @return Improved tour after 2-opt optimization
     */
    private List<Integer> twoOptImprovement(List<Point> points, List<Integer> tour) {
        // Make a copy of the input tour as our current best solution
        List<Integer> bestTour = new ArrayList<>(tour);
        
        // Calculate the distance of the initial tour
        double bestDistance = calculateTourDistance(points, bestTour);
        
        // We'll continue trying swaps until no more improvements can be found
        boolean improved = true;
        
        // Main 2-opt improvement loop
        while (improved) {
            improved = false;  // Assume no improvement will be found in this iteration
            
            // Step 1: Try all possible pairs of non-adjacent edges
            for (int i = 0; i < tour.size() - 1; i++) {
                for (int j = i + 1; j < tour.size(); j++) {
                    // Skip adjacent edges as swapping them doesn't change the tour
                    if (j - i == 1) continue;
                    
                    // Step 2: Perform a 2-opt swap by reversing the path between i and j
                    // This effectively removes edges (i,i+1) and (j,j+1) and adds (i,j) and (i+1,j+1)
                    List<Integer> newTour = twoOptSwap(bestTour, i, j);
                    
                    // Step 3: Calculate the distance of the new tour
                    double newDistance = calculateTourDistance(points, newTour);
                    
                    // Step 4: If the new tour is better, accept it as our new best solution
                    if (newDistance < bestDistance) {
                        bestDistance = newDistance;  // Update best distance
                        bestTour = newTour;          // Update best tour
                        improved = true;             // Mark that we found an improvement
                        
                        // Break out of the inner loop to restart with the new tour
                        // This is a common optimization in 2-opt called "first improvement"
                        break;
                    }
                }
                // If we found an improvement, break out of the outer loop too
                if (improved) break;
            }
            // The while loop will continue if we found an improvement
        }
        
        return bestTour;  // Return the optimized tour
    }
    
    /**
     * Performs a 2-opt swap operation on the tour.
     * <p>
     * Removes edges (i-1,i) and (j,j+1), and adds edges (i-1,j) and (i,j+1),
     * effectively reversing the path from i to j.
     * </p>
     *
     * @param tour Current tour
     * @param i Start index of the segment to reverse
     * @param j End index of the segment to reverse
     * @return New tour after the swap
     */
    private List<Integer> twoOptSwap(List<Integer> tour, int i, int j) {
        // This method implements a 2-opt swap by creating a new tour with a reversed segment
        List<Integer> newTour = new ArrayList<>();
        
        // Step 1: Add all cities from the beginning of the tour up to index i (unchanged)
        for (int k = 0; k <= i; k++) {
            newTour.add(tour.get(k));
        }
        
        // Step 2: Add cities from index j down to i+1 (in reverse order)
        // This is the key part of the 2-opt swap - we reverse the segment between i and j
        for (int k = j; k > i; k--) {
            newTour.add(tour.get(k));
        }
        
        // Step 3: Add all remaining cities from j+1 to the end (unchanged)
        for (int k = j + 1; k < tour.size(); k++) {
            newTour.add(tour.get(k));
        }
        
        // The new tour now has the segment between i and j reversed
        return newTour;
    }
    
    /**
     * Calculates the total distance of a tour.
     * <p>
     * Includes the return distance from the last city to the first city
     * to complete the tour.
     * </p>
     *
     * @param points List of points
     * @param tour List of city indices in visitation order
     * @return Total distance of the tour
     */
    private double calculateTourDistance(List<Point> points, List<Integer> tour) {
        // Handle edge case of tours with fewer than 2 cities
        if (tour.size() < 2) return 0.0;
        
        double distance = 0.0;
        
        // Step 1: Calculate the distance between consecutive cities in the tour
        for (int i = 0; i < tour.size() - 1; i++) {
            int currentCity = tour.get(i);
            int nextCity = tour.get(i + 1);
            distance += points.get(currentCity).distanceTo(points.get(nextCity));
        }
        
        // Step 2: Complete the tour by adding the distance from the last city back to the first
        int lastCity = tour.get(tour.size() - 1);
        int firstCity = tour.get(0);
        distance += points.get(lastCity).distanceTo(points.get(firstCity));
        
        return distance;
    }
    
    /**
     * Builds the final route as a list of RoutePoint objects.
     * <p>
     * Converts from a list of city indices to a list of RoutePoint objects
     * that include both the point coordinates and the order in the route.
     * </p>
     *
     * @param points List of original points
     * @param tour List of city indices in the optimal order
     * @return List of RoutePoint objects representing the final solution
     */
    private List<RoutePoint> buildRoute(List<Point> points, List<Integer> tour) {
        // Create a new list to hold the route points in order
        List<RoutePoint> route = new ArrayList<>();
        
        // Convert each city index in the tour to a RoutePoint with its position
        for (int i = 0; i < tour.size(); i++) {
            // Get the point corresponding to this city in the tour
            int cityIndex = tour.get(i);
            Point point = points.get(cityIndex);
            
            // Create a RoutePoint with the city's coordinates and its position in the tour
            route.add(new RoutePoint(point, i));
        }
        
        return route;
    }
}