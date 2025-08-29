package tn.sip.property_service.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
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
import tn.sip.property_service.entities.UserPreferenceProfile;
import tn.sip.property_service.enums.PropertyStatus;
import tn.sip.property_service.mappers.PropertyMapper;
import tn.sip.property_service.services.LikeService;
import tn.sip.property_service.services.PropertyService;



@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
public class PropertyController {
	 private final PropertyService propertyService;
	 private final LikeService likeService;

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
	        property.setPropertyStatus(propertyDto.getPropertyStatus());
			property.setTypeStanding(propertyDto.getTypeStanding());
	        property.setTitle(propertyDto.getTitle());
	        property.setType(propertyDto.getType());
			property.setNbrEtage(propertyDto.getNbrEtage());
			property.setArea(propertyDto.getArea());
			property.setAgencyId(propertyDto.getAgencyId());

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

	@GetMapping("/agency/{id}")
	public List<Property> getAgencyProperties(@PathVariable Long id) {
			return propertyService.getPropertiesByAgencyId(id);
	}
	@GetMapping("/status/{status}/agency/{agencyId}")
	public ResponseEntity<Long> getPropertiesCountByStatusAndAgency(
			@PathVariable("status") PropertyStatus status,
			@PathVariable("agencyId") Long agencyId) {

		long count = propertyService.countPropertiesByStatusAndAgencyId(status, agencyId);
		return ResponseEntity.ok(count);
	}

	// Endpoint pour obtenir le nombre de propriétés par zone et agence
	@GetMapping("/location/agency/{location}/{agencyId}")
	public ResponseEntity<Long> getPropertiesCountByLocation(
			@PathVariable("agencyId") Long agencyId, @PathVariable("location") String location) {

		long propertiesByLocation = propertyService.countPropertiesByLocation(location,agencyId);
		return ResponseEntity.ok(propertiesByLocation);
	}

	@GetMapping("/property/{id}")
	public ResponseEntity<Long> getAgencyByPropertyId(@PathVariable("id") Long propertyId) {
		Long agencyId = propertyService.getAgencyIdByProperty(propertyId);
		return ResponseEntity.ok(agencyId);
	}
	@GetMapping("/total/{agencyId}")
	public Long getTotalProperties(@PathVariable Long agencyId) {
		return propertyService.getTotalProperties(agencyId);
	}

	@GetMapping("/by-type/{agencyId}")
	public Map<String, Long> getPropertiesByType(@PathVariable Long agencyId) {
		return propertyService.getPropertiesByType(agencyId);
	}

	@GetMapping("/by-status/{agencyId}")
	public Map<String, Long> getPropertiesByStatus(@PathVariable Long agencyId) {
		return propertyService.getPropertiesByStatus(agencyId);
	}

	@PostMapping("/{id}/like")
	public ResponseEntity<?> like(@PathVariable Long id, @RequestParam Long userId) {
		likeService.likeProperty(id, userId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}/unlike")
	public ResponseEntity<?> unlike(@PathVariable Long id, @RequestParam Long userId) {
		likeService.unlikeProperty(id, userId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}/likes/count")
	public ResponseEntity<Long> getLikesCount(@PathVariable Long id) {
		return ResponseEntity.ok(likeService.getLikesCount(id));
	}

	@GetMapping("/{id}/isLiked")
	public ResponseEntity<Boolean> isLikedByUser(@PathVariable Long id, @RequestParam Long userId) {
		return ResponseEntity.ok(likeService.isLikedByUser(id, userId));
	}

	@GetMapping("/favorites/{userId}")
	public ResponseEntity<List<Property>> getFavoritesByUser(@PathVariable Long userId) {
		List<Property> likedProperties = likeService.getFavoritesByUser(userId);
		return ResponseEntity.ok(likedProperties);
	}

	@GetMapping("/recommended/{userId}")
	public ResponseEntity<List<Property>> recommendProperties(@PathVariable Long userId) {
		List<Property> recommended = likeService.getRecommendedPropertiesForUser(userId);
		return ResponseEntity.ok(recommended);
	}



}

