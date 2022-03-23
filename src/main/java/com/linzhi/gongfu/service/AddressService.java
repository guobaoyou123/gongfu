package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TAddress;
import com.linzhi.gongfu.dto.TArea;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.mapper.AddressMapper;
import com.linzhi.gongfu.mapper.AdministrativeAreaMapper;
import com.linzhi.gongfu.repository.AddressRepository;
import com.linzhi.gongfu.repository.AdministrativeAreaRepository;
import com.linzhi.gongfu.repository.DisabledAreaRepository;
import com.linzhi.gongfu.vo.VAddressRequest;
import com.linzhi.gongfu.vo.VDisableAreaRequest;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * 地址信息及处理业务服务
 *
 * @author zgh
 * @create_at 2022-03-22
 */
@RequiredArgsConstructor
@Service
public class AddressService {

    private final DisabledAreaRepository disabledAreaRepository;
    private final AdministrativeAreaRepository administrativeAreaRepository;
    private final AdministrativeAreaMapper administrativeAreaMapper;
    private final JPAQueryFactory queryFactory;
    private  final AddressMapper addressMapper;
    private final AddressRepository addressRepository;

    /**
     * 三级行政区划查找（包括禁用区域状态）
     * @param dcCompId 单位id
     * @return 三级行政区划查找（包括禁用区域状态）列表
     */
    public List<TArea> areas(String dcCompId){
        var list = findAllArea().stream()
            .map(administrativeAreaMapper::toDo)
            .toList();
        List<DisabledArea> disabledAreaList = findDisabledAreaByCompId(dcCompId);
        Map<String,DisabledArea> disabledAreaMap = new HashMap<>();
        disabledAreaList.forEach(disabledArea ->
            disabledAreaMap.put(
                disabledArea.getDisabledAreaId().getCode(),
                disabledArea
            )
        );
        list.forEach(tArea -> {
           DisabledArea disabledArea =  disabledAreaMap.get(tArea.getCode());
           if(disabledArea!=null)
               tArea.setDisabled(true);
        });
        return list;
    }

    /**
     * 查找所有的三级行政区划
     * @return 返回三级行政区划列表
     */
    @Cacheable("AdministrativeArea;1800")
    public List<AdministrativeArea> findAllArea(){
        return  StreamSupport.stream(
            administrativeAreaRepository.findAll().spliterator(),false)
            .toList();
    }

    /**
     * 根据单位id查询所有禁用区域列表
     * @param  compId 单位id
     * @return 返回禁用区域列表
     */
    @Cacheable(value = "DisabledArea_compId;1800", key="#compId",unless = "#result == null")
    public  List<DisabledArea> findDisabledAreaByCompId(String compId){
        return  disabledAreaRepository
            .findAllByDisabledAreaId_DcCompId(compId);
    }

