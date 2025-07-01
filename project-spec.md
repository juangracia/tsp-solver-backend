## Sample Test Data Files

### small_test.txt (5 points)

```
0,0
3,4
6,0
3,-4
-3,2
```

Expected optimal distance: ~16.97

### medium_test.txt (15 points)

```
0,0
10,10
20,5
15,15
5,20
25,25
30,10
12,8
18,22
7,15
22,18
28,5
9,25
16,3
4,12
```

### large_test.txt (50+ points)

Generate programmatically with known patterns

## Google# TSP (Travelling Salesman Problem) Project Specifications

## Project Overview

Build a full-stack application that solves the Travelling Salesman Problem with different algorithms based on input size, featuring a Spring Boot REST API backend and React frontend with visualization.

## Backend Specification (Spring Boot)

### Core Requirements

- **Language**: Java with Spring Boot 3.x
- **Build Tool**: Maven
- **Database**: H2 (in-memory for demo)
- **Deployment**: Railway-ready configuration

### Algorithm Selection Strategy

```
Points <= 10: Exact algorithm (Brute Force or Dynamic Programming)
Points 11-25: Heuristic (Nearest Neighbor + 2-opt improvement)
Points 26-100: Advanced heuristic (Simulated Annealing)
Points > 100: Genetic Algorithm or Ant Colony Optimization
```

### REST API Endpoints

#### 1. File Upload Endpoint

```
POST /api/tsp/upload
Content-Type: multipart/form-data
Body: delivery_points.txt file

Response:
{
  "id": "uuid",
  "pointCount": 15,
  "status": "UPLOADED",
  "fileName": "delivery_points.txt"
}
```

#### 2. Solve TSP Endpoint

```
POST /api/tsp/{id}/solve
Query params: 
  - algorithm (optional): "auto", "exact", "heuristic", "genetic"
  - maxTime (optional): maximum solving time in seconds

Response:
{
  "id": "uuid",
  "status": "SOLVED",
  "algorithm": "NEAREST_NEIGHBOR_2OPT",
  "totalDistance": 245.67,
  "executionTimeMs": 1250,
  "route": [
    {
      "x": 0, "y": 0, "order": 0,
    },
    {
      "x": 10, "y": 5, "order": 1
    }
  ],
  "originalPoints": [
    {"x": 0, "y": 0},
    {"x": 10, "y": 5}
  ],
}
```

#### 3. Get Solution Status

```
GET /api/tsp/{id}
Response: Same as solve endpoint
```

#### 4. Get All Solutions

```
GET /api/tsp
Response:
{
  "solutions": [
    {
      "id": "uuid",
      "fileName": "delivery_points.txt",
      "pointCount": 15,
      "status": "SOLVED",
      "totalDistance": 245.67,
      "algorithm": "NEAREST_NEIGHBOR_2OPT",
      "createdAt": "2025-06-28T10:30:00Z"
    }
  ]
}
```

### Data Models

#### TSPSolution Entity

```java
@Entity
public class TSPSolution {
    private String id;
    private String fileName;
    private List<Point> originalPoints;
    private List<RoutePoint> route;
    private Double totalDistance;
    private String algorithm;
    private SolutionStatus status;
    private Long executionTimeMs;
    private LocalDateTime createdAt;
}
```

#### Point Model

```java
public class Point {
    private Double x;
    private Double y;
}

```

#### RoutePoint Model

```java
public class RoutePoint extends Point {
    private Integer order;
}
```

### When to Enable Real-World Mode

- **Primary Purpose**: Showcase practical application of TSP algorithms
- **Interview Benefit**: Demonstrates real-world problem-solving thinking
- **Technical Showcase**: Integration skills with external APIs
- **Business Value**: Shows understanding of practical constraints vs theoretical optimization

### Feature Toggle Strategy

```typescript
interface AppConfig {
  showRealWorldToggle: boolean;
}

// App can work in three states:
```

### Implementation Priority

1. **Phase 1**: Complete abstract TSP implementation (core algorithms)
2. **Phase 2**: Add Google Maps integration as enhancement
3. **Phase 3**: Polish UI for seamless mode switching

### Fallback Strategy

- Clear messaging about feature availability


1. **ExactTSPSolver** (for ≤10 points)
   
   - Dynamic Programming with bitmask
   - Fallback to brute force for ≤8 points

2. **HeuristicTSPSolver** (for 11-25 points)
   
   - Nearest Neighbor construction
   - 2-opt local improvement

3. **MetaheuristicTSPSolver** (for 26+ points)
   
   - Simulated Annealing or Genetic Algorithm
   - Configurable parameters

### Performance Requirements

- Solutions < 10 points: < 1 second
- Solutions 11-25 points: < 5 seconds
- Solutions 26-100 points: < 30 seconds
- Solutions > 100 points: < 2 minutes

### Configuration

