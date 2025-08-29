package tn.sip.property_service.services;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import tn.sip.property_service.entities.Property;
import tn.sip.property_service.enums.PropertyStatus;

public interface PropertyService {
	
	Property addProperty(Property property);
	
	Property updateProperty(Long id, Property property);
	
	List<Property> getAllProperties();
	
	Property getPropertyById(Long id);

	List<Property> getPropertiesByAgencyId(Long agencyId);
	
	void deleteProperty(Long id);

    long countPropertiesByStatusAndAgencyId(PropertyStatus status, Long agencyId);

	long countPropertiesByLocation(String location, Long agencyId);

    Long getAgencyIdByProperty(Long propertyId);

    Long getTotalProperties(Long agencyId);

	Map<String, Long> getPropertiesByType(Long agencyId);

	Map<String, Long> getPropertiesByStatus(Long agencyId);
}
