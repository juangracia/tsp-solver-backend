package com.tsp.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.tsp.model.RoutePoint;

import java.io.IOException;

/**
 * Custom JSON serializer for RoutePoint to ensure both Point properties (x, y)
 * and RoutePoint properties (order) are properly included in the JSON output.
 */
public class RoutePointSerializer extends JsonSerializer<RoutePoint> {

    @Override
    public void serialize(RoutePoint routePoint, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        
        // Write Point properties
        jsonGenerator.writeNumberField("x", routePoint.getX() != null ? routePoint.getX() : 0);
        jsonGenerator.writeNumberField("y", routePoint.getY() != null ? routePoint.getY() : 0);
        
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
