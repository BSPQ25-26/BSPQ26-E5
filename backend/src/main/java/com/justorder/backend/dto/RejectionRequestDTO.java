package com.justorder.backend.dto;

public class RejectionRequestDTO {
    
    private String reason;
    
    public RejectionRequestDTO() {
    }

    public String getReason() { 
        return reason; 
    }
    
    public void setReason(String reason) { 
        this.reason = reason; 
    }
}