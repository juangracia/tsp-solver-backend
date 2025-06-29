package com.tsp.solver;

import com.tsp.model.Point;
import com.tsp.model.RoutePoint;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HeuristicTSPSolver implements TSPSolver {
    
    @Override
    public TSPResult solve(List<Point> points) {
        long startTime = System.currentTimeMillis();
        
        if (points.size() < 2) {
            List<RoutePoint> route = new ArrayList<>();
            for (int i = 0; i < points.size(); i++) {
                route.add(new RoutePoint(points.get(i), i));
            }
            return new TSPResult(route, 0.0, System.currentTimeMillis() - startTime, "NEAREST_NEIGHBOR_2OPT");
        }
        
        List<Integer> tour = nearestNeighborConstruction(points);
        
        tour = twoOptImprovement(points, tour);
        
        List<RoutePoint> route = buildRoute(points, tour);
        double totalDistance = calculateTourDistance(points, tour);
        long executionTime = System.currentTimeMillis() - startTime;
        
        return new TSPResult(route, totalDistance, executionTime, "NEAREST_NEIGHBOR_2OPT");
    }
    
    private List<Integer> nearestNeighborConstruction(List<Point> points) {
        int n = points.size();
        List<Integer> tour = new ArrayList<>();
        boolean[] visited = new boolean[n];
        
        int current = 0;
        tour.add(current);
        visited[current] = true;
        
        for (int i = 1; i < n; i++) {
            int nearest = -1;
            double nearestDistance = Double.MAX_VALUE;
            
            for (int j = 0; j < n; j++) {
                if (!visited[j]) {
                    double distance = points.get(current).distanceTo(points.get(j));
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearest = j;
                    }
                }
            }
            
            if (nearest != -1) {
                tour.add(nearest);
                visited[nearest] = true;
                current = nearest;
            }
        }
        
        return tour;
    }
    
    private List<Integer> twoOptImprovement(List<Point> points, List<Integer> tour) {
        List<Integer> bestTour = new ArrayList<>(tour);
        double bestDistance = calculateTourDistance(points, bestTour);
        boolean improved = true;
        
        while (improved) {
            improved = false;
            
            for (int i = 1; i < tour.size() - 2; i++) {
                for (int j = i + 1; j < tour.size(); j++) {
                    if (j - i == 1) continue;
                    
                    List<Integer> newTour = twoOptSwap(tour, i, j);
                    double newDistance = calculateTourDistance(points, newTour);
                    
                    if (newDistance < bestDistance) {
                        bestTour = newTour;
                        bestDistance = newDistance;
                        tour = new ArrayList<>(newTour);
                        improved = true;
                    }
                }
            }
        }
        
        return bestTour;
    }
    
    private List<Integer> twoOptSwap(List<Integer> tour, int i, int j) {
        List<Integer> newTour = new ArrayList<>();
        
        for (int k = 0; k <= i - 1; k++) {
            newTour.add(tour.get(k));
        }
        
        for (int k = j; k >= i; k--) {
            newTour.add(tour.get(k));
        }
        
        for (int k = j + 1; k < tour.size(); k++) {
            newTour.add(tour.get(k));
        }
        
        return newTour;
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