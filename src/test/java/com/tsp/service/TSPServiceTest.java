package com.tsp.service;

import com.tsp.model.Point;
import com.tsp.model.SolutionStatus;
import com.tsp.model.TSPSolution;
import com.tsp.repository.TSPSolutionRepository;
import com.tsp.solver.ExactTSPSolver;
import com.tsp.solver.HeuristicTSPSolver;
import com.tsp.solver.MetaheuristicTSPSolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TSPServiceTest {
    
    @Mock
    private TSPSolutionRepository repository;
    
    @Mock
    private ExactTSPSolver exactSolver;
    
    @Mock
    private HeuristicTSPSolver heuristicSolver;
    
    @Mock
    private MetaheuristicTSPSolver metaheuristicSolver;
    
    @InjectMocks
    private TSPService tspService;
    
    private TSPSolution testSolution;
    private List<Point> testPoints;
    
    @BeforeEach
    void setUp() {
        testPoints = Arrays.asList(
            new Point(0.0, 0.0),
            new Point(1.0, 1.0),
            new Point(2.0, 0.0)
        );
        
        testSolution = new TSPSolution(testPoints, "test.txt");
        testSolution.setId("test-id");
    }
    
    @Test
    void testUploadAddresses() {
        List<String> addresses = Arrays.asList(
            "Address 1",
            "Address 2",
            "Address 3"
        );
        
        when(repository.save(any(TSPSolution.class))).thenReturn(testSolution);
        
        TSPSolution result = tspService.uploadAddresses(addresses, "DEMO");
        
        assertNotNull(result);
        assertEquals(SolutionStatus.GEOCODED, result.getStatus());
        assertTrue(result.getRealWorldDemo());
        verify(repository).save(any(TSPSolution.class));
    }
    
    @Test
    void testGetSolution() {
        when(repository.findById("test-id")).thenReturn(Optional.of(testSolution));
        
        Optional<TSPSolution> result = tspService.getSolution("test-id");
        
        assertTrue(result.isPresent());
        assertEquals(testSolution, result.get());
        verify(repository).findById("test-id");
    }
    
    @Test
    void testGetSolutionNotFound() {
        when(repository.findById("non-existent")).thenReturn(Optional.empty());
        
        Optional<TSPSolution> result = tspService.getSolution("non-existent");
        
        assertFalse(result.isPresent());
        verify(repository).findById("non-existent");
    }
    
    @Test
    void testGetAllSolutions() {
        List<TSPSolution> solutions = Arrays.asList(testSolution);
        when(repository.findAll()).thenReturn(solutions);
        
        List<TSPSolution> result = tspService.getAllSolutions();
        
        assertEquals(1, result.size());
        assertEquals(testSolution, result.get(0));
        verify(repository).findAll();
    }
    
    @Test
    void testDeleteSolution() {
        tspService.deleteSolution("test-id");
        
        verify(repository).deleteById("test-id");
    }
}