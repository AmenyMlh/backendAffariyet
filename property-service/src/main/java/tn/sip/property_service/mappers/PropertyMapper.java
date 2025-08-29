package tn.sip.property_service.mappers;

import org.mapstruct.Mapper;
import tn.sip.property_service.dtos.PropertyDTO;
import tn.sip.property_service.entities.Property;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PropertyMapper {
    PropertyDTO toDto(Property property);
    List<PropertyDTO> toDtoList(List<Property> properties);
}
