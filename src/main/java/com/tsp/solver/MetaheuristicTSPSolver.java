package com.tsp.solver;

import com.tsp.model.Point;
import com.tsp.model.RoutePoint;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Implementation of TSP Solver using a metaheuristic approach (Simulated Annealing).
 * <p>
 * Simulated Annealing is a probabilistic technique for approximating the global optimum
 * of a given function. It is inspired by the annealing process in metallurgy where
 * materials are heated and then slowly cooled to increase the size of crystals and reduce defects.
 * </p>
 * <p>
 * Key advantages of Simulated Annealing for TSP:
 * <ul>
 *   <li>Can escape local optima through probabilistic acceptance of worse solutions</li>
 *   <li>Generally produces high-quality solutions for large problem instances</li>
 *   <li>Configurable parameters to balance computation time vs. solution quality</li>
 * </ul>
 * </p>
 */
@Component
public class MetaheuristicTSPSolver implements TSPSolver {
    
    /**
     * Initial temperature for simulated annealing.
     * Higher values increase the probability of accepting worse solutions initially.
     */
    private static final double INITIAL_TEMPERATURE = 10000.0;
    
    /**
     * Cooling rate for temperature reduction.
     * Values closer to 1.0 result in slower cooling and more thorough exploration.
     */
    private static final double COOLING_RATE = 0.995;
    
    /**
     * Minimum temperature at which the algorithm terminates.
     * Lower values allow more exploration but increase computation time.
     */
    private static final double MIN_TEMPERATURE = 1.0;
    
    /**
     * Number of iterations to perform at each temperature level.
     * Higher values improve solution quality but increase computation time.
     */
    private static final int ITERATIONS_PER_TEMPERATURE = 100;
    
    /**
     * Random number generator for probabilistic decisions.
     */
    private final Random random = new Random();
    
    /**
     * Solves the TSP using Simulated Annealing metaheuristic.
     * <p>
     * The algorithm starts with a random tour and progressively improves it through
     * a series of moves, gradually decreasing the temperature parameter to reduce
     * the probability of accepting worse solutions over time.
     * </p>
     *
     * @param points List of points to visit
     * @return TSP solution containing the optimized route
     */
    @Override
    public TSPResult solve(List<Point> points) {
        // Step 0: Start timing the algorithm execution
        long startTime = System.currentTimeMillis();
        
        // Handle trivial cases with 0 or 1 points
        if (points.size() < 2) {
            List<RoutePoint> route = new ArrayList<>();
            for (int i = 0; i < points.size(); i++) {
                route.add(new RoutePoint(points.get(i), i));
            }
            TSPSolver.calculateRouteDistances(route);
            return new TSPResult(route, 0.0, System.currentTimeMillis() - startTime, "SIMULATED_ANNEALING");
        }
        
        // Step 1: Apply the simulated annealing algorithm to find a near-optimal tour
        List<Integer> bestTour = simulatedAnnealing(points);
        
        // Step 2: Convert the tour (list of indices) to a route (list of RoutePoints)
        List<RoutePoint> route = buildRoute(points, bestTour);
        
        // Step 3: Calculate the total distance of the final tour
        double totalDistance = calculateTourDistance(points, bestTour);
        
        // Step 4: Calculate distances for the route
        TSPSolver.calculateRouteDistances(route);
        
        // Step 5: Measure total execution time
        long executionTime = System.currentTimeMillis() - startTime;
        
        // Step 6: Return the TSP solution with route, distance, time, and algorithm name
        return new TSPResult(route, totalDistance, executionTime, "SIMULATED_ANNEALING");
    }
    
    /**
     * Core simulated annealing algorithm implementation.
     * <p>
     * Starting from a random initial solution, the algorithm iteratively:
     * <ol>
     *   <li>Generates a neighboring solution by applying a random move</li>
     *   <li>Evaluates the new solution</li>
     *   <li>Decides whether to accept the new solution based on its quality and current temperature</li>
     *   <li>Gradually reduces the temperature according to the cooling schedule</li>
     * </ol>
     * </p>
     * <p>
     * Time complexity: O(iterations * n), where iterations is determined by
     * temperature parameters and n is the number of points.
     * </p>
     *
     * @param points List of points to visit
     * @return List of point indices in the best found tour order
     */
    private List<Integer> simulatedAnnealing(List<Point> points) {
        int n = points.size();
        
        // Step 1: Generate a random initial tour as our starting point
        List<Integer> currentTour = generateInitialSolution(n);
        List<Integer> bestTour = new ArrayList<>(currentTour);  // Keep track of the best solution found
        
        // Calculate the initial tour's distance
        double currentDistance = calculateTourDistance(points, currentTour);
        double bestDistance = currentDistance;
        
        // Step 2: Initialize the simulated annealing temperature
        // High initial temperature allows accepting worse solutions with higher probability
        double temperature = INITIAL_TEMPERATURE;
        
        // Step 3: Main simulated annealing loop - continue until system is "cooled"
        while (temperature > MIN_TEMPERATURE) {
            // At each temperature, try multiple iterations to explore the state space
            for (int i = 0; i < ITERATIONS_PER_TEMPERATURE; i++) {
                // Step 3a: Generate a neighboring solution by perturbing the current tour
                List<Integer> newTour = generateNeighbor(new ArrayList<>(currentTour));
                double newDistance = calculateTourDistance(points, newTour);
                
                // Step 3b: Decide whether to accept the new solution
                // This is based on both solution quality and current temperature
                if (acceptSolution(currentDistance, newDistance, temperature)) {
                    // Accept the new solution as our current solution
                    currentTour = newTour;
                    currentDistance = newDistance;
                    
                    // Update the best solution if this is better than our previous best
                    if (newDistance < bestDistance) {
                        bestTour = new ArrayList<>(newTour);  // Make a copy to preserve the best tour
                        bestDistance = newDistance;
                    }
                }
                // If not accepted, we keep the current solution and try another neighbor
            }
            
            // Step 3c: Cool down the system according to the cooling schedule
            // This reduces the probability of accepting worse solutions as the algorithm progresses
            temperature *= COOLING_RATE;  // Geometric cooling schedule
        }
        
        // Return the best solution found during the entire annealing process
        return bestTour;
    }
    
