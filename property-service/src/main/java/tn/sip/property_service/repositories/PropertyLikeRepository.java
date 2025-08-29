package tn.sip.property_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.sip.property_service.entities.Property;
import tn.sip.property_service.entities.PropertyLike;

import java.util.List;

public interface PropertyLikeRepository extends JpaRepository<PropertyLike, Long> {
    boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);
    void deleteByUserIdAndPropertyId(Long userId, Long propertyId);
    long countByPropertyId(Long propertyId);
    @Query("SELECT pl FROM PropertyLike pl WHERE pl.userId = :userId")
    List<PropertyLike> findPropertiesLikedByUser(@Param("userId") Long userId);




}