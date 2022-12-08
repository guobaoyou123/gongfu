package com.linzhi.gongfu.mapper.trade;

import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.entity.Blacklist;
import com.linzhi.gongfu.vo.trade.VRefusedListResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换黑名单公司相关信息
 *
 * @author zgh
 * @create_at 2022-08-01
 */
@Mapper(componentModel = "spring")
public interface BlacklistMapper {

    /**
     * 将黑名单公司信息 转换为可供使用的公司信息
     *
     * @param blacklist 黑名单中的公司信息
     * @return 可供使用的公司信息
     */
    @Mapping(target = "code", source = "company.id")
    @Mapping(target = "name", source = "company.nameInCN")
    TCompanyBaseInformation toTCompanyDetail(Blacklist blacklist);

    /**
     * 明确可以成功获取到始终拒绝公司的基础信息时，向预获取响应转换
     *
     * @param company 入格单位公司基础信息
     * @return 始终拒绝列表的公司基本信息预获取响应
     */
    @Mapping(target = "companyName", source = "name")
    VRefusedListResponse.VCompany toRefusedCompany(TCompanyBaseInformation company);

}
