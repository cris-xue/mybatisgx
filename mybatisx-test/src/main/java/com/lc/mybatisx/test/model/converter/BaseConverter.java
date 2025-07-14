package com.lc.mybatisx.test.model.converter;

import com.lc.mybatisx.dao.Page;
import com.lc.mybatisx.test.model.dto.BaseDto;
import com.lc.mybatisx.test.model.entity.BaseEntity;

import java.util.List;

/**
 * @author ：薛承城
 * @description：账户dto转换器
 * @date ：2019/12/30 16:17
 */
public interface BaseConverter<ENTITY extends BaseEntity, DTO extends BaseDto> {

    DTO toDto(ENTITY entity);

    List<DTO> toDtoList(List<ENTITY> entityList);

    ENTITY toEntity(DTO dto);

    List<ENTITY> toEntityList(List<DTO> dtoList);

    Page<DTO> toPage(Page<ENTITY> page);
}
