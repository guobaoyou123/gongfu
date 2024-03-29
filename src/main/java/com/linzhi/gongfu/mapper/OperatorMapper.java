package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TOperatorInfo;
import com.linzhi.gongfu.entity.Operator;
import com.linzhi.gongfu.entity.OperatorBase;
import com.linzhi.gongfu.entity.WareHouseOperator;
import com.linzhi.gongfu.vo.trade.VForeignCustomerResponse;
import com.linzhi.gongfu.vo.trade.VOperatorDetailResponse;
import com.linzhi.gongfu.vo.trade.VOperatorListResponse;
import com.linzhi.gongfu.vo.trade.VOperatorPageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 用于进行操作员相关信息的转换
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Mapper(componentModel = "spring", uses = {CompanyMapper.class, SceneMapper.class})
public interface OperatorMapper {
    @Mapping(target = "companyCode", source = "identity.companyCode")
    @Mapping(target = "code", source = "identity.operatorCode")
    @Mapping(target = "companyName", source = "company.nameInCN")
    @Mapping(target = "companyShortName", source = "company.details.shortNameInCN")
    @Mapping(target = "companyDomain", source = "company.subdomainName")
    TOperatorInfo toDTO(Operator operator);


    VOperatorPageResponse.VOperator toVOperatorDTO(TOperatorInfo operator);


    @Mapping(target = "code", source = "identity.operatorCode")
    @Mapping(target = "birthday", expression = "java(operator.getBirthday()==null?null:com.linzhi.gongfu.util.DateConverter.dateFormat(operator.getBirthday()))")
    @Mapping(target = "entryAt", expression = "java(operator.getEntryAt()==null?null:com.linzhi.gongfu.util.DateConverter.dateFormat(operator.getEntryAt()))")
    @Mapping(target = "resignationAt", expression = "java(operator.getResignationAt()==null?null:com.linzhi.gongfu.util.DateConverter.dateFormat(operator.getResignationAt()))")
    @Mapping(target = "sex", expression = "java(operator.getSex()!=null?operator.getSex().trim():null)")
    TOperatorInfo toOperatorDetailDTO(OperatorBase operator);

    @Mapping(target = "state", expression = "java(String.valueOf(operator.getState().getState()))")
    VOperatorDetailResponse.VOperator toOperatorDetailDTOs(TOperatorInfo operator);

    @Mapping(target = "scenes", expression = "java(operator.getScenes().stream().map(TScene::getCode).collect(java.util.stream.Collectors.toSet()))")
    VOperatorListResponse.VOperator toOperatorDTOs(TOperatorInfo operator);

    VForeignCustomerResponse.VOperator toForeignCustomerDetail(TOperatorInfo operatorInfo);

    List<TOperatorInfo> toWareHouseOperators(List<WareHouseOperator> operators);

    @Mapping(target = "code",source = "operator.identity.operatorCode")
    @Mapping(target = "name",source = "operator.name")
    TOperatorInfo  toWareHouseOperator(WareHouseOperator wareHouseOperator);
}
