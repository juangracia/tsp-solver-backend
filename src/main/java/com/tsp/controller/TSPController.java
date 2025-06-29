package com.tsp.controller;

import com.tsp.dto.SolutionsResponse;
import com.tsp.dto.UploadAddressesRequest;
import com.tsp.model.TSPSolution;
import com.tsp.service.TSPService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/tsp")
@CrossOrigin(origins = {"http://localhost:3000", "https://*.vercel.app"})
@Tag(name = "TSP Solver API", description = "API for solving Traveling Salesman Problem with multiple algorithms")
public class TSPController {
    
    @Autowired
    private TSPService tspService;
    
    @Operation(summary = "Upload a file with coordinates", 
        description = "Upload a CSV or TXT file containing coordinates for TSP solving. Each line should contain latitude and longitude separated by comma.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File uploaded and processed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TSPSolution.class))),
        @ApiResponse(responseCode = "400", description = "Invalid file format or content", 
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(mediaType = "text/plain"))
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
        @Parameter(description = "CSV or TXT file with coordinates", required = true)
        @RequestParam("file") MultipartFile file) {
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
    
    @Operation(summary = "Upload a list of addresses", 
        description = "Upload a list of addresses for TSP solving. Addresses will be geocoded to coordinates.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Addresses uploaded and processed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TSPSolution.class))),
        @ApiResponse(responseCode = "400", description = "Invalid addresses format", 
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(mediaType = "text/plain"))
    })
    @PostMapping(value = "/upload-addresses", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadAddresses(
        @Parameter(description = "List of addresses and mode", required = true,
            content = @Content(examples = {
                @ExampleObject(name = "Sample Address Request",
                    value = "{\"addresses\":[\"123 Main St, New York, NY\", \"456 Park Ave, Boston, MA\", \"789 Ocean Blvd, Miami, FL\"],\"mode\":\"DRIVING\"}")
            }))
        @Valid @RequestBody UploadAddressesRequest request) {
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
    
    @Operation(summary = "Solve TSP for a given dataset", 
        description = "Solve the TSP problem for a previously uploaded dataset using the specified algorithm")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "TSP solved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TSPSolution.class))),
        @ApiResponse(responseCode = "400", description = "Invalid parameters or dataset not found", 
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/{id}/solve")
    public ResponseEntity<?> solveTSP(
            @Parameter(description = "Solution ID to solve", required = true, example = "abc123")
            @PathVariable String id,
            @Parameter(description = "Algorithm to use: NEAREST_NEIGHBOR, TWO_OPT, SIMULATED_ANNEALING, or GENETIC", example = "TWO_OPT")
            @RequestParam(required = false) String algorithm,
            @Parameter(description = "Maximum time in seconds for the algorithm to run", example = "30")
            @RequestParam(required = false) Integer maxTime,
            @Parameter(description = "Whether to use real distances from Google Maps API", example = "true")
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
    
    @Operation(summary = "Get a specific TSP solution", 
        description = "Retrieve a specific TSP solution by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solution found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TSPSolution.class))),
        @ApiResponse(responseCode = "404", description = "Solution not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getSolution(
            @Parameter(description = "Solution ID to retrieve", required = true, example = "abc123")
            @PathVariable String id) {
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
    
    @Operation(summary = "Get all TSP solutions", 
        description = "Retrieve all available TSP solutions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solutions retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolutionsResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(mediaType = "text/plain"))
    })
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
    
    @Operation(summary = "Delete a TSP solution", 
        description = "Delete a specific TSP solution by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Solution deleted successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(mediaType = "text/plain"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSolution(
            @Parameter(description = "Solution ID to delete", required = true, example = "abc123")
            @PathVariable String id) {
        try {
            tspService.deleteSolution(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }
}