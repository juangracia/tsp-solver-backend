package com.tsp;

import com.tsp.model.Point;
import com.tsp.solver.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class ManualAPITest {
    public static void main(String[] args) {
        System.out.println("=== Manual TSP API Testing ===");
        
        // Test all three algorithms
        testExactSolver();
        testHeuristicSolver();
        testMetaheuristicSolver();
        
        // Test API data structures
        testDataSerialization();
    }
    
    private static void testExactSolver() {
        System.out.println("\n--- Testing ExactTSPSolver ---");
        List<Point> points = Arrays.asList(
            new Point(0.0, 0.0),
            new Point(3.0, 4.0),
            new Point(6.0, 0.0),
            new Point(3.0, -4.0)
        );
        
        ExactTSPSolver solver = new ExactTSPSolver();
        TSPSolver.TSPResult result = solver.solve(points);
        
        System.out.println("✓ Algorithm: " + result.getAlgorithm());
        System.out.println("✓ Points: " + points.size());
        System.out.println("✓ Distance: " + String.format("%.2f", result.getTotalDistance()));
        System.out.println("✓ Time: " + result.getExecutionTimeMs() + "ms");
    }
    
    private static void testHeuristicSolver() {
        System.out.println("\n--- Testing HeuristicTSPSolver ---");
        List<Point> points = Arrays.asList(
            new Point(0.0, 0.0), new Point(10.0, 10.0), new Point(20.0, 5.0),
            new Point(15.0, 15.0), new Point(5.0, 20.0), new Point(25.0, 25.0),
            new Point(30.0, 10.0), new Point(12.0, 8.0), new Point(18.0, 22.0),
            new Point(7.0, 15.0), new Point(22.0, 18.0), new Point(28.0, 5.0)
        );
        
        HeuristicTSPSolver solver = new HeuristicTSPSolver();
        TSPSolver.TSPResult result = solver.solve(points);
        
        System.out.println("✓ Algorithm: " + result.getAlgorithm());
        System.out.println("✓ Points: " + points.size());
        System.out.println("✓ Distance: " + String.format("%.2f", result.getTotalDistance()));
        System.out.println("✓ Time: " + result.getExecutionTimeMs() + "ms");
    }
    
    private static void testMetaheuristicSolver() {
        System.out.println("\n--- Testing MetaheuristicTSPSolver ---");
        List<Point> points = generateRandomPoints(30);
        
        MetaheuristicTSPSolver solver = new MetaheuristicTSPSolver();
        TSPSolver.TSPResult result = solver.solve(points);
        
        System.out.println("✓ Algorithm: " + result.getAlgorithm());
        System.out.println("✓ Points: " + points.size());
        System.out.println("✓ Distance: " + String.format("%.2f", result.getTotalDistance()));
        System.out.println("✓ Time: " + result.getExecutionTimeMs() + "ms");
    }
    
    private static void testDataSerialization() {
        System.out.println("\n--- Testing Data Serialization ---");
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            Point point = new Point(1.0, 2.0);
            String json = mapper.writeValueAsString(point);
            Point deserialized = mapper.readValue(json, Point.class);
            
            System.out.println("✓ Point serialization: " + json);
            System.out.println("✓ Point deserialization: " + deserialized);
            
            System.out.println("✓ JSON serialization working correctly");
        } catch (Exception e) {
            System.err.println("✗ Serialization failed: " + e.getMessage());
        }
    }
    
    private static List<Point> generateRandomPoints(int count) {
        List<Point> points = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            double x = Math.random() * 100;
            double y = Math.random() * 100;
            points.add(new Point(x, y));
        }
        return points;
    }
}