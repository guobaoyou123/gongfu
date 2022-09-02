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

    /**
     * 将获取到的三级行政区划信息，转换成可供使用的三级行政区划基础信息
     *
     * @param area 三级行政区划信息
     * @return 三级行政区划基础信息
     */
    @Mapping(target = "disabled", constant = "false")
    @Mapping(target = "lev", expression = "java(Long.parseLong(area.getLev()))")
    TArea toDo(AdministrativeArea area);

    /**
     * 明确可以成功获取到三级行政区划基础信息时，向预获取响应转换
     *
     * @param area 三级行政区划基础信息
     * @return 三级行政区划基本信息预获取响应
     */
    @Mapping(target = "parent", source = "parentCode")
    VAreaResponse.Area toAreaDo(TArea area);

}
