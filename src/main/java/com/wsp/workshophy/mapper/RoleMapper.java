package com.wsp.workshophy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.wsp.workshophy.dto.request.RoleRequest;
import com.wsp.workshophy.dto.response.RoleResponse;
import com.wsp.workshophy.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
