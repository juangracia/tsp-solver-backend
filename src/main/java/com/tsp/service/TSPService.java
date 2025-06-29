package com.tsp.service;

import com.tsp.model.*;
import com.tsp.repository.TSPSolutionRepository;
import com.tsp.solver.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class TSPService {
    
    @Autowired
    private TSPSolutionRepository repository;
    
    @Autowired
    private ExactTSPSolver exactSolver;
    
    @Autowired
    private HeuristicTSPSolver heuristicSolver;
    
    @Autowired
    private MetaheuristicTSPSolver metaheuristicSolver;
    
    public TSPSolution uploadFile(MultipartFile file) throws IOException {
        validateFile(file);
        
        List<Point> points = parsePointsFromFile(file);
        TSPSolution solution = new TSPSolution(points, file.getOriginalFilename());
        
        return repository.save(solution);
    }
    
    public TSPSolution uploadAddresses(List<String> addresses, String mode) {
        List<Point> points = new ArrayList<>();
        List<TSPSolution.AddressInfo> addressInfos = new ArrayList<>();
        
        for (int i = 0; i < addresses.size(); i++) {
            String address = addresses.get(i);
            // Create coordinates with the same values as x/y for demonstration
            Coordinates coordinates = new Coordinates((double) i, (double) i);
            Point point = new Point((double) i, (double) i, address, coordinates);
            points.add(point);
            
            addressInfos.add(new TSPSolution.AddressInfo(address, null, null));
        }
        
        TSPSolution solution = new TSPSolution(points, "addresses.txt");
        solution.setStatus(SolutionStatus.GEOCODED);
        solution.setRealWorldDemo(true);
        solution.setAddresses(addressInfos);
        
        return repository.save(solution);
    }
    
    public TSPSolution solveTSP(String id, String algorithm, Integer maxTime, Boolean useRealDistances) {
        Optional<TSPSolution> optionalSolution = repository.findById(id);
        if (optionalSolution.isEmpty()) {
            throw new RuntimeException("Solution not found with id: " + id);
        }
        
        TSPSolution solution = optionalSolution.get();
        solution.setStatus(SolutionStatus.SOLVING);
        repository.save(solution);
        
        try {
            TSPSolver solver = selectAlgorithm(solution.getPointCount(), algorithm);
            TSPSolver.TSPResult result = solver.solve(solution.getOriginalPoints());
            
            solution.setRoute(result.getRoute());
            solution.setTotalDistance(result.getTotalDistance());
            solution.setAlgorithm(result.getAlgorithm());
            solution.setExecutionTimeMs(result.getExecutionTimeMs());
            solution.setStatus(SolutionStatus.SOLVED);
            
            // Fix route coordinates before saving
            // fixRouteCoordinates(solution);  // Temporarily disabled to test
            
            if (Boolean.TRUE.equals(useRealDistances) && Boolean.TRUE.equals(solution.getRealWorldDemo())) {
                solution.setRealWorldDistance(String.format("%.1f km", result.getTotalDistance()));
                solution.setEstimatedDriveTime(estimateDriveTime(result.getTotalDistance()));
            }
            
            return repository.save(solution);
            
        } catch (Exception e) {
            solution.setStatus(SolutionStatus.ERROR);
            repository.save(solution);
            throw new RuntimeException("Error solving TSP: " + e.getMessage(), e);
        }
    }
    
    public Optional<TSPSolution> getSolution(String id) {
        Optional<TSPSolution> solution = repository.findById(id);
        // solution.ifPresent(this::fixRouteCoordinates);  // Temporarily disabled to test
        return solution;
    }
    
    public List<TSPSolution> getAllSolutions() {
        List<TSPSolution> solutions = repository.findAll();
        // solutions.forEach(this::fixRouteCoordinates);  // Temporarily disabled to test
        return solutions;
    }
    
    /**
     * Fixes the route coordinates after retrieving from the database.
     * This is needed because the coordinates are not properly persisted in the database.
     * 
     * @param solution The solution to fix
     */
    private void fixRouteCoordinates(TSPSolution solution) {
        if (solution.getRoute() == null || solution.getOriginalPoints() == null) {
            return;
        }
        
        // Map of original points by order
        for (int i = 0; i < solution.getRoute().size(); i++) {
            RoutePoint routePoint = solution.getRoute().get(i);
            int order = routePoint.getOrder();
            
            // For order 0, use the first original point
            if (order == 0 && !solution.getOriginalPoints().isEmpty()) {
                Point originalPoint = solution.getOriginalPoints().get(0);
                routePoint.setX(originalPoint.getX());
                routePoint.setY(originalPoint.getY());
                routePoint.setCoordinates(originalPoint.getCoordinates());
                continue;
            }
            
            // For other orders, find the corresponding original point
            if (order > 0 && order <= solution.getOriginalPoints().size()) {
                // The route order is 1-based for points after the first, but the list is 0-based
                Point originalPoint = solution.getOriginalPoints().get(order - 1);
                routePoint.setX(originalPoint.getX());
                routePoint.setY(originalPoint.getY());
                routePoint.setCoordinates(originalPoint.getCoordinates());
            }
        }
    }
    
    public void deleteSolution(String id) {
        repository.deleteById(id);
    }
    
    private TSPSolver selectAlgorithm(int pointCount, String algorithmHint) {
        if ("exact".equalsIgnoreCase(algorithmHint)) {
            return exactSolver;
        } else if ("heuristic".equalsIgnoreCase(algorithmHint)) {
            return heuristicSolver;
        } else if ("genetic".equalsIgnoreCase(algorithmHint) || "metaheuristic".equalsIgnoreCase(algorithmHint)) {
            return metaheuristicSolver;
        }
        
        if (pointCount <= 10) {
            return exactSolver;
        } else if (pointCount <= 25) {
            return heuristicSolver;
        } else {
            return metaheuristicSolver;
        }
    }
    
    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new IllegalArgumentException("File size exceeds limit (10MB)");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".txt")) {
            throw new IllegalArgumentException("File must be a .txt file");
        }
    }
    
    private List<Point> parsePointsFromFile(MultipartFile file) throws IOException {
        List<Point> points = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] coordinates = line.split(",");
                if (coordinates.length != 2) {
                    throw new IllegalArgumentException("Invalid format at line " + lineNumber + ". Expected: x,y");
                }
                
                try {
                    double x = Double.parseDouble(coordinates[0].trim());
                    double y = Double.parseDouble(coordinates[1].trim());
                    points.add(new Point(x, y));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid coordinates at line " + lineNumber + ": " + line);
                }
            }
        }
        
        if (points.size() < 3) {
            throw new IllegalArgumentException("At least 3 points are required");
        }
        
        if (points.size() > 1000) {
            throw new IllegalArgumentException("Maximum 1000 points allowed");
        }
        
        return points;
    }
    
    private String estimateDriveTime(double distanceKm) {
        double averageSpeedKmh = 50.0;
        double timeHours = distanceKm / averageSpeedKmh;
        
        int hours = (int) timeHours;
        int minutes = (int) ((timeHours - hours) * 60);
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
}