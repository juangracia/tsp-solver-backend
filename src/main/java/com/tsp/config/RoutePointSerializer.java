package com.tsp.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.tsp.model.RoutePoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Custom JSON serializer for RoutePoint to ensure both Point properties (x, y)
 * and RoutePoint properties (order) are properly included in the JSON output.
 */
public class RoutePointSerializer extends StdSerializer<RoutePoint> {
    
    private static final Logger logger = LoggerFactory.getLogger(RoutePointSerializer.class);
    
    public RoutePointSerializer() {
        super(RoutePoint.class);
    }

    @Override
    public void serialize(RoutePoint routePoint, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        
        // Log route point details for debugging
        logger.debug("Serializing RoutePoint: x={}, y={}, coordinates={}, order={}", 
            routePoint.getX(), routePoint.getY(),
            (routePoint.getCoordinates() != null ? 
                "lat=" + routePoint.getCoordinates().getLat() + ", lng=" + routePoint.getCoordinates().getLng() : "null"),
            routePoint.getOrder());
        
        // Always use the x and y values directly
        // This ensures we're using the values set in the solver
        double x = routePoint.getX() != null ? routePoint.getX() : 0.0;
        double y = routePoint.getY() != null ? routePoint.getY() : 0.0;
        
        // If we have coordinates, use those instead
        if (routePoint.getCoordinates() != null) {
            x = routePoint.getCoordinates().getLng();
            y = routePoint.getCoordinates().getLat();
            logger.debug("Using coordinates for serialization: lat={}, lng={}", y, x);
        } else {
            logger.debug("Using x/y values: x={}, y={}", x, y);
        }
        
        jsonGenerator.writeNumberField("x", x);
        jsonGenerator.writeNumberField("y", y);
        
        // Write RoutePoint specific properties
        jsonGenerator.writeNumberField("order", routePoint.getOrder() != null ? routePoint.getOrder() : 0);
        
        // Write address if available
        if (routePoint.getAddress() != null) {
            jsonGenerator.writeStringField("address", routePoint.getAddress());
        }
        
        // Write coordinates if available
        if (routePoint.getCoordinates() != null) {
            jsonGenerator.writeObjectFieldStart("coordinates");
            jsonGenerator.writeNumberField("lat", routePoint.getCoordinates().getLat() != null ? routePoint.getCoordinates().getLat() : 0);
            jsonGenerator.writeNumberField("lng", routePoint.getCoordinates().getLng() != null ? routePoint.getCoordinates().getLng() : 0);
            jsonGenerator.writeEndObject();
        }
        
        jsonGenerator.writeEndObject();
    }
}
