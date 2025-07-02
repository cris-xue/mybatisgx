package com.lc.mybatisx.test.model.converter;

import com.lc.mybatisx.test.model.dto.RoleDto;
import com.lc.mybatisx.test.model.entity.Role;
import org.mapstruct.Mapper;

/**
 * @author ：薛承城
 * @description：账户dto转换器
 * @date ：2019/12/30 16:17
 */
@Mapper(componentModel = "spring")
public interface RoleConverter extends BaseConverter<Role, RoleDto> {
}
