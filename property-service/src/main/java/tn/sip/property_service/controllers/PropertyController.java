package tn.sip.property_service.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tn.sip.property_service.dtos.PropertyDTO;
import tn.sip.property_service.entities.Property;
import tn.sip.property_service.enums.PropertyStatus;
import tn.sip.property_service.services.PropertyService;



@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "http://localhost:4200") 
public class PropertyController {
	 private final PropertyService propertyService;

	    public PropertyController(PropertyService propertyService) {
	        this.propertyService = propertyService;
	    }
	    
	 @GetMapping
	    public List<Property> getAllProperties() {
	        return propertyService.getAllProperties();
	    }

	    @GetMapping("/{id}")
	    public Property getPropertyById(@PathVariable Long id) {
	        return propertyService.getPropertyById(id);
	    }
	    
	    @PostMapping("/add")
	    public ResponseEntity<Map<String, String>> addProperty(@RequestBody PropertyDTO propertyDto) {
	    	System.out.print(propertyDto);
	        Property property = new Property();
	        property.setFiles(propertyDto.getFiles());
	        property.setImages(propertyDto.getImages());
	        property.setBathNumb(propertyDto.getBathNumb());
	        property.setDescription(propertyDto.getDescription());
	        property.setPiecesNumb(propertyDto.getPiecesNumb());
	        property.setKitchenNumb(propertyDto.getKitchenNumb());
	        property.setLocation(propertyDto.getLocation());
	        property.setPrice(propertyDto.getPrice());
	        property.setPropertyStatus(PropertyStatus.AVAILABLE);
	        property.setTitle(propertyDto.getTitle());
	        property.setType(propertyDto.getType());
	        propertyService.addProperty(property);

	        Map<String, String> response = new HashMap<>();
	        response.put("message", "Propriété ajoutée avec succès");
	        return ResponseEntity.ok(response);
	    }
    
    @PutMapping("/{id}")
    public Property updateProperty(@PathVariable Long id, @RequestBody Property property) {
        return propertyService.updateProperty(id, property);
    }

    @DeleteMapping("/{id}")
    public void deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
    }
    

    
}

