package com.tsp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UploadAddressesRequest {
    
    @NotEmpty(message = "Addresses list cannot be empty")
    @Size(min = 3, max = 100, message = "Must have between 3 and 100 addresses")
    private List<String> addresses;
    
    private String mode = "DEMO";
    
    public List<String> getAddresses() {
        return addresses;
    }
    
    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }
    
    public String getMode() {
        return mode;
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }
}