    /**
     * 保存
     * @param disableArea 系统区域编码
     * @param dcCompCode 单位id
     * @return 成功或者错误信息
     */
    @CachePut(value = "DisabledArea_compId;1800", key="#dcCompCode")
    @Transactional
    public DisabledArea saveDisableArea(VDisableAreaRequest disableArea, String dcCompCode){
        try{
           AdministrativeArea area= administrativeAreaRepository.findById(disableArea.getCode()).get();
           String name = findByCode("",disableArea.getCode());
            DisabledArea disabledArea = DisabledArea.builder()
                .disabledAreaId(
                    DisabledAreaId.builder()
                        .code(area.getCode())
                        .dcCompId(dcCompCode)
                        .build()
                )
                .country(area.getCountry())
                .idcode(area.getIdcode())
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
            disabledAreaRepository.save(
                disabledArea
            );
            return  disabledArea;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    /**
     * 删除禁用区域
     * @param compId 单位id
     * @param code 系统区域编码列表
     * @return 成功或失败信息
     */
    @CacheEvict(value = "DisabledArea_compId;1800",key = "#compId",beforeInvocation=true)
    @Transactional
    public Map<String,Object> deleteDisablesAreaByCode(String compId,List<String> code){
        Map<String,Object> map = new HashMap<>();
        try {
            List<DisabledAreaId> ids = new ArrayList<>();
            code.forEach(s -> ids.add(DisabledAreaId.builder()
                    .dcCompId(compId)
                    .code(s)
                    .build()
                )
            );
            disabledAreaRepository.deleteAllById(ids);
            map.put("code",200);
            map.put("message","删除成功");
        }catch (Exception e){
            e.printStackTrace();
            map.put("code",500);
            map.put("message","删除失败，请稍后再试");
            return map;
        }
        return  map;
    }

    /**
     * 根据查询条件查询地址信息（包含禁用区域）
     * @param compId  单位id
     * @param areaCode 区域编码
     * @param addresses 地址信息
     * @param state 状态
     * @return 返回地址信息（包括禁用区域）
     */
    public List<TAddress> findAddressesByCompId(String compId,String areaCode,String addresses,String state){
        //根据条件查询地址信息
        List<TAddress> tAddresses =findAddresses(compId, areaCode, addresses, state).stream()
            .map(addressMapper::toAddress)
            .toList();

        List<DisabledArea> list = findDisabledAreaByCompId(compId);
        Map<String,DisabledArea> map  = new HashMap<>();
        list.forEach(disabledArea -> map.put(disabledArea.getDisabledAreaId().getCode(),disabledArea));
        tAddresses.forEach(tAddress -> {
            DisabledArea d=  map.get(tAddress.getCountry()+tAddress.getAreaCode());
            DisabledArea d1=  map.get(tAddress.getCountry()+tAddress.getAreaCode().substring(0,2)+"0000");
            DisabledArea d2=  map.get(tAddress.getCountry()+tAddress.getAreaCode().substring(0,4)+"00");
            if(d!=null)
                tAddress.setDisabled(true);
            if(d1!=null)
                tAddress.setDisabled(true);
            if(d2!=null)
                tAddress.setDisabled(true);
        });
        return  tAddresses;
    }

    /**
     * 根据条件查询地址信息
     * @param compId 单位id
     * @param areaCode 区域编码
     * @param addresses 地址信息
     * @param state 状态
     * @return 返回地址信息
     */
    @Cacheable(value = "Addresses_compId;1800", unless = "#result == null")
    public List<Address> findAddresses(String compId,String areaCode,String addresses,String state){
        //根据条件查询产品信息
        QAddress qAddress = QAddress.address1;
        JPAQuery<Address> query = queryFactory.select(qAddress).from(qAddress);
        query.where(qAddress.addressId.dcCompId.eq(compId));
        if(!areaCode.isEmpty()) {
            AdministrativeArea area = administrativeAreaRepository.findById(areaCode).get();
            query.where(qAddress.country.eq(area.getNumber()));
            if(area.getLev().equals("1")||area.getLev().equals("2")){
                query.where(qAddress.areaName.like("%"+area.getName()+"%"));
            }else{
                query.where(qAddress.areaCode.eq(area.getIdcode()));
            }
        }
        if(!addresses.isEmpty())
            query.where(qAddress.address.like("%"+addresses+"%"));
        if ( !state.isEmpty())
            query.where(qAddress.state.eq(Availability.valueOf(state)));
        query.orderBy(qAddress.address.asc());
        return  query.fetch();
    }

    /**
     * 保存地址信息
     * @param vAddress 地址信息
     * @param compId 单位id
     * @return 返回成功或者失败信息
     */
    @CacheEvict(value = "Addresses_compId;1800",allEntries=true)
    @Transactional
    public Map<String,Object> saveAddress(VAddressRequest vAddress,String compId){
        Map<String,Object> map = new HashMap<>();
        try{
            String code = addressRepository.findMaxCode(compId);
            if(code==null)
                code="001";
            AdministrativeArea area = administrativeAreaRepository.findById(vAddress.getAreaCode()).get();
            String name = findByCode("",vAddress.getAreaCode());
            Address address = Address.builder()
                .addressId(
                    AddressId.builder()
                        .dcCompId(compId)
                        .code(code)
                        .build()
                )
                .address(vAddress.getAddress())
                .country(area.getNumber())
                .areaCode(area.getIdcode())
                .areaName(name)
                .flag(vAddress.getFlag()? Whether.YES:Whether.NO)
                .state(Availability.ENABLED)
                .build();
            if(vAddress.getFlag()) {
                addressRepository.updateAddressById("0",compId);
            }

            addressRepository.save(address);
            map.put("code",200);
            map.put("message","保存地址成功");
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("code",500);
            map.put("message","保存地址失败");
            return map;
        }
    }

    /**
     * 修改地址信息
     * @param code 地址编码
     * @param vAddress 地址信息
     * @param compId 单位id
     * @return 返回成功或者失败信息
     */
    @CacheEvict(value = "Addresses_compId;1800",allEntries=true)
    @Transactional
    public Map<String,Object> modifyAddress(String code,VAddressRequest vAddress,String compId){
        Map<String,Object> map = new HashMap<>();
        try{
            Address address = addressRepository.findById(AddressId.builder()
                    .code(code)
                    .dcCompId(compId)
                .build()).get();
            AdministrativeArea area = administrativeAreaRepository.findById(vAddress.getCountry()+vAddress.getAreaCode()).get();
            String name = findByCode("",vAddress.getCountry()+vAddress.getAreaCode());

            if(vAddress.getFlag()&&!(address.getFlag().getState()=='0')) {
                addressRepository.updateAddressById("0",compId);
            }
            address.setAddress(vAddress.getAddress());
            address.setCountry(area.getNumber());
            address.setAreaName(name);
            address.setFlag(vAddress.getFlag()? Whether.YES:Whether.NO);
            address.setAreaCode(area.getIdcode());

            addressRepository.save(address);
            map.put("code",200);
            map.put("message","修改地址成功");
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("code",500);
            map.put("message","修改地址失败");
            return map;
        }
    }

    /**
     * 修改地址状态
     * @param code 地址编码
     * @param state 状态
     * @param compId 公司id
     * @return 返回成功或失败信息
     */
    @CacheEvict(value = "Addresses_compId;1800",allEntries=true)
    @Transactional
    public Map<String,Object> modifyAddressState(List<String> code,String state ,String compId){
        Map<String,Object> map = new HashMap<>();
        try{
            addressRepository.updateAddressStateById(state,compId,code);
            map.put("code",200);
            map.put("message","设置成功");
            return map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("code",500);
            map.put("message","设置失败");
            return map;
        }
    }

    /**
     * 根据系统区域编码查找父级名称
     * @param name 名称
     * @param code 系统编码
     * @return 父级名称+该区域的名称
     */
    public  String  findByCode(String name ,String code){
        AdministrativeArea area= administrativeAreaRepository.findById(code).get();
        if(!name.contains(area.getName()))
            name= area.getName()+name;
        if(area.getLev().equals("1")){
            return  name;
        }
       return findByCode(name, area.getParentCode());
    }
}
