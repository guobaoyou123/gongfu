package com.linzhi.gongfu.mapper.storage;

import com.linzhi.gongfu.dto.TWareHouse;
import com.linzhi.gongfu.entity.WareHouse;
import com.linzhi.gongfu.mapper.OperatorMapper;
import com.linzhi.gongfu.vo.storage.VWareHouseListResponse;
import com.linzhi.gongfu.vo.storage.VWareHouseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {OperatorMapper.class})
public interface WareHouseMapper {

    /**
     * 将获取的库房信息转换为可供使用的库房详情
     *
     * @param wareHouse 库房详情
     * @return 返回可供使用的库房详情
     */
    @Mapping(target = "type",constant = "0")
    @Mapping(target = "AuthorizedOperators", source = "operatorList")
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.getDateTime(wareHouse.getCreatedAt())+\"\")")
    TWareHouse toTWareHouse(WareHouse wareHouse);

    /**
     * 将获取的可供使用的库房详情装换成前端页面展示的库房列表
     * @param tWareHouse 可供使用的库房详情
     * @return 前端展示的库房列表
     */
    VWareHouseListResponse.VWareHouse toVWareHouse(TWareHouse tWareHouse);

    /**
     * 将获取的可供使用的库房详情转换成前端页面展示的库房列表
     * @param tWareHouse 可供使用的库房详情
     * @return 前端展示的库房详情
     */
    VWareHouseResponse.VWareHouse toVWareHouseDetail(TWareHouse tWareHouse);

}
