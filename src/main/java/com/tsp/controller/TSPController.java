package com.tsp.controller;

import com.tsp.dto.SolutionsResponse;
import com.tsp.dto.UploadAddressesRequest;
import com.tsp.model.TSPSolution;
import com.tsp.service.TSPService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/tsp")
@CrossOrigin(origins = {"http://localhost:3000", "https://*.vercel.app"})
public class TSPController {
    
    @Autowired
    private TSPService tspService;
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            TSPSolution solution = tspService.uploadFile(file);
            return ResponseEntity.ok(solution);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error reading file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }
    
    @PostMapping("/upload-addresses")
    public ResponseEntity<?> uploadAddresses(@Valid @RequestBody UploadAddressesRequest request) {
        try {
            TSPSolution solution = tspService.uploadAddresses(request.getAddresses(), request.getMode());
            return ResponseEntity.ok(solution);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/solve")
    public ResponseEntity<?> solveTSP(
            @PathVariable String id,
            @RequestParam(required = false) String algorithm,
            @RequestParam(required = false) Integer maxTime,
            @RequestParam(required = false) Boolean useRealDistances) {
        try {
            TSPSolution solution = tspService.solveTSP(id, algorithm, maxTime, useRealDistances);
            return ResponseEntity.ok(solution);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getSolution(@PathVariable String id) {
        try {
            Optional<TSPSolution> solution = tspService.getSolution(id);
            if (solution.isPresent()) {
                return ResponseEntity.ok(solution.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllSolutions() {
        try {
            SolutionsResponse response = new SolutionsResponse(tspService.getAllSolutions());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSolution(@PathVariable String id) {
        try {
            tspService.deleteSolution(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }
}