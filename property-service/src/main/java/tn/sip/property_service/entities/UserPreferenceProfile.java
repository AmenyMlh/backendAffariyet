package tn.sip.property_service.entities;

import lombok.Data;
import tn.sip.property_service.enums.PropertyType;

@Data
public class UserPreferenceProfile {
    private String mostCommonLocation;
    private PropertyType mostLikedType;
    private Double avgPrice;
    private Double avgArea;
    private Integer avgPieces;
}
