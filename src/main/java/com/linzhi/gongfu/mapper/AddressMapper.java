package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TAddress;
import com.linzhi.gongfu.entity.Address;
import com.linzhi.gongfu.vo.VAddressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换地址相关信息
 *
 * @author zgh
 * @create_at 2022-03-23
 */
@Mapper(componentModel = "spring")
public interface AddressMapper {
    /**
     * 将地址信息转换为可用的详细地址信息
     * @param address 地址信息
     * @return 可用的详细地址信息
     */
    @Mapping(target = "dcCompId",source = "addressId.dcCompId")
    @Mapping(target = "code",source = "addressId.code")
    @Mapping(target = "disabled",constant = "false")
    @Mapping(target = "flag",expression = "java(address.getFlag().getState()=='0'?false:true)")
    @Mapping(target = "state",expression = "java(String.valueOf(address.getState().getState()))")
    TAddress  toAddress(Address address);
    /**
     * 明确可以成功获取到地址基础信息时，向预获取响应转换
     *
     * @param tAddress 地址基础信息
     * @return 地址基本信息预获取响应
     */
    VAddressResponse.VAddress toPreloadAddress(TAddress tAddress);
}