```properties
# application.properties
server.port=${PORT:8080}
spring.servlet.multipart.max-file-size=10MB
tsp.max-points=1000
tsp.default-timeout-seconds=120

# Demo mode - can work without Google Maps
demo.real.world.enabled=${REAL_WORLD_DEMO:false}
```

### CORS Configuration

```java
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://*.vercel.app"
})
```

## Frontend Specification (React)

### Core Requirements

- **Framework**: React 18 with TypeScript
- **Styling**: Tailwind CSS
- **Charts**: Recharts or D3.js for visualization
- **HTTP Client**: Axios
- **Deployment**: Vercel

### Main Components

#### 1. FileUploadComponent

- **Abstract Mode**: Drag & drop interface for delivery_points.txt
- File validation (format, size, max points)
- Upload progress indicator


#### 2. TSPVisualization

- **Abstract Mode**: 2D canvas/SVG showing coordinate points and routes
- Color-coded points (start=green, others=blue, route=red line)
- Zoom and pan functionality
- Route animation (optional)
- Mathematical grid, Euclidean distances

#### 3. SolutionDetails

- Algorithm used and why it was selected
- Euclidean distance units
- Execution time and performance metrics
- Point count

#### 4. SolutionsHistory

- List of previous solutions
- Filter by algorithm, date, point count
- Delete functionality

#### 5. AlgorithmSelector (Advanced)

- Manual algorithm selection override
- Algorithm comparison mode
- Performance parameters tuning

### File Format Validation

```typescript
// Expected format in delivery_points.txt:
// 0,0
// 10,5
// 15,12
// 8,20

interface Point {
  x: number;
  y: number;
}

const validateFile = (content: string): Point[] => {
  // Validate CSV format
  // Check coordinate ranges
  // Ensure minimum 3 points
  // Maximum 1000 points
}
```

### API Integration

```typescript
const API_BASE_URL = process.env.NODE_ENV === 'production' 
  ? 'https://tsp-backend.railway.app'
  : 'http://localhost:8080';

class TSPService {
  async uploadFile(file: File): Promise<TSPSolution>
  async solveTSP(id: string, options?: SolveOptions): Promise<TSPSolution>
  async getSolution(id: string): Promise<TSPSolution>
  async getAllSolutions(): Promise<TSPSolution[]>

  async getDistanceMatrix(origins: Coordinates[], destinations: Coordinates[]): Promise<DistanceMatrix>
}

interface SolveOptions {
  algorithm?: string;
  maxTime?: number;
}
```

### UI/UX Requirements

- Responsive design (mobile-friendly)
- Loading states for all async operations
- Error handling with user-friendly messages
- Progress indicators for long-running solutions
- Results shareable via URL

## Testing Specifications

### Backend Unit Tests

```java
@Test
class TSPSolverTests {
    // Test each algorithm with known optimal solutions
    // Test algorithm selection logic
    // Test file parsing
    // Test performance within time limits
    // Test edge cases (2 points, duplicate points, etc.)
}
```

### Frontend Unit Tests

```typescript
// Component rendering tests
// File upload validation
// API integration mocks
// Route visualization accuracy
// User interaction flows
```

### Integration Tests

- End-to-end file upload → solve → display flow
- Algorithm performance benchmarks
- CORS functionality

## Sample Test Data Files

### small_test.txt (5 points)

```
0,0
3,4
6,0
3,-4
-3,2
```

Expected optimal distance: ~16.97

### medium_test.txt (15 points)

```
0,0
10,10
20,5
15,15
5,20
25,25
30,10
12,8
18,22
7,15
22,18
28,5
9,25
16,3
4,12
```

### large_test.txt (50+ points)

Generate programmatically with known patterns

## Deployment Specifications

### Backend (Railway)

- Dockerfile for consistent deployment
- Environment variable configuration
- Health check endpoint: `GET /actuator/health`
- Logging configuration for monitoring

### Frontend (Vercel)

- Build optimization for production
- Environment variables for API URL
- Static asset optimization

### Monitoring Plan (Hypothetical)

- **Performance**: Algorithm execution times, memory usage
- **Business**: Solution success rate, popular algorithms
- **Infrastructure**: API response times, error rates
- **User**: File upload success, visualization performance

## Documentation Requirements

### README.md Structure

1. Project overview and problem statement
2. Architecture decisions (why Spring Boot + React)
3. Algorithm selection rationale
4. Setup and running instructions
5. API documentation
6. Performance benchmarks
7. Deployment strategy
8. Future improvements

### Code Documentation

- Javadoc for all public methods
- TSDoc for TypeScript interfaces
- Algorithm complexity analysis
- Decision rationale comments

## Success Criteria

- [ ] Handles files up to 1000 points
- [ ] Correct algorithm selection based on input size
- [ ] Visual route representation
- [ ] Sub-second response for small problems
- [ ] Professional UI/UX
- [ ] Deployed and accessible via public URLs
- [ ] Comprehensive test coverage
- [ ] Clear documentation