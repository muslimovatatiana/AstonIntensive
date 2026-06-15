package ru.aston.hometask4.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.aston.hometask4.dto.UserRequestDto;
import ru.aston.hometask4.dto.UserResponseDto;
import ru.aston.hometask4.models.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "name", source = "name", qualifiedByName = "trim")
    @Mapping(target = "email", source = "email", qualifiedByName = "trim")
    User toEntity(UserRequestDto requestDto);

    UserResponseDto toResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "name", source = "name", qualifiedByName = "trim")
    @Mapping(target = "email", source = "email", qualifiedByName = "trim")
    void updateUserFromDto(UserRequestDto requestDto, @MappingTarget User user);

    @Named("trim")
    default String trimString(String value) {
        return value != null ? value.trim() : null;
    }
}
