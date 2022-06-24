package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TDisableArea;
import com.linzhi.gongfu.entity.DisabledArea;
import com.linzhi.gongfu.vo.VDisableAreaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换禁用区域的划相关信息
 *
 * @author zgh
 * @create_at 2022-02-07
 */
@Mapper(componentModel = "spring")
public interface DisabledAreaMapper {

    @Mapping(target = "code",source = "disabledAreaId.code")
    TDisableArea toDo(DisabledArea disabledArea);
    @Mapping(target = "createdAt",expression = "java(com.linzhi.gongfu.util.DateConverter.getDateTime(area.getCreatedAt()))")
    VDisableAreaResponse.Area toDisabledArea(TDisableArea area);
}
