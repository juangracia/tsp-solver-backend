# TSP API Postman Testing Guide

## 📦 Collection Overview

This Postman collection provides comprehensive testing for the TSP Solver API with all endpoints, test cases, and automated validations.

## 🚀 Quick Setup

### 1. Import Files
Import these files into Postman:
- `TSP_API.postman_collection.json` - Main collection
- `TSP_API.postman_environment.json` - Environment variables

### 2. Prepare Test Files
The collection requires these test files in the `postman_test_files/` directory:
- `small_test.txt` - 5 coordinate points (for exact algorithms)
- `medium_test.txt` - 15 coordinate points (for heuristic algorithms)
- `invalid_test.txt` - Invalid data (for error testing)

### 3. Start the Backend
```bash
cd tsp-solver-backend
mvn spring-boot:run
```

### 4. Run the Collection
Execute requests in order or run the entire collection with the Collection Runner.

## 📋 Test Scenarios

### Core Functionality Tests

#### 1. **Health Check**
- **Endpoint**: `GET /actuator/health`
- **Purpose**: Verify the application is running
- **Expected**: 200 OK with health status

#### 2. **File Upload Tests**
- **Small File (5 points)**: Tests exact algorithm selection
- **Medium File (15 points)**: Tests heuristic algorithm selection
- **Invalid File**: Tests error handling


#### 4. **TSP Solving Tests**
- **Auto Algorithm Selection**: System chooses best algorithm
- **Forced Algorithm**: Manual algorithm override

#### 5. **Solution Retrieval**
- **Get by ID**: Retrieve specific solution
- **Get All**: List all solutions
- **Delete**: Remove solutions

#### 6. **Error Handling**
- **404 Tests**: Non-existent solution IDs
- **400 Tests**: Invalid file uploads

## 🧪 Automated Test Validations

Each request includes automated tests that verify:

### Algorithm Selection Tests
```javascript
pm.test('Algorithm is exact (BRUTE_FORCE or DYNAMIC_PROGRAMMING)', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.algorithm).to.match(/BRUTE_FORCE|DYNAMIC_PROGRAMMING/);
});
```

### Performance Tests
```javascript
pm.test('Execution time is fast (< 1000ms)', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.executionTimeMs).to.be.below(1000);
});
```

### Data Integrity Tests
```javascript
pm.test('Has route with correct number of points', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.route).to.have.lengthOf(5);
});
```

## 🎯 Test Flow

### Recommended Testing Order

1. **Health Check** - Verify server is running
2. **Upload Small File** - Test exact algorithm path
3. **Upload Medium File** - Test heuristic algorithm path
4. **Solve Small TSP** - Test exact solving
5. **Solve Medium TSP** - Test heuristic solving
6. **Solve with Forced Algorithm** - Test algorithm override
7. **Get Solution by ID** - Test retrieval
8. **Get All Solutions** - Test listing
9. **Delete Solution** - Test cleanup
10. **Error Tests** - Test invalid inputs

### Environment Variables

The collection automatically manages these variables:
- `smallFileId` - ID from small file upload
- `mediumFileId` - ID from medium file upload

## 📊 Expected Results

### Algorithm Performance Expectations

| Test Case | Points | Expected Algorithm | Time Limit | Distance Quality |
|-----------|--------|-------------------|------------|------------------|
| Small File | 5 | BRUTE_FORCE/DYNAMIC_PROGRAMMING | <1s | Optimal |
| Medium File | 15 | NEAREST_NEIGHBOR_2OPT | <5s | Good Heuristic |
| Large Dataset | 30+ | SIMULATED_ANNEALING | <30s | Meta-heuristic |

### Response Structure Examples

**Upload Response:**
```json
{
  "id": "uuid-string",
  "pointCount": 5,
  "status": "UPLOADED",
  "fileName": "small_test.txt"
}
```

**Solve Response:**
```json
{
  "id": "uuid-string",
  "status": "SOLVED",
  "algorithm": "BRUTE_FORCE",
  "totalDistance": 24.93,
  "executionTimeMs": 1,
  "route": [
    {"x": 0, "y": 0, "order": 0},
    {"x": 3, "y": 4, "order": 1}
  ],
  "originalPoints": [
    {"x": 0, "y": 0},
    {"x": 3, "y": 4}
  ]
}
```

## 🔧 Configuration Options

### Environment Setup
Switch between environments by changing `baseUrl`:
- **Local**: `http://localhost:8080`
- **Production**: `https://your-app.railway.app`


## 🐛 Troubleshooting

### Common Issues

**❌ Connection Refused**
- Ensure Spring Boot app is running: `mvn spring-boot:run`
- Check port 8080 is available

**❌ File Upload Fails**
- Verify test files exist in `postman_test_files/` directory
- Check file format matches expected CSV coordinates

**❌ Algorithm Selection Wrong**
- Small files (≤10 points) should use exact algorithms
- Medium files (11-25 points) should use heuristic
- Large files (26+ points) should use metaheuristic

**❌ Tests Failing**
- Check response status codes first
- Verify JSON structure matches expected format
- Ensure environment variables are set correctly

## 📈 Collection Runner

For automated testing:
1. Select "TSP API" collection
2. Click "Run" 
3. Select "TSP API Environment"
4. Set iterations to 1
5. Enable "Stop on first test failure"
6. Click "Run TSP API"

The runner will execute all requests in sequence and provide a comprehensive test report.

## 🎉 Success Criteria

A successful test run should show:
- ✅ All health checks pass
- ✅ File uploads return valid solution IDs
- ✅ Algorithm selection matches problem size
- ✅ Solving completes within time limits
- ✅ Solutions have valid routes and distances
- ✅ Error cases return appropriate status codes

This collection provides complete coverage of the TSP API functionality and validates all requirements from the project specification.