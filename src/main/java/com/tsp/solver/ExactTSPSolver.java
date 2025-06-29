package com.tsp.solver;

import com.tsp.model.Point;
import com.tsp.model.RoutePoint;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ExactTSPSolver implements TSPSolver {
    
    @Override
    public TSPResult solve(List<Point> points) {
        long startTime = System.currentTimeMillis();
        
        if (points.size() <= 8) {
            return solveBruteForce(points, startTime);
        } else {
            return solveDynamicProgramming(points, startTime);
        }
    }
    
    private TSPResult solveBruteForce(List<Point> points, long startTime) {
        int n = points.size();
        if (n < 2) {
            List<RoutePoint> route = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                route.add(new RoutePoint(points.get(i), i));
            }
            return new TSPResult(route, 0.0, System.currentTimeMillis() - startTime, "BRUTE_FORCE");
        }
        
        List<Integer> indices = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            indices.add(i);
        }
        
        double minDistance = Double.MAX_VALUE;
        List<Integer> bestRoute = null;
        
        do {
            double currentDistance = calculateRouteDistance(points, indices);
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                bestRoute = new ArrayList<>(indices);
            }
        } while (nextPermutation(indices));
        
        List<RoutePoint> route = buildRoute(points, bestRoute);
        long executionTime = System.currentTimeMillis() - startTime;
        
        return new TSPResult(route, minDistance, executionTime, "BRUTE_FORCE");
    }
    
    private TSPResult solveDynamicProgramming(List<Point> points, long startTime) {
        int n = points.size();
        if (n < 2) {
            List<RoutePoint> route = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                route.add(new RoutePoint(points.get(i), i));
            }
            return new TSPResult(route, 0.0, System.currentTimeMillis() - startTime, "DYNAMIC_PROGRAMMING");
        }
        
        double[][] dist = buildDistanceMatrix(points);
        
        Map<String, Double> memo = new HashMap<>();
        Map<String, Integer> parent = new HashMap<>();
        
        double minCost = tspDP(0, 1, n, dist, memo, parent);
        
        List<Integer> path = reconstructPath(0, 1, n, parent);
        List<RoutePoint> route = new ArrayList<>();
        
        for (int i = 0; i < path.size(); i++) {
            Point point = points.get(path.get(i));
            route.add(new RoutePoint(point, i));
        }
        
        long executionTime = System.currentTimeMillis() - startTime;
        return new TSPResult(route, minCost, executionTime, "DYNAMIC_PROGRAMMING");
    }
    
    private double tspDP(int pos, int mask, int n, double[][] dist, Map<String, Double> memo, Map<String, Integer> parent) {
        if (mask == (1 << n) - 1) {
            return dist[pos][0];
        }
        
        String key = pos + "," + mask;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }
        
        double result = Double.MAX_VALUE;
        int bestNext = -1;
        
        for (int city = 0; city < n; city++) {
            if ((mask & (1 << city)) == 0) {
                int newMask = mask | (1 << city);
                double cost = dist[pos][city] + tspDP(city, newMask, n, dist, memo, parent);
                if (cost < result) {
                    result = cost;
                    bestNext = city;
                }
            }
        }
        
        memo.put(key, result);
        if (bestNext != -1) {
            parent.put(key, bestNext);
        }
        
        return result;
    }
    
    private List<Integer> reconstructPath(int start, int mask, int n, Map<String, Integer> parent) {
        List<Integer> path = new ArrayList<>();
        int current = start;
        int currentMask = mask;
        
        path.add(current);
        
        while (currentMask != (1 << n) - 1) {
            String key = current + "," + currentMask;
            if (!parent.containsKey(key)) break;
            
            int next = parent.get(key);
            path.add(next);
            currentMask |= (1 << next);
            current = next;
        }
        
        return path;
    }
    
    private double[][] buildDistanceMatrix(List<Point> points) {
        int n = points.size();
        double[][] dist = new double[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    dist[i][j] = points.get(i).distanceTo(points.get(j));
                }
            }
        }
        
        return dist;
    }
    
    private double calculateRouteDistance(List<Point> points, List<Integer> route) {
        double distance = 0.0;
        
        distance += points.get(0).distanceTo(points.get(route.get(0)));
        
        for (int i = 0; i < route.size() - 1; i++) {
            distance += points.get(route.get(i)).distanceTo(points.get(route.get(i + 1)));
        }
        
        distance += points.get(route.get(route.size() - 1)).distanceTo(points.get(0));
        
        return distance;
    }
    
    private List<RoutePoint> buildRoute(List<Point> points, List<Integer> bestRoute) {
        List<RoutePoint> route = new ArrayList<>();
        
        route.add(new RoutePoint(points.get(0), 0));
        
        for (int i = 0; i < bestRoute.size(); i++) {
            Point point = points.get(bestRoute.get(i));
            route.add(new RoutePoint(point, i + 1));
        }
        
        return route;
    }
    
    private boolean nextPermutation(List<Integer> arr) {
        int i = arr.size() - 2;
        while (i >= 0 && arr.get(i) >= arr.get(i + 1)) {
            i--;
        }
        
        if (i < 0) return false;
        
        int j = arr.size() - 1;
        while (arr.get(j) <= arr.get(i)) {
            j--;
        }
        
        Collections.swap(arr, i, j);
        Collections.reverse(arr.subList(i + 1, arr.size()));
        
        return true;
    }
}