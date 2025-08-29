package tn.sip.property_service.entities;

import java.util.List;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.sip.property_service.enums.PropertyStatus;
import tn.sip.property_service.enums.PropertyType;
import tn.sip.property_service.enums.TypeStanding;

@Entity
@Table(name = "properties")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType type;


    @ElementCollection
    @CollectionTable(
        name = "property_images",
        joinColumns = @JoinColumn(name = "property_id")
    )
    @Column(name = "image_url")
    private List<String> images;
    
    @ElementCollection
    @CollectionTable(
        name = "property_files",
        joinColumns = @JoinColumn(name = "property_id")
    )
    @Column(name = "file_url")
    private List<String> files;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PropertyStatus propertyStatus;

    @Enumerated(EnumType.STRING)
    private TypeStanding typeStanding;
    
    private int piecesNumb;
    
    private int bathNumb;
    
    private int kitchenNumb;

    private int nbrEtage;

    private Double area;

    private int likeCount = 0;

    private Long agencyId;

    
}
