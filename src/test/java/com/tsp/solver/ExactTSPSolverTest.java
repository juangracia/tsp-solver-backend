package com.tsp.solver;

import com.tsp.model.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExactTSPSolverTest {
    
    private ExactTSPSolver solver;
    
    @BeforeEach
    void setUp() {
        solver = new ExactTSPSolver();
    }
    
    @Test
    void testSolveWithSmallInstance() {
        List<Point> points = Arrays.asList(
            new Point(0.0, 0.0),
            new Point(3.0, 4.0),
            new Point(6.0, 0.0),
            new Point(3.0, -4.0),
            new Point(-3.0, 2.0)
        );
        
        TSPSolver.TSPResult result = solver.solve(points);
        
        assertNotNull(result);
        assertNotNull(result.getRoute());
        assertEquals(5, result.getRoute().size());
        assertTrue(result.getTotalDistance() > 0);
        assertTrue(result.getExecutionTimeMs() >= 0);
        assertNotNull(result.getAlgorithm());
        assertTrue(result.getAlgorithm().contains("BRUTE_FORCE") || result.getAlgorithm().contains("DYNAMIC_PROGRAMMING"));
    }
    
    @Test
    void testSolveWithTwoPoints() {
        List<Point> points = Arrays.asList(
            new Point(0.0, 0.0),
            new Point(1.0, 1.0)
        );
        
        TSPSolver.TSPResult result = solver.solve(points);
        
        assertNotNull(result);
        assertEquals(2, result.getRoute().size());
        assertEquals(2.0 * Math.sqrt(2), result.getTotalDistance(), 0.001);
    }
    
    @Test
    void testSolveWithSinglePoint() {
        List<Point> points = Arrays.asList(new Point(0.0, 0.0));
        
        TSPSolver.TSPResult result = solver.solve(points);
        
        assertNotNull(result);
        assertEquals(1, result.getRoute().size());
        assertEquals(0.0, result.getTotalDistance());
    }
}