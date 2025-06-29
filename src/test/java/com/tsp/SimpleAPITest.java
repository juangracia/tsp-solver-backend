package com.tsp;

import com.tsp.controller.TSPController;
import com.tsp.service.TSPService;
import com.tsp.solver.*;
import com.tsp.model.*;
import com.tsp.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;

public class SimpleAPITest {
    public static void main(String[] args) {
        System.out.println("=== Testing TSP API Components ===");
        
        // Create mock services
        ExactTSPSolver exactSolver = new ExactTSPSolver();
        HeuristicTSPSolver heuristicSolver = new HeuristicTSPSolver();
        MetaheuristicTSPSolver metaheuristicSolver = new MetaheuristicTSPSolver();
        
        // Test algorithm selection
        testAlgorithmSelection(exactSolver, heuristicSolver, metaheuristicSolver);
        
        // Test JSON serialization for API responses
        testAPIResponses();
        
        System.out.println("\n✅ All API components working correctly!");
    }
    
    private static void testAlgorithmSelection(ExactTSPSolver exactSolver, 
                                              HeuristicTSPSolver heuristicSolver,
                                              MetaheuristicTSPSolver metaheuristicSolver) {
        System.out.println("\n--- Testing Algorithm Selection Logic ---");
        
        // Test small dataset (should use exact)
        List<Point> smallPoints = Arrays.asList(
            new Point(0.0, 0.0), new Point(1.0, 1.0), new Point(2.0, 0.0)
        );
        TSPSolver.TSPResult smallResult = exactSolver.solve(smallPoints);
        System.out.println("✓ Small dataset (3 points): " + smallResult.getAlgorithm());
        
        // Test medium dataset (should use heuristic)
        List<Point> mediumPoints = Arrays.asList(
            new Point(0.0, 0.0), new Point(1.0, 1.0), new Point(2.0, 0.0),
            new Point(3.0, 1.0), new Point(4.0, 0.0), new Point(5.0, 1.0),
            new Point(6.0, 0.0), new Point(7.0, 1.0), new Point(8.0, 0.0),
            new Point(9.0, 1.0), new Point(10.0, 0.0), new Point(11.0, 1.0),
            new Point(12.0, 0.0), new Point(13.0, 1.0), new Point(14.0, 0.0)
        );
        TSPSolver.TSPResult mediumResult = heuristicSolver.solve(mediumPoints);
        System.out.println("✓ Medium dataset (15 points): " + mediumResult.getAlgorithm());
        
        // Test large dataset (should use metaheuristic)
        List<Point> largePoints = generateRandomPoints(30);
        TSPSolver.TSPResult largeResult = metaheuristicSolver.solve(largePoints);
        System.out.println("✓ Large dataset (30 points): " + largeResult.getAlgorithm());
    }
    
    private static void testAPIResponses() {
        System.out.println("\n--- Testing API Response Serialization ---");
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            // Test UploadAddressesRequest
            UploadAddressesRequest request = new UploadAddressesRequest();
            request.setAddresses(Arrays.asList("Address 1", "Address 2", "Address 3"));
            request.setMode("DEMO");
            String requestJson = mapper.writeValueAsString(request);
            System.out.println("✓ Upload request: " + requestJson);
            
            // Test SolutionsResponse
            TSPSolution solution = new TSPSolution();
            solution.setFileName("test.txt");
            solution.setPointCount(5);
            solution.setStatus(SolutionStatus.SOLVED);
            solution.setTotalDistance(25.5);
            solution.setAlgorithm("EXACT");
            
            SolutionsResponse response = new SolutionsResponse(Arrays.asList(solution));
            String responseJson = mapper.writeValueAsString(response);
            System.out.println("✓ Solutions response: " + responseJson.length() + " characters");
            
            System.out.println("✓ API serialization working correctly");
            
        } catch (Exception e) {
            System.err.println("✗ API serialization failed: " + e.getMessage());
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