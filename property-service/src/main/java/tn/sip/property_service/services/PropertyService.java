package tn.sip.property_service.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import tn.sip.property_service.entities.Property;

public interface PropertyService {
	
	Property addProperty(Property property);
	
	Property updateProperty(Long id, Property property);
	
	List<Property> getAllProperties();
	
	Property getPropertyById(Long id);
	
	void deleteProperty(Long id);

}
