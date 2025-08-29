package tn.sip.property_service.servicesImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import tn.sip.property_service.entities.Property;
import tn.sip.property_service.enums.PropertyStatus;
import tn.sip.property_service.enums.PropertyType;
import tn.sip.property_service.repositories.PropertyRepository;
import tn.sip.property_service.services.PropertyService;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {
	private final PropertyRepository propertyRepository;

    @Override
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id).orElseThrow(() -> new RuntimeException("Property not found"));
    }

    @Override
    public List<Property> getPropertiesByAgencyId(Long agencyId) {
        return propertyRepository.findPropertiesByAgencyId(agencyId);
    }

    @Override
    public Property addProperty(Property property) {

        if (property.getPrice() < 0) {
            throw new IllegalArgumentException("Le prix de la propriété ne peut pas être négatif.");
        }

        
        return propertyRepository.save(property);
    }

    @Override
    public Property updateProperty(Long id, Property property) {
        Property existingProperty = getPropertyById(id);
        existingProperty.setTitle(property.getTitle());
        existingProperty.setDescription(property.getDescription());
        existingProperty.setType(property.getType());
        existingProperty.setPrice(property.getPrice());
        existingProperty.setLocation(property.getLocation());
        existingProperty.setPropertyStatus(property.getPropertyStatus());
        existingProperty.setImages(property.getImages());
        existingProperty.setFiles(property.getFiles());
        existingProperty.setBathNumb(property.getBathNumb());
        existingProperty.setPiecesNumb(property.getPiecesNumb());
        existingProperty.setKitchenNumb(property.getKitchenNumb());
        existingProperty.setNbrEtage(property.getNbrEtage());
        existingProperty.setArea(property.getArea());

        return propertyRepository.save(existingProperty);
    }

    @Override
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }

    @Override
    public long countPropertiesByStatusAndAgencyId(PropertyStatus status, Long agencyId) {
        return propertyRepository.countByPropertyStatusAndAgencyId(status, agencyId);
    }


    @Override
    public long countPropertiesByLocation(String location, Long agencyId) {
        return propertyRepository.countPropertiesByLocationAndAgencyId(location,agencyId);
    }

    @Override
    public Long getAgencyIdByProperty(Long propertyId) {
        return propertyRepository.getAgencyIdByPropertyId(propertyId);
    }

    @Override
    public Long getTotalProperties(Long agencyId) {
        return propertyRepository.countPropertiesByAgency(agencyId);
    }

    @Override
    public Map<String, Long> getPropertiesByType(Long agencyId) {
        List<Object[]> results = propertyRepository.countPropertiesByType(agencyId);
        return results.stream().collect(Collectors.toMap(
                r -> ((PropertyType) r[0]).name(),
                r -> (Long) r[1]
        ));
    }

    @Override
    public Map<String, Long> getPropertiesByStatus(Long agencyId) {
        List<Object[]> results = propertyRepository.countPropertiesByStatus(agencyId);
        return results.stream().collect(Collectors.toMap(
                r -> ((PropertyStatus) r[0]).name(),
                r -> (Long) r[1]
        ));
    }
}
