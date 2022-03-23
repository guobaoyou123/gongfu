package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TArea;
import com.linzhi.gongfu.entity.AdministrativeArea;
import com.linzhi.gongfu.vo.VAreaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换三级行政区划相关信息
 *
 * @author zgh
 * @create_at 2022-02-07
 */
@Mapper(componentModel = "spring")
public interface AdministrativeAreaMapper {
    @Mapping(target = "disabled", constant = "false")
    @Mapping(target = "lev",  expression = "java(Long.parseLong(area.getLev()))")
    TArea toDo(AdministrativeArea area);
    @Mapping(target = "parent",source = "parentCode")
    VAreaResponse.Area toAreaDo(TArea area);

}
