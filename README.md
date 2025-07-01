# TSP Solver - Interview Coding Exercise Solution

**Travelling Salesman Problem (TSP) Solution** - A Spring Boot REST API backend that solves the Travelling Salesman Problem using multiple algorithms with automatic selection based on input size.

> This project was initially developed using the specifications in [project-spec.md](./project-spec.md) as an AI-friendly prompt to guide the implementation.

## 🚀 Quick Links

- **🌐 Live Demo**: [https://tsp-solver-frontend.vercel.app/](https://tsp-solver-frontend.vercel.app/)
- **📚 API Docs**: [https://tsp-solver-backend-production.up.railway.app/swagger-ui.html](https://tsp-solver-backend-production.up.railway.app/swagger-ui.html)
- **📥 Postman Collection**: [Download TSP_API_Complete.postman_collection.json](./TSP_API_Complete.postman_collection.json)
- **🔗 Backend API**: [https://tsp-solver-backend-production.up.railway.app](https://tsp-solver-backend-production.up.railway.app)

## Problem Statement

**Objective**: Minimize the total travel distance while ensuring that all delivery points are visited exactly once and return to the starting point.

**Requirements**:

1. **Input**: List of delivery points with coordinates (x, y)
2. **Output**: Shortest possible route and total travel distance
3. **Algorithm**: Core TSP algorithms implemented from scratch
4. **Performance**: Optimized for efficiency with larger datasets
5. **Testing**: Comprehensive unit tests included
6. **Documentation**: Clear explanations and deployment guidance

## Solution Overview

This solution implements a **multi-algorithm approach** with automatic selection based on problem size:

- **≤10 points**: Exact algorithms (Dynamic Programming with bit masking or Brute Force)
- **11-25 points**: Heuristic approach (Nearest Neighbor + 2-opt local search)
- **26+ points**: Metaheuristic approach (Simulated Annealing with multiple mutation operators)

## Architecture & Design Decisions

### Why Spring Boot?

- **Scalability**: REST API allows multiple clients and easy horizontal scaling
- **Production-Ready**: Built-in monitoring, health checks, and deployment features
- **Database Integration**: Persistent storage for solutions and batch processing
- **Testing**: Comprehensive testing framework with mocking capabilities

### Algorithm Selection Strategy

```java
if (pointCount <= 10) {  // tsp.algorithm.exact-max-points
    solver = new ExactTSPSolver();     // Guaranteed optimal solution
} else if (pointCount <= 25) {  // tsp.algorithm.heuristic-max-points
    solver = new HeuristicTSPSolver(); // Good balance of speed and quality
} else {
    solver = new MetaheuristicTSPSolver(); // Handles large datasets efficiently
}
```

These thresholds are configurable in `application.properties`:

```properties
# Algorithm Selection Thresholds
tsp.algorithm.exact-max-points=10
tsp.algorithm.heuristic-max-points=25
```

### Core Algorithms Implemented

#### 1. ExactTSPSolver (≤10 points)

- **Dynamic Programming**: O(n²2ⁿ) using bit masking for state representation
- **Brute Force**: O(n!) fallback for very small instances
- **Guarantee**: Mathematically optimal solution

#### 2. HeuristicTSPSolver (11-25 points)

- **Nearest Neighbor**: O(n²) greedy construction heuristic
- **2-opt Improvement**: O(n²) local search optimization
- **Quality**: Good approximate solutions with fast execution

#### 3. MetaheuristicTSPSolver (26+ points)

- **Simulated Annealing**: Probabilistic optimization with adaptive cooling
- **Multiple Mutations**: 2-opt, swap, and insertion operators
- **Scalability**: Handles large datasets with configurable time limits

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

#### Option 1: Use Live Demo

- **Live Frontend**: https://tsp-solver-frontend.vercel.app/
- **Live Backend API**: https://tsp-solver-backend-production.up.railway.app
- **Swagger API Docs**: https://tsp-solver-backend-production.up.railway.app/swagger-ui.html
- **Health Check**: https://tsp-solver-backend-production.up.railway.app/actuator/health

**Demo Instructions**: Visit the frontend URL above to instantly test the TSP solver with drag-and-drop file upload, interactive visualization, and real-time algorithm performance metrics.

#### Option 2: Run Locally

1. **Clone and navigate to the project**:
   
   ```bash
   cd tsp-solver-backend
   ```

2. **Start the application**:
   
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the application**:
   
   - API Base URL: `http://localhost:8080`
   - Swagger API Docs: `http://localhost:8080/swagger-ui.html`
   - Health Check: `http://localhost:8080/actuator/health`
   - H2 Database Console: `http://localhost:8080/h2-console`

### Input File Format

Create a `delivery_points.txt` file with coordinates (as specified in requirements):

```
0.0,0.0
3.0,4.0
6.0,0.0
3.0,-4.0
-3.0,2.0
```

### API Usage Examples

#### Live Demo Examples (Production)

```bash
# Upload delivery points to live backend
curl -X POST -F "file=@delivery_points.txt" https://tsp-solver-backend-production.up.railway.app/api/tsp/upload

# Solve TSP (automatic algorithm selection)  
curl -X POST https://tsp-solver-backend-production.up.railway.app/api/tsp/{solution-id}/solve

# Get optimal route
curl -X GET https://tsp-solver-backend-production.up.railway.app/api/tsp/{solution-id}

# Example request from live frontend
curl 'https://tsp-solver-backend-production.up.railway.app/api/tsp/c40e4488-1176-4915-93f1-da74390856de' \
  -H 'accept: application/json, text/plain, */*' \
  -H 'origin: https://tsp-solver-frontend.vercel.app'
```

#### Local Development Examples

```bash
# Upload delivery points to local backend
curl -X POST -F "file=@delivery_points.txt" http://localhost:8080/api/tsp/upload

# Solve TSP (automatic algorithm selection)
curl -X POST http://localhost:8080/api/tsp/{solution-id}/solve

# Get optimal route
curl -X GET http://localhost:8080/api/tsp/{solution-id}
```

## Performance Results

**Actual performance significantly exceeds requirements:**

| Problem Size | Algorithm           | Target Time | Actual Time | Performance Gain |
| ------------ | ------------------- | ----------- | ----------- | ---------------- |
| 5 points     | Exact (Brute Force) | <1s         | <1ms        | **1000x faster** |
| 15 points    | Heuristic (NN+2opt) | <5s         | 1-2ms       | **2500x faster** |
| 30 points    | Simulated Annealing | <30s        | 35-40ms     | **750x faster**  |

**Example Results:**

- **5 points**: Distance 15.15, Algorithm: BRUTE_FORCE, Time: 1ms
- **15 points**: Distance 91.14, Algorithm: NEAREST_NEIGHBOR_2OPT, Time: 2ms  
- **30 points**: Distance 201.50, Algorithm: SIMULATED_ANNEALING, Time: 141ms

## Testing

### Unit Tests

```bash
./mvnw test
```

**Coverage**: 9/9 tests passing (ExactTSPSolver: 3, Spring Boot: 1, TSPService: 5)

### Algorithm Verification

```bash
./mvnw test-compile exec:java -Dexec.mainClass="com.tsp.AlgorithmTest" -Dexec.classpathScope="test"
```

### API Integration Tests

```bash
./mvnw test-compile exec:java -Dexec.mainClass="com.tsp.ManualAPITest" -Dexec.classpathScope="test"
```

### Test Data Sets

The project includes organized test files in `txt/` directory:

- `exact_*.txt` - Small datasets (≤10 points)
- `heuristic_*.txt` - Medium datasets (11-25 points)  
- `metaheuristic_*.txt` - Large datasets (26+ points)

### Postman Testing

**📥 Files to Download**:
- **[TSP_API_Complete.postman_collection.json](./TSP_API_Complete.postman_collection.json)** - API collection with all requests and tests
- **[TSP_API_Environments.postman_environment.json](./TSP_API_Environments.postman_environment.json)** - Environment settings for both local and production testing

**Quick Setup**:

1. **Import Files**: In Postman → File → Import → Select both downloaded files
2. **Configure Environment**: 
   - After importing, select "TSP API Environments" from the environment dropdown in the top right
   - By default, it's configured for production testing (Railway)
   - To test locally: Click the eye icon next to the environment dropdown → Edit → Change `currentUrl` from `{{railwayUrl}}` to `{{localUrl}}`
3. **Run**: Execute entire collection (14 tests) or individual requests

**✅ Verified Results**: All 14 tests pass against Railway production with 50 assertions covering:

- Health checks, file uploads, algorithm selection, TSP solving, error handling
- Performance verification: Average response time 220ms
- All algorithms tested: BRUTE_FORCE (≤10), NEAREST_NEIGHBOR_2OPT (11-25), SIMULATED_ANNEALING (26+)

## Deployment Strategy

### 1. Local Development

```bash
./mvnw spring-boot:run
```

### 2. Production JAR Deployment

```bash
./mvnw clean package
java -jar target/tsp-solver-1.0.0.jar
```

### 3. Docker Deployment

```bash
docker build -t tsp-solver .
docker run -p 8080:8080 tsp-solver
```

### 4. Railway/Cloud Deployment

- **Environment Variables**: `PORT` (auto-configured)
- **Database**: H2 in-memory (production would use PostgreSQL)
- **Health Check**: `/actuator/health` endpoint
- **Zero-downtime**: Spring Boot's graceful shutdown

### Change Deployment Process

#### Development → Production Pipeline

1. **Local Development**:
   
   ```bash
   # Make changes, run tests
   ./mvnw test
   git add . && git commit -m "feature: description"
   ```

2. **CI/CD Pipeline** (GitHub Actions example):
   
   ```yaml
   - name: Run Tests
     run: ./mvnw test
   - name: Build JAR
     run: ./mvnw clean package
   - name: Deploy to Railway
     run: railway up
   ```

3. **Production Deployment**:
   
   - **Blue-Green Deployment**: Deploy to staging, health check, switch traffic
   - **Database Migrations**: Use Flyway for schema changes
   - **Rollback Strategy**: Keep previous JAR version for quick rollback

## Monitoring Plan

### 1. Application Health Monitoring

```bash
# Health endpoint (built-in)
GET /actuator/health

# Custom metrics
GET /actuator/metrics
```

### 2. Performance Monitoring

- **Response Time**: Track API endpoint latency
- **Algorithm Performance**: Monitor execution time per algorithm type
- **Throughput**: Track requests per second and concurrent processing
- **Memory Usage**: Monitor JVM heap and algorithm memory consumption

### 3. Business Logic Monitoring

```java
// Custom metrics examples
@Component
public class TSPMetrics {
    private final MeterRegistry meterRegistry;

    public void recordSolutionTime(String algorithm, long timeMs) {
        Timer.Sample sample = Timer.start(meterRegistry);
        sample.stop(Timer.builder("tsp.solution.time")
            .tag("algorithm", algorithm)
            .register(meterRegistry));
    }

    public void recordSolutionQuality(double distance, int pointCount) {
        Gauge.builder("tsp.solution.distance")
            .tag("point_count", String.valueOf(pointCount))
            .register(meterRegistry, () -> distance);
    }
}
```

### 4. Alerting Strategy

- **Response Time**: Alert if >95th percentile exceeds 1s
- **Error Rate**: Alert if error rate >1%
- **Algorithm Failures**: Alert on any algorithm timeout or exception
- **Memory**: Alert if heap usage >80%

### 5. Logging & Observability

```java
// Structured logging
log.info("TSP solution completed", 
    kv("algorithm", result.getAlgorithm()),
    kv("points", solution.getPointCount()),
    kv("distance", result.getTotalDistance()),
    kv("executionTimeMs", result.getExecutionTimeMs())
);
```

### 6. Monitoring Tools Integration

- **Prometheus + Grafana**: Metrics collection and visualization
- **ELK Stack**: Log aggregation and analysis  
- **PagerDuty**: Alert management and incident response

### 7. SLA Monitoring

- **Availability**: 99.9% uptime target
- **Performance**: 
  - Exact solver: <100ms for ≤10 points
  - Heuristic solver: <1s for ≤25 points  
  - Metaheuristic solver: <30s for any size
- **Accuracy**: Track solution quality vs known optimal solutions

## API Documentation

### Interactive Documentation (Swagger)

- **Production**: [https://tsp-solver-backend-production.up.railway.app/swagger-ui.html](https://tsp-solver-backend-production.up.railway.app/swagger-ui.html)
- **Local**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) (when running locally)

The Swagger UI provides interactive API documentation where you can test all endpoints directly in your browser.

## REST API Reference

### Core Endpoints

| Method   | Endpoint              | Description                                  |
| -------- | --------------------- | -------------------------------------------- |
| `POST`   | `/api/tsp/upload`     | Upload delivery points file                  |
| `POST`   | `/api/tsp/{id}/solve` | Solve TSP with automatic algorithm selection |
| `GET`    | `/api/tsp/{id}`       | Get solution by ID                           |
| `GET`    | `/api/tsp`            | Get all solutions                            |
| `DELETE` | `/api/tsp/{id}`       | Delete solution                              |

### Request/Response Examples

#### Upload Response

```json
{
    "id": "uuid-generated-id",
    "fileName": "delivery_points.txt",
    "originalPoints": [{"x": 0.0, "y": 0.0}, ...],
    "status": "UPLOADED",
    "pointCount": 5,
    "createdAt": "2025-06-30T02:02:42.613764"
}
```

#### Solution Response

```json
{
    "id": "uuid-generated-id",
    "route": [
        {"x": 0.0, "y": 0.0, "order": 0, "segmentDistance": 3.16},
        {"x": 1.0, "y": 3.0, "order": 1, "segmentDistance": 3.0}
    ],
    "totalDistance": 15.15,
    "algorithm": "BRUTE_FORCE",
    "status": "SOLVED",
    "executionTimeMs": 1
}
```

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.0, Maven
- **Database**: H2 (in-memory), JPA/Hibernate
- **Testing**: JUnit 5, Mockito, Newman/Postman
- **Deployment**: Docker, Railway-ready
- **Monitoring**: Spring Actuator, Micrometer

## Key Implementation Files

- **Algorithms**: `src/main/java/com/tsp/solver/`
  - `ExactTSPSolver.java` - Dynamic Programming and Brute Force
  - `HeuristicTSPSolver.java` - Nearest Neighbor + 2-opt  
  - `MetaheuristicTSPSolver.java` - Simulated Annealing
- **Service Layer**: `src/main/java/com/tsp/service/TSPService.java`
- **REST API**: `src/main/java/com/tsp/controller/TSPController.java`
- **Data Models**: `src/main/java/com/tsp/model/`
- **Tests**: `src/test/java/com/tsp/`

## Future Enhancements

1. **Algorithm Improvements**:
   
   - Machine Learning-based heuristics

2. **Scalability**:
   
   - Async processing for large datasets
   - Distributed computing with multiple nodes
   - Caching frequently solved problem patterns
   - Multi-threading

3. **Features**:
   
   - Real-world distance calculations (Google Maps API)
   - Batch processing for multiple TSP instances
   - Custom algorithm parameter tuning

---