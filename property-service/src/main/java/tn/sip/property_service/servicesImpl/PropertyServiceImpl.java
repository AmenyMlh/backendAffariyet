package tn.sip.property_service.servicesImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tn.sip.property_service.entities.Property;
import tn.sip.property_service.repositories.PropertyRepository;
import tn.sip.property_service.services.PropertyService;

@Service
public class PropertyServiceImpl implements PropertyService {
	private final PropertyRepository propertyRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }


    @Override
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id).orElseThrow(() -> new RuntimeException("Property not found"));
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
        return propertyRepository.save(existingProperty);
    }

    @Override
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }


}
