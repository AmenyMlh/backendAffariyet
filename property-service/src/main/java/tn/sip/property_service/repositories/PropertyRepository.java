package tn.sip.property_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.sip.property_service.entities.Property;
import tn.sip.property_service.enums.PropertyStatus;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long>{
    List<Property> findPropertiesByAgencyId(Long agencyId);

    @Query("SELECT COUNT(p) FROM Property p WHERE p.propertyStatus = :status AND p.agencyId = :agencyId")
    long countByPropertyStatusAndAgencyId(PropertyStatus status, Long agencyId);


    @Query("SELECT COUNT(p) FROM Property p WHERE p.location = :location AND p.agencyId = :agencyId")
    long countPropertiesByLocationAndAgencyId(String location,Long agencyId);

    @Query("SELECT p.agencyId FROM Property p WHERE p.id = :propertyId")
    Long getAgencyIdByPropertyId(Long propertyId);

    @Query("SELECT COUNT(p) FROM Property p WHERE p.agencyId = :agencyId")
    Long countPropertiesByAgency(@Param("agencyId") Long agencyId);

    @Query("SELECT p.type, COUNT(p) FROM Property p WHERE p.agencyId = :agencyId GROUP BY p.type")
    List<Object[]> countPropertiesByType(@Param("agencyId") Long agencyId);

    @Query("SELECT p.propertyStatus, COUNT(p) FROM Property p WHERE p.agencyId = :agencyId GROUP BY p.propertyStatus")
    List<Object[]> countPropertiesByStatus(@Param("agencyId") Long agencyId);



}
