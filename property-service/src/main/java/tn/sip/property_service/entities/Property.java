package tn.sip.property_service.entities;

import java.util.List;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.sip.property_service.enums.PropertyStatus;
import tn.sip.property_service.enums.PropertyType;

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
    
    private int piecesNumb;
    
    private int bathNumb;
    
    private int kitchenNumb;
    
}
