package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TOperatorInfo;
import com.linzhi.gongfu.dto.TScene;
import com.linzhi.gongfu.entity.Operator;
import com.linzhi.gongfu.entity.OperatorDetail;
import com.linzhi.gongfu.entity.OperatorId;
import com.linzhi.gongfu.entity.Scene;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.mapper.OperatorMapper;
import com.linzhi.gongfu.mapper.SceneMapper;
import com.linzhi.gongfu.repository.OperatorDetailRepository;
import com.linzhi.gongfu.repository.OperatorRepository;
import com.linzhi.gongfu.repository.SceneRepository;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.VOperatorPageResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final OperatorDetailRepository operatorDetailRepository;
    private final OperatorMapper operatorMapper;
    private final SceneRepository sceneRepository;
    private final SceneMapper sceneMapper;
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
     * 查询人员列表 分页
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

    /**
     * 查询人员列表
     * @param companyCode 公司编码
     * @param state 状态
     * @return 人员列表
     */
    @Cacheable(value = "Operator_page;1800", key = "#companyCode+'-'+#state")
    public List<Operator>  operatorList(String companyCode,String state){
        return operatorRepository.findOperatorByStateAndIdentity_CompanyCode(state.equals("0")? Availability.DISABLED:Availability.ENABLED,companyCode);
    }

    /**
     * 人员详情
     * @param companyCode 公司编码
     * @param operatorCode 操作员编码
     * @return 人员详情
     */
    @Cacheable(value = "Operator_datail;1800", key = "#companyCode+'-'+#operatorCode")
    public Optional<OperatorDetail> operatorDetail(String companyCode, String operatorCode){
             return  operatorDetailRepository.findById(OperatorId.builder()
                     .companyCode(companyCode)
                     .operatorCode(operatorCode)
                 .build());
    }

    @Cacheable(value = "Scene_List;1800", key = "#companyCode+'-'+#operatorCode")
    public Set<Scene> findScene(String companyCode, String operator){
       return sceneRepository.findScene(operator,companyCode);
    }

    /**
     * 查找操作员详情（包括基本信息和场景列表）
     * @param companyCode 公司编码
     * @param operatorCode 操作员编码
     * @return 操作员详情
     */
    public  Optional<TOperatorInfo> findOperatorDtail(String companyCode, String operatorCode){
       Optional<TOperatorInfo> operator =  operatorDetail( companyCode, operatorCode)
            .map(operatorMapper::toOperatorDetailDTO);
       Set<TScene> tScenes=findScene( companyCode, operatorCode).stream().map(sceneMapper::toDTO).collect(Collectors.toSet());
       operator.get().setScenes(tScenes);
        return  operator;
    }
}
