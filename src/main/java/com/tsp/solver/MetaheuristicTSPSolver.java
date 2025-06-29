package com.tsp.solver;

import com.tsp.model.Point;
import com.tsp.model.RoutePoint;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MetaheuristicTSPSolver implements TSPSolver {
    
    private static final double INITIAL_TEMPERATURE = 10000.0;
    private static final double COOLING_RATE = 0.995;
    private static final double MIN_TEMPERATURE = 1.0;
    private static final int ITERATIONS_PER_TEMPERATURE = 100;
    
    private final Random random = new Random();
    
    @Override
    public TSPResult solve(List<Point> points) {
        long startTime = System.currentTimeMillis();
        
        if (points.size() < 2) {
            List<RoutePoint> route = new ArrayList<>();
            for (int i = 0; i < points.size(); i++) {
                route.add(new RoutePoint(points.get(i), i));
            }
            return new TSPResult(route, 0.0, System.currentTimeMillis() - startTime, "SIMULATED_ANNEALING");
        }
        
        List<Integer> bestTour = simulatedAnnealing(points);
        
        List<RoutePoint> route = buildRoute(points, bestTour);
        double totalDistance = calculateTourDistance(points, bestTour);
        long executionTime = System.currentTimeMillis() - startTime;
        
        return new TSPResult(route, totalDistance, executionTime, "SIMULATED_ANNEALING");
    }
    
    private List<Integer> simulatedAnnealing(List<Point> points) {
        int n = points.size();
        
        List<Integer> currentTour = generateInitialSolution(n);
        List<Integer> bestTour = new ArrayList<>(currentTour);
        
        double currentDistance = calculateTourDistance(points, currentTour);
        double bestDistance = currentDistance;
        
        double temperature = INITIAL_TEMPERATURE;
        
        while (temperature > MIN_TEMPERATURE) {
            for (int i = 0; i < ITERATIONS_PER_TEMPERATURE; i++) {
                List<Integer> newTour = generateNeighbor(currentTour);
                double newDistance = calculateTourDistance(points, newTour);
                
                if (acceptSolution(currentDistance, newDistance, temperature)) {
                    currentTour = newTour;
                    currentDistance = newDistance;
                    
                    if (newDistance < bestDistance) {
                        bestTour = new ArrayList<>(newTour);
                        bestDistance = newDistance;
                    }
                }
            }
            
            temperature *= COOLING_RATE;
        }
        
        return bestTour;
    }
    
    private List<Integer> generateInitialSolution(int n) {
        List<Integer> tour = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            tour.add(i);
        }
        Collections.shuffle(tour, random);
        return tour;
    }
    
    private List<Integer> generateNeighbor(List<Integer> tour) {
        List<Integer> neighbor = new ArrayList<>(tour);
        int n = neighbor.size();
        
        if (random.nextDouble() < 0.7) {
            twoOptMutation(neighbor);
        } else if (random.nextDouble() < 0.9) {
            swapMutation(neighbor);
        } else {
            insertMutation(neighbor);
        }
        
        return neighbor;
    }
    
    private void twoOptMutation(List<Integer> tour) {
        int n = tour.size();
        if (n < 4) return;
        
        int i = random.nextInt(n - 2) + 1;
        int j = random.nextInt(n - i - 1) + i + 1;
        
        Collections.reverse(tour.subList(i, j + 1));
    }
    
    private void swapMutation(List<Integer> tour) {
        int n = tour.size();
        if (n < 2) return;
        
        int i = random.nextInt(n);
        int j = random.nextInt(n);
        
        Collections.swap(tour, i, j);
    }
    
    private void insertMutation(List<Integer> tour) {
        int n = tour.size();
        if (n < 3) return;
        
        int i = random.nextInt(n);
        int j = random.nextInt(n);
        
        if (i != j) {
            Integer city = tour.remove(i);
            tour.add(j, city);
        }
    }
    
    private boolean acceptSolution(double currentDistance, double newDistance, double temperature) {
        if (newDistance < currentDistance) {
            return true;
        }
        
        double probability = Math.exp((currentDistance - newDistance) / temperature);
        return random.nextDouble() < probability;
    }
    
    private double calculateTourDistance(List<Point> points, List<Integer> tour) {
        if (tour.size() < 2) return 0.0;
        
        double distance = 0.0;
        
        for (int i = 0; i < tour.size() - 1; i++) {
            distance += points.get(tour.get(i)).distanceTo(points.get(tour.get(i + 1)));
        }
        
        distance += points.get(tour.get(tour.size() - 1)).distanceTo(points.get(tour.get(0)));
        
        return distance;
    }
    
    private List<RoutePoint> buildRoute(List<Point> points, List<Integer> tour) {
        List<RoutePoint> route = new ArrayList<>();
        
        for (int i = 0; i < tour.size(); i++) {
            Point point = points.get(tour.get(i));
            route.add(new RoutePoint(point, i));
        }
        
        return route;
    }
}