package tn.sip.property_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.sip.property_service.entities.Property;

public interface PropertyRepository extends JpaRepository<Property, Long>{

}