    /**
     * Generates a random initial solution for the simulated annealing algorithm.
     * <p>
     * Creates a permutation of all point indices and shuffles it randomly.
     * </p>
     *
     * @param n Number of cities (points)
     * @return Random permutation of point indices
     */
    private List<Integer> generateInitialSolution(int n) {
        // Step 1: Create a list containing all point indices (0 to n-1)
        List<Integer> tour = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            tour.add(i);  // Add each point to the tour
        }
        
        // Step 2: Randomly shuffle the cities to create an initial random tour
        // This provides a different starting point each time the algorithm runs
        Collections.shuffle(tour, random);  // Use our random instance for deterministic testing if needed
        
        return tour;  // Return the randomly generated tour
    }
    
    /**
     * Generates a neighboring solution by applying a random move to the current tour.
     * <p>
     * This method uses a mix of different neighborhood operators with weighted probabilities:
     * <ul>
     *   <li>Two-opt mutation (70%): Reverses a random subsequence of the tour</li>
     *   <li>Swap mutation (20%): Swaps two randomly selected cities</li>
     *   <li>Insert mutation (10%): Removes a city and inserts it elsewhere in the tour</li>
     * </ul>
     * </p>
     *
     * @param tour Current tour
     * @return Modified tour representing a neighbor solution
     */
    private List<Integer> generateNeighbor(List<Integer> tour) {
        // Create a copy of the current tour to modify
        List<Integer> neighbor = new ArrayList<>(tour);
        int n = neighbor.size();
        
        // Step 1: Choose a mutation operator based on probabilities
        // Using multiple operators provides diverse neighborhood exploration
        double p = random.nextDouble();  // Random value between 0.0 and 1.0
        
        // Step 2: Apply the selected mutation operator to generate a new solution
        if (p < 0.7) {           // 70% chance for 2-opt mutation
            // Two-opt tends to work well for TSP, so it gets the highest probability
            twoOptMutation(neighbor);
        } else if (p < 0.9) {    // 20% chance for swap mutation
            // Simple swap of two cities
            swapMutation(neighbor);
        } else {                // 10% chance for insertion mutation
            // Remove a point and insert it elsewhere
            insertMutation(neighbor);
        }
        
        // Return the modified neighbor solution
        return neighbor;
    }
    
    /**
     * Performs a two-opt mutation on the tour.
     * <p>
     * Selects two random positions and reverses the subsequence between them.
     * This effectively breaks two edges and reconnects them in a different way.
     * </p>
     *
     * @param tour Tour to mutate (modified in-place)
     */
    private void twoOptMutation(List<Integer> tour) {
        int n = tour.size();
        // Skip mutation if the tour is too small
        if (n < 4) return;
        
        // Step 1: Select two random positions i and j (not adjacent)
        // We ensure i < j and they have at least one position between them
        int i = 1 + random.nextInt(n - 3);  // i in range [1, n-3]
        int j = i + 1 + random.nextInt(n - i - 1);  // j in range [i+1, n-1]
        
        // Step 2: Reverse the portion of the tour between positions i and j
        // This breaks the edges (i-1,i) and (j,j+1) and adds edges (i-1,j) and (i,j+1)
        Collections.reverse(tour.subList(i, j + 1));
        
        // The tour is now modified in-place with the two-opt move applied
    }
    
    /**
     * Performs a swap mutation on the tour.
     * <p>
     * Randomly selects two cities and swaps their positions in the tour.
     * </p>
     *
     * @param tour Tour to mutate (modified in-place)
     */
    private void swapMutation(List<Integer> tour) {
        int n = tour.size();
        // Skip mutation if the tour is too small
        if (n < 2) return;
        
        // Step 1: Select two random distinct positions i and j
        int i = random.nextInt(n);  // First random position
        
        // Keep selecting j until it's different from i
        int j = random.nextInt(n);
        while (j == i) j = random.nextInt(n);
        
        // Step 2: Swap the cities at positions i and j
        // This is a simple move that exchanges the positions of two cities
        Collections.swap(tour, i, j);
        
        // The tour is now modified in-place with the swap applied
    }
    
    /**
     * Performs an insertion mutation on the tour.
     * <p>
     * Removes a point from one position and inserts it at another position in the tour.
     * </p>
     *
     * @param tour Tour to mutate (modified in-place)
     */
    private void insertMutation(List<Integer> tour) {
        int n = tour.size();
        // Skip mutation if the tour is too small
        if (n < 3) return;
        
        // Step 1: Select a random position i to remove a point from
        int i = random.nextInt(n);
        
        // Step 2: Select a random position j to insert the point
        // Ensure j is not i or immediately after i (which would result in no change)
        int j = random.nextInt(n);
        while (j == i || j == (i + 1) % n) j = random.nextInt(n);
        
        // Step 3: Remove the point from position i
        int pointIndex = tour.remove(i);
        
        // Step 4: Adjust j if necessary (if j was after i, its index decreases after removal)
        if (j > i) j--;
        
        // Step 5: Insert the point at position j
        tour.add(j, pointIndex);
        
        // The tour is now modified in-place with the insertion move applied
    }
    
    /**
     * Determines whether to accept a new solution based on its quality and current temperature.
     * <p>
     * Uses the Metropolis acceptance criterion:
     * <ul>
     *   <li>Always accepts better solutions (shorter tours)</li>
     *   <li>May accept worse solutions with a probability that decreases as temperature decreases</li>
     * </ul>
     * </p>
     *
     * @param currentDistance Distance of the current tour
     * @param newDistance Distance of the proposed new tour
     * @param temperature Current temperature in the annealing schedule
     * @return True if the new solution should be accepted, false otherwise
     */
    private boolean acceptSolution(double currentDistance, double newDistance, double temperature) {
        // Step 1: Always accept better solutions (shorter tours)
        if (newDistance < currentDistance) {
            return true;  // Greedily accept any improvement
        }
        
        // Step 2: For worse solutions, accept with a probability based on:
        // - How much worse the new solution is
        // - The current temperature
        
        // Calculate the acceptance probability using the Metropolis criterion:
        // P = exp(-(newDistance - currentDistance) / temperature)
        // Note: (currentDistance - newDistance) is negative here, making the probability between 0 and 1
        double probability = Math.exp((currentDistance - newDistance) / temperature);
        
        // Accept with calculated probability (higher temp = higher chance to accept worse solutions)
        return random.nextDouble() < probability;  // Return true if a random value is less than our probability
        
        // This probabilistic acceptance is key to escaping local optima in simulated annealing
    }
    
    /**
     * Calculates the total distance of a tour.
     * <p>
     * Includes the return distance from the last point to the first point
     * to complete the tour.
     * </p>
     *
     * @param points List of points
     * @param tour List of point indices in visitation order
     * @return Total distance of the tour
     */
    private double calculateTourDistance(List<Point> points, List<Integer> tour) {
        // Handle trivial case of empty or single-point tour
        if (tour.size() < 2) return 0.0;
        
        double distance = 0.0;
        
        // Step 1: Calculate the distance between consecutive cities in the tour
        for (int i = 0; i < tour.size() - 1; i++) {
            // Get the indices of the current and next cities
            int currentCity = tour.get(i);
            int nextCity = tour.get(i + 1);
            
            // Add the distance between them to the total
            distance += points.get(currentCity).distanceTo(points.get(nextCity));
        }
        
        // Step 2: Complete the tour by adding the distance from last point back to first point
        int lastCity = tour.get(tour.size() - 1);
        int firstCity = tour.get(0);
        distance += points.get(lastCity).distanceTo(points.get(firstCity));
        
        return distance;  // Return the total tour distance
    }
    
    /**
     * Builds the final route as a list of RoutePoint objects.
     * <p>
     * Converts from a list of point indices to a list of RoutePoint objects
     * that include both the point coordinates and the order in the route.
     * </p>
     *
     * @param points List of original points
     * @param tour List of point indices in the optimal order
     * @return List of RoutePoint objects representing the final solution
     */
    private List<RoutePoint> buildRoute(List<Point> points, List<Integer> tour) {
        // Initialize the list that will hold the ordered route points
        List<RoutePoint> route = new ArrayList<>();
        
        // Step 1: Convert each point index in the tour to a RoutePoint
        // The RoutePoint combines the point's coordinates with its position in the tour
        for (int i = 0; i < tour.size(); i++) {
            // Get the point index from the tour
            int pointIndex = tour.get(i);
            
            // Get the actual point coordinates for this point
            Point point = points.get(pointIndex);
            
            // Create a RoutePoint with the coordinates and position in the tour
            route.add(new RoutePoint(point, i));
        }
        
        return route;  // Return the complete route
    }
}