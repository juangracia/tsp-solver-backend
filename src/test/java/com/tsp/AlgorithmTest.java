package com.tsp;

import com.tsp.model.Point;
import com.tsp.solver.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmTest {
    public static void main(String[] args) {
        System.out.println("=== TSP Algorithm Testing ===");
        
        // Test small dataset with exact solver
        testSmallDataset();
        
        // Test medium dataset with heuristic solver
        testMediumDataset();
        
        // Test large dataset with metaheuristic solver
        testLargeDataset();
    }
    
    private static void testSmallDataset() {
        System.out.println("\n--- Testing Small Dataset (5 points) ---");
        List<Point> points = loadTestData("src/test/resources/small_test.txt");
        
        ExactTSPSolver solver = new ExactTSPSolver();
        TSPSolver.TSPResult result = solver.solve(points);
        
        System.out.println("Points: " + points.size());
        System.out.println("Algorithm: " + result.getAlgorithm());
        System.out.println("Distance: " + String.format("%.2f", result.getTotalDistance()));
        System.out.println("Execution time: " + result.getExecutionTimeMs() + "ms");
        System.out.println("Expected optimal: ~16.97");
    }
    
    private static void testMediumDataset() {
        System.out.println("\n--- Testing Medium Dataset (15 points) ---");
        List<Point> points = loadTestData("src/test/resources/medium_test.txt");
        
        HeuristicTSPSolver solver = new HeuristicTSPSolver();
        TSPSolver.TSPResult result = solver.solve(points);
        
        System.out.println("Points: " + points.size());
        System.out.println("Algorithm: " + result.getAlgorithm());
        System.out.println("Distance: " + String.format("%.2f", result.getTotalDistance()));
        System.out.println("Execution time: " + result.getExecutionTimeMs() + "ms");
    }
    
    private static void testLargeDataset() {
        System.out.println("\n--- Testing Large Dataset (Generated 30 points) ---");
        List<Point> points = generateRandomPoints(30);
        
        MetaheuristicTSPSolver solver = new MetaheuristicTSPSolver();
        TSPSolver.TSPResult result = solver.solve(points);
        
        System.out.println("Points: " + points.size());
        System.out.println("Algorithm: " + result.getAlgorithm());
        System.out.println("Distance: " + String.format("%.2f", result.getTotalDistance()));
        System.out.println("Execution time: " + result.getExecutionTimeMs() + "ms");
    }
    
    private static List<Point> loadTestData(String filename) {
        List<Point> points = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] coords = line.trim().split(",");
                if (coords.length == 2) {
                    double x = Double.parseDouble(coords[0]);
                    double y = Double.parseDouble(coords[1]);
                    points.add(new Point(x, y));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading test data: " + e.getMessage());
        }
        return points;
    }
    
    private static List<Point> generateRandomPoints(int count) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double x = Math.random() * 100;
            double y = Math.random() * 100;
            points.add(new Point(x, y));
        }
        return points;
    }
}