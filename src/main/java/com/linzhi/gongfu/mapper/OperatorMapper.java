package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TOperatorInfo;
import com.linzhi.gongfu.entity.Operator;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于进行操作员相关信息的转换
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Mapper(componentModel = "spring", uses = { CompanyMapper.class, SceneMapper.class })
public interface OperatorMapper {
    @Mapping(target = "companyCode", source = "identity.companyCode")
    @Mapping(target = "code", source = "identity.operatorCode")
    @Mapping(target = "companyName", source = "company.nameInCN")
    @Mapping(target = "companyShortName", source = "company.details.shortNameInCN")
    @Mapping(target = "companyDomain", source = "company.subdomainName")
    TOperatorInfo toDTO(Operator operator);
}
