package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TOperatorInfo;
import com.linzhi.gongfu.dto.TScene;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.mapper.OperatorMapper;
import com.linzhi.gongfu.mapper.SceneMapper;
import com.linzhi.gongfu.repository.OperatorDetailRepository;
import com.linzhi.gongfu.repository.OperatorRepository;
import com.linzhi.gongfu.repository.OperatorSceneRepository;
import com.linzhi.gongfu.repository.SceneRepository;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.util.RNGUtil;
import com.linzhi.gongfu.vo.VOperatorPageResponse;
import com.linzhi.gongfu.vo.VOperatorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
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
    private final AddressService addressService;
    private final OperatorSceneRepository operatorSceneRepository;
    private final PasswordEncoder passwordEncoder;
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
    public Page<VOperatorPageResponse.VOperator> getOperatorPage(Pageable pageable, String companyCode, String state,String keyWord){
        List<VOperatorPageResponse.VOperator> operatorList= operatorList(companyCode,state,keyWord).stream()
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
    @Cacheable(value = "Operator_List;1800", key = "#companyCode+'-'+#state+'-'+#keyWord")
    public List<Operator>  operatorList(String companyCode,String state,String keyWord){
        return keyWord.equals("")?operatorRepository.findOperatorByStateAndIdentity_CompanyCodeAndIdentity_OperatorCodeNot(state.equals("0")? Availability.DISABLED:Availability.ENABLED,
            companyCode,
            "000"):operatorRepository.findOperatorByStateAndIdentity_CompanyCodeAndIdentity_OperatorCodeNotAndAreaNameLikeOrPhoneLike(
            state.equals("0")? Availability.DISABLED:Availability.ENABLED,
            companyCode,
            "000",
            keyWord,
            keyWord);
    }

    /**
     * 人员详情
     * @param companyCode 公司编码
     * @param operatorCode 操作员编码
     * @return 人员详情
     */
    @Cacheable(value = "Operator_detail;1800", key = "#companyCode+'-'+#operatorCode")
    public Optional<OperatorDetail> operatorDetail(String companyCode, String operatorCode){
             return  operatorDetailRepository.findById(OperatorId.builder()
                     .companyCode(companyCode)
                     .operatorCode(operatorCode)
                 .build());
    }

    /**
     * 场景列表
     * @param companyCode 公司编码
     * @param operator 操作员编码
     * @return 场景列表
     */
    @Cacheable(value = "Scene_List;1800", key = "#companyCode+'-'+#operator")
    public Set<Scene> findScene(String companyCode, String operator){
       return sceneRepository.findScene(operator,companyCode);
    }

    /**
     * 查找操作员详情（包括基本信息和场景列表）
     * @param companyCode 公司编码
     * @param operatorCode 操作员编码
     * @return 操作员详情
     */
    public  Optional<TOperatorInfo> findOperatorDetail(String companyCode, String operatorCode) throws IOException {
       Optional<TOperatorInfo> operator =  operatorDetail( companyCode, operatorCode)
            .map(operatorMapper::toOperatorDetailDTO);
       Set<TScene> tScenes=findScene( companyCode, operatorCode).stream().map(sceneMapper::toDTO).collect(Collectors.toSet());
       operator.orElseThrow(()->new IOException("未查询到数据")).setScenes(tScenes);
        return  operator;
    }

    /**
     * 修改人员信息
     * @param companyCode 公司编码
     * @param operatorCode 操作员编码
     * @param operatorRequest 修改人员信息
     * @return 返回修改成功或者失败信息
     */
    @Caching(evict = {
        @CacheEvict(value = "Operator_detail;1800",key = "#companyCode+'-'+#operatorCode"),
        @CacheEvict(value="Operator_List;1800",key = "#companyCode+'-'",allEntries = true)
    })
    @Transactional
    public boolean modifyOperator(String companyCode, String operatorCode, VOperatorRequest operatorRequest){
        try {
            OperatorDetail operatorDetail = operatorDetail(companyCode,operatorCode).orElseThrow(()->new IOException("为从数据库找到"));
            operatorDetail.setName(operatorRequest.getName());
            operatorDetail.setBirthday(operatorRequest.getBirthday()==null?null:LocalDate.parse(operatorRequest.getBirthday()));
            operatorDetail.setSex(operatorRequest.getSex());
            operatorDetail.setAreaCode(operatorRequest.getAreaCode());
            operatorDetail.setPhone(operatorRequest.getPhone());
            if(operatorRequest.getAreaCode()!=null){
                operatorDetail.setAreaName(addressService.findByCode("",operatorRequest.getAreaCode()));
            }
            operatorDetail.setAddress(operatorRequest.getAddress());
            operatorDetail.setEntryAt(operatorRequest.getEntryAt()==null?null:LocalDate.parse(operatorRequest.getEntryAt()));
            operatorDetail.setResignationAt(operatorRequest.getResignationAt()==null?null:LocalDate.parse(operatorRequest.getResignationAt()));
            operatorDetailRepository.save(operatorDetail);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 添加人员信息
     * @param companyCode 公司编码
     * @param operatorRequest 人员信息
     * @return 返回成功或则失败信息
     */
    @CacheEvict(value="Operator_List;1800",key = "#companyCode+'-'",allEntries = true)
    @Transactional
    public boolean addOperator(String companyCode,VOperatorRequest operatorRequest){
        try{
            String maxCode = operatorDetailRepository.findMaxCode(companyCode).orElse("001");
            OperatorDetail operatorDetail = OperatorDetail.builder()
                .identity(OperatorId.builder()
                    .companyCode(companyCode)
                    .operatorCode(maxCode)
                    .build())
                .name(operatorRequest.getName())
                .phone(operatorRequest.getPhone())
                .sex(operatorRequest.getSex())
                .admin(Whether.NO)
                .birthday(operatorRequest.getBirthday()==null?null:LocalDate.parse(operatorRequest.getBirthday()))
                .entryAt(operatorRequest.getEntryAt()==null?null:LocalDate.parse(operatorRequest.getEntryAt()))
                .areaCode(operatorRequest.getAreaCode())
                .address(operatorRequest.getAddress())
                .state(Availability.ENABLED)
                .password("")
                .build();
            if(operatorRequest.getAreaCode()!=null){
                operatorDetail.setAreaName(addressService.findByCode("",operatorRequest.getAreaCode()));
            }
            operatorDetailRepository.save(operatorDetail);
            if(operatorRequest.getScenes().size()>0)
                saveOperatorScene(operatorRequest.getScenes(),companyCode,maxCode);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 修改人员场景权限
     * @param companyCode 公司编码
     * @param operatorRequests 场景列表
     * @param code 人员编码
     * @return 保存成功或者失败信息
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "Operator_Login;1800", key = "T(String).valueOf(#companyCode).concat(#code)"),
        @CacheEvict(value = "Operator_detail;1800",key = "#companyCode+'-'+#code")
    })
    public boolean modifyOperatorScene(String companyCode,VOperatorRequest operatorRequests,String code){
          try{
              operatorSceneRepository.deleteByOperatorSceneId_DcCompIdAndOperatorSceneId_OperatorCode(companyCode,code);
              if(operatorRequests.getScenes().size()>0)
                  saveOperatorScene(operatorRequests.getScenes(),companyCode,code);
          }catch (Exception e){
              e.printStackTrace();
              return false;
          }
           return true;
    }

    /**
     * 重置和修改密码
     * @param companyCode 公司编码
     * @param code 人员编码
     * @return 保存成功或者失败信息
     */
    @CacheEvict(value = "Operator_Login;1800", key = "T(String).valueOf(#companyCode).concat(#code)")
    @Transactional
    public Optional<String> resetPassword(String companyCode,String code,String password){
        try{
            var flag = true;
            if(password==null) {
                flag=false;
                password = RNGUtil.getLowerLetter(3) + RNGUtil.getNumber(3);
            }
            operatorDetailRepository.updatePassword(
                passwordEncoder.encode(password)
                ,flag?Whether.YES:Whether.NO
                ,OperatorId.builder()
                        .operatorCode(code)
                        .companyCode(companyCode)
                    .build()
            );
            return Optional.of(password);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证密码是否正确
     * @param companyCode 单位编码
     * @param operatorCode 操作员编码
     * @param password 密码
     * @return 返回是或否
     */
    public boolean verifyPassword(String companyCode ,String operatorCode,String password){
        var operator =findOperatorByID(
            OperatorId.builder()
            .companyCode(companyCode)
            .operatorCode(operatorCode)
            .build()
        ).orElseThrow(() -> new UsernameNotFoundException("请求的操作员不存在"));
        return passwordEncoder.matches(password,operator.getPassword()) ;
    }

    /**
     * 保存场景信息
     * @param scene 场景列表
     * @param companyCode 公司编码
     * @param operatorCode 操作员编码
     */
    public void saveOperatorScene(List<String> scene ,String companyCode,String operatorCode){
        List<OperatorScene> operatorSceneList = new ArrayList<>();
        scene.forEach(
            s -> operatorSceneList.add(OperatorScene.builder()
                .operatorSceneId(
                    OperatorSceneId.builder()
                        .operatorCode(operatorCode)
                        .dcCompId(companyCode)
                        .sceneCode(s)
                        .build()
                )
                .build())
        );
        operatorSceneRepository.saveAll(operatorSceneList);
    }
}
