package com.tsp.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.tsp.model.Coordinates;
import com.tsp.model.TSPSolution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleMapsService {
    
    @Value("${google.maps.api.key:}")
    private String apiKey;
    
    @Value("${google.maps.api.enabled:false}")
    private boolean enabled;
    
    private GeoApiContext context;
    
    public boolean isEnabled() {
        return enabled && apiKey != null && !apiKey.isEmpty();
    }
    
    private GeoApiContext getContext() {
        if (context == null && isEnabled()) {
            context = new GeoApiContext.Builder()
                    .apiKey(apiKey)
                    .build();
        }
        return context;
    }
    
    public List<TSPSolution.AddressInfo> geocodeAddresses(List<String> addresses) {
        List<TSPSolution.AddressInfo> results = new ArrayList<>();
        
        if (!isEnabled()) {
            for (String address : addresses) {
                results.add(new TSPSolution.AddressInfo(address, null, null));
            }
            return results;
        }
        
        try {
            GeoApiContext ctx = getContext();
            for (String address : addresses) {
                try {
                    GeocodingResult[] geocodingResults = GeocodingApi.geocode(ctx, address).await();
                    
                    if (geocodingResults.length > 0) {
                        LatLng location = geocodingResults[0].geometry.location;
                        Coordinates coordinates = new Coordinates(location.lat, location.lng);
                        String placeId = geocodingResults[0].placeId;
                        
                        results.add(new TSPSolution.AddressInfo(address, coordinates, placeId));
                    } else {
                        results.add(new TSPSolution.AddressInfo(address, null, null));
                    }
                } catch (Exception e) {
                    results.add(new TSPSolution.AddressInfo(address, null, null));
                }
            }
        } catch (Exception e) {
            for (String address : addresses) {
                results.add(new TSPSolution.AddressInfo(address, null, null));
            }
        }
        
        return results;
    }
    
    public String generateMapsUrl(List<TSPSolution.AddressInfo> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }
        
        StringBuilder url = new StringBuilder("https://www.google.com/maps/dir/");
        
        for (int i = 0; i < addresses.size(); i++) {
            TSPSolution.AddressInfo addressInfo = addresses.get(i);
            if (addressInfo.getCoordinates() != null) {
                url.append(addressInfo.getCoordinates().getLat())
                   .append(",")
                   .append(addressInfo.getCoordinates().getLng());
            } else {
                url.append(addressInfo.getAddress().replace(" ", "+"));
            }
            
            if (i < addresses.size() - 1) {
                url.append("/");
            }
        }
        
        if (addresses.size() > 1 && addresses.get(0).getCoordinates() != null) {
            TSPSolution.AddressInfo first = addresses.get(0);
            url.append("/").append(first.getCoordinates().getLat())
               .append(",").append(first.getCoordinates().getLng());
        }
        
        return url.toString();
    }
    
    public double calculateRealDistance(Coordinates from, Coordinates to) {
        if (!isEnabled() || from == null || to == null) {
            return haversineDistance(from, to);
        }
        
        return haversineDistance(from, to);
    }
    
    private double haversineDistance(Coordinates from, Coordinates to) {
        if (from == null || to == null) {
            return 0.0;
        }
        
        final double R = 6371.0;
        
        double lat1Rad = Math.toRadians(from.getLat());
        double lon1Rad = Math.toRadians(from.getLng());
        double lat2Rad = Math.toRadians(to.getLat());
        double lon2Rad = Math.toRadians(to.getLng());
        
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}