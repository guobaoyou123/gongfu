package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TOperatorInfo;
import com.linzhi.gongfu.entity.Operator;
import com.linzhi.gongfu.entity.OperatorId;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.mapper.OperatorMapper;
import com.linzhi.gongfu.repository.OperatorRepository;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.VOperatorPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 操作员相关信息处理及业务服务
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@RequiredArgsConstructor
@Service
public class OperatorService {
    private final OperatorRepository operatorRepository;
    private final OperatorMapper operatorMapper;

    /**
     * 根据所提供的操作员ID寻找制定的操作员信息。
     *
     * @param id 已经生成的操作员ID
     * @return 可能存在的操作员信息
     */
    @Cacheable(value = "Operator_Login;1800", key = "T(String).valueOf(#id.companyCode).concat(#id.operatorCode)")
    public Optional<TOperatorInfo> findOperatorByID(OperatorId id) {
        return operatorRepository.findById(id).map(operatorMapper::toDTO);
    }

    /**
     * 查询人员列表
     * @param pageable 页码
     * @param companyCode 公司编码
     *  @param state 状态
     * @return 公司人员列表
     */

    public Page<VOperatorPageResponse.VOperator> getOperatorPage(Pageable pageable, String companyCode, String state){
        List<VOperatorPageResponse.VOperator> operatorList= operatorList(companyCode,state).stream()
            .map(operatorMapper::toDTO)
            .map(operatorMapper::toVOperatorDTO)
            .toList();

        return PageTools.listConvertToPage(operatorList,pageable);
    }

    @Cacheable(value = "Operator_page;1800", key = "#companyCode+'-'+#state")
    public List<Operator>  operatorList(String companyCode,String state){
        return operatorRepository.findOperatorByStateAndIdentity_CompanyCode(state.equals("0")? Availability.DISABLED:Availability.ENABLED,companyCode);

    }
}
