package com.tsp.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Coordinates {
    private Double lat;
    private Double lng;

    public Coordinates() {}

    public Coordinates(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinates that = (Coordinates) obj;
        return Double.compare(that.lat, lat) == 0 && Double.compare(that.lng, lng) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(lat, lng);
    }

    @Override
    public String toString() {
        return String.format("Coordinates(%.6f, %.6f)", lat, lng);
    }
}