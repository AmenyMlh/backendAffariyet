package tn.sip.property_service.dtos;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import tn.sip.property_service.enums.PropertyStatus;
import tn.sip.property_service.enums.PropertyType;

@Data
public class PropertyDTO {
	
	    private String title;

	    private String description;

	    private PropertyType type;

	    private List<String> images;
	    
	    private List<String> files;

	    private Double price;

	    private String location;

	    private PropertyStatus propertyStatus;
	    
	    private int piecesNumb;
	    
	    private int bathNumb;
	    
	    private int kitchenNumb;
}
