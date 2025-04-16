package tn.sip.user_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import tn.sip.user_service.dto.AgencyDTO;
import tn.sip.user_service.entities.Agency;

@Mapper
public interface AgencyMapper {
    AgencyMapper INSTANCE = Mappers.getMapper(AgencyMapper.class);

    AgencyDTO toAgencyDTO(Agency agency);
}
