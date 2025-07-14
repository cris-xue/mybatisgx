package com.lc.mybatisx.test.model.converter;

import com.lc.mybatisx.test.model.dto.UserDto;
import com.lc.mybatisx.test.model.entity.User;
import org.mapstruct.Mapper;

/**
 * @author ：薛承城
 * @description：账户dto转换器
 * @date ：2019/12/30 16:17
 */
@Mapper(componentModel = "spring")
public interface UserConverter extends BaseConverter<User, UserDto> {
}
