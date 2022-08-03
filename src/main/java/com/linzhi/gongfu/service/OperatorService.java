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
import com.linzhi.gongfu.vo.VOperatorSceneRequest;
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
import java.util.concurrent.atomic.AtomicBoolean;
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
    public Page<VOperatorPageResponse.VOperator> pageOperators(Pageable pageable, String companyCode, String state,String keyWord){
        List<VOperatorPageResponse.VOperator> operatorList= listOperators(companyCode,state).stream()
            .map(operatorMapper::toOperatorDetailDTO)
            .map(operatorMapper::toVOperatorDTO)
            .toList();
        if(!keyWord.equals(""))
            operatorList=operatorList.stream()
                .filter(vOperator -> vOperator.getName().contains(keyWord)||(vOperator.getPhone()!=null&&vOperator.getPhone().contains(keyWord)))
                .toList();

        return PageTools.listConvertToPage(operatorList,pageable);
    }

    /**
     * 查询人员列表
     * @param companyCode 公司编码
     * @param state 状态
     * @return 人员列表
     */
    @Cacheable(value = "Operator_List;1800", key = "#companyCode+'-'+#state")
    public List<OperatorDetail>  listOperators(String companyCode,String state){
        return operatorDetailRepository.findOperatorByStateAndIdentity_CompanyCodeAndIdentity_OperatorCodeNot(state.equals("0")? Availability.DISABLED:Availability.ENABLED,
            companyCode,
            "000");
    }

    /**
     * 人员详情
     * @param companyCode 公司编码
     * @param operatorCode 操作员编码
     * @return 人员详情
     */
    @Cacheable(value = "Operator_detail;1800", key = "#companyCode+'-'+#operatorCode")
    public Optional<OperatorDetail> getOperator(String companyCode, String operatorCode){
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
    public  Optional<TOperatorInfo> getOperatorDetail(String companyCode, String operatorCode) throws IOException {
       Optional<TOperatorInfo> operator =  getOperator( companyCode, operatorCode)
            .map(operatorMapper::toOperatorDetailDTO);
       Set<TScene> tScenes=findScene( companyCode, operatorCode).stream().map(sceneMapper::toDTO).collect(Collectors.toSet());
       operator.orElseThrow(()->new IOException("未查询到数据")).setScenes(tScenes);
        return  operator;
    }

    /**
     * 人员权限列表
     * @param companyCode 单位编码
     * @return 返回人员权限列表
     */
    @Cacheable(value = "Operator_scene_statistics;1800", key = "#companyCode")
    public List<TOperatorInfo> listOperators(String companyCode){
        return   operatorRepository.findOperatorByStateAndIdentity_CompanyCodeAndIdentity_OperatorCodeNot(
                   Availability.ENABLED,
                   companyCode,
                  "000"
                ).stream()
                 .map(operatorMapper::toDTO)
                 .toList();
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
        @CacheEvict(value="Operator_List;1800",key = "#companyCode+'-1'")
    })
    @Transactional
    public boolean modifyOperator(String companyCode, String operatorCode, VOperatorRequest operatorRequest){
        try {
            OperatorDetail operatorDetail = getOperator(companyCode,operatorCode)
                .orElseThrow(()->new IOException("为从数据库找到"));
            operatorDetail.setName(operatorRequest.getName());
            operatorDetail.setBirthday(operatorRequest.getBirthday()==null?null:LocalDate.parse(operatorRequest.getBirthday()));
            operatorDetail.setSex(operatorRequest.getSex()!=null?operatorRequest.getSex().trim():null);
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
    @Caching(evict = {
        @CacheEvict(value="Operator_List;1800",key = "#companyCode+'-1'")
        ,
        @CacheEvict(value = "Operator_scene_statistics;1800", key = "#companyCode")
    })
    @Transactional
    public String saveOperator(String companyCode,VOperatorRequest operatorRequest){
        try{
            String maxCode = operatorDetailRepository.findMaxCode(companyCode).orElse("001");
            String  password = RNGUtil.getLowerLetter(3) + RNGUtil.getNumber(3);
            OperatorDetail operatorDetail = OperatorDetail.builder()
                .identity(OperatorId.builder()
                    .companyCode(companyCode)
                    .operatorCode(maxCode)
                    .build())
                .name(operatorRequest.getName())
                .phone(operatorRequest.getPhone())
                .sex(operatorRequest.getSex()!=null?operatorRequest.getSex().trim():null)
                .admin(Whether.NO)
                .birthday(operatorRequest.getBirthday()==null?null:LocalDate.parse(operatorRequest.getBirthday()))
                .entryAt(operatorRequest.getEntryAt()==null?null:LocalDate.parse(operatorRequest.getEntryAt()))
                .areaCode(operatorRequest.getAreaCode())
                .address(operatorRequest.getAddress())
                .state(Availability.ENABLED)
                .password(passwordEncoder.encode(password))
                .changed(Whether.NO)
                .build();
            if(operatorRequest.getAreaCode()!=null){
                operatorDetail.setAreaName(addressService.findByCode("",operatorRequest.getAreaCode()));
            }
            operatorDetailRepository.save(operatorDetail);
            if(operatorRequest.getScenes().size()>0)
                saveOperatorScene(operatorRequest.getScenes(),companyCode,maxCode);
            return password;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 修改人员场景权限
     * @param companyCode 公司编码
     * @param operatorRequests 场景列表
     * @return 保存成功或者失败信息
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "Operator_Login;1800",key = "T(String).valueOf(#companyCode).concat('*')"),
        @CacheEvict(value = "Scene_List;1800", key="#companyCode+'_'+'*'"),
        @CacheEvict(value = "Operator_scene_statistics;1800", key = "#companyCode")
    })
    public boolean modifyOperatorScene(String companyCode, List<VOperatorSceneRequest> operatorRequests){
          try{
              List<String> operators = new ArrayList<>();
              List<OperatorScene> operatorSceneList = new ArrayList<>();
              operatorRequests.forEach(vOperatorSceneRequest -> {
                  operators.add(vOperatorSceneRequest.getCode());
                  vOperatorSceneRequest.getScenes().forEach(s -> operatorSceneList.add(OperatorScene.builder()
                      .operatorSceneId(
                          OperatorSceneId.builder()
                              .operatorCode(vOperatorSceneRequest.getCode())
                              .dcCompId(companyCode)
                              .sceneCode(s)
                              .build()
                      )
                      .build()) );
              });
              operatorSceneRepository.deleteByOperatorSceneId_DcCompIdAndOperatorSceneId_OperatorCodeIn(companyCode,operators);
              operatorSceneRepository.saveAll(operatorSceneList);
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

    @Transactional
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

    /**
     * 启用和禁用人员
     * @param code 操作员编码
     * @param companyCode 公司编码
     * @param state 状态
     * @return 返回是与否
     */
    @Caching(evict = {
        @CacheEvict(value = "Operator_Login;1800", key = "T(String).valueOf(#companyCode).concat(#code)"),
        @CacheEvict(value = "Operator_List;1800", key = "#companyCode+'-0'"),
        @CacheEvict(value = "Operator_List;1800", key = "#companyCode+'-1'")
    })
    @Transactional
    public boolean modifyOperatorState(String code,String companyCode,String state){
       try{
           OperatorDetail operator = getOperator(companyCode,code).orElseThrow(()->new IOException("没有从数据库中找到"));
           operator.setState(state.equals("0")?Availability.DISABLED:Availability.ENABLED);
           operatorDetailRepository.save(operator);
           return true;
       }catch (Exception e){
           e.printStackTrace();
           return false;
       }
    }

    /**
     * 根据权限查找操作员列表
     * @param companyCode 本单位公司编码
     * @param privilege  权限
     * @return 操作员列表
     */
    public List<TOperatorInfo>  listOperatorsByPrivilege(String companyCode,String privilege){
        List<TOperatorInfo> tOperatorInfos = listOperators(companyCode);
        switch (privilege){
            case "1": return  tOperatorInfos.stream().filter(operatorInfo -> {
                AtomicBoolean flag = new AtomicBoolean(false);
                operatorInfo.getScenes().forEach(t->{
                    if(t.getName().contains("采购")){
                        flag.set(true);
                        return;
                    }
                });
                return  flag.get();
            }).toList();
            case "2" : return null;
        }
        return  null;
    }
}
