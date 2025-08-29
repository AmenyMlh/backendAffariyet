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
import tn.sip.property_service.enums.TypeStanding;

@Data
public class PropertyDTO {

	private Long id;
	private String title;
	private String description;
	private PropertyType type;
	private List<String> images;
	private List<String> files;
	private Double price;
	private String location;
	private PropertyStatus propertyStatus;
	private TypeStanding typeStanding;
	private int piecesNumb;
	private int bathNumb;
	private int kitchenNumb;
	private int nbrEtage;
	private Double area;
	private int likeCount;
	private Long agencyId;
}
