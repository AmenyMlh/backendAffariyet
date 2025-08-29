package tn.sip.user_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import tn.sip.user_service.dto.UserDTO;
import tn.sip.user_service.dto.UserUpdateRequest;
import tn.sip.user_service.entities.Agency;
import tn.sip.user_service.entities.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toUserDTO(User user);
    User toUser(UserDTO userDTO);
    List<User> toUsers(List<UserDTO> usersDTO);
    List<UserDTO> toUsersDTO(List<User> users);



}
