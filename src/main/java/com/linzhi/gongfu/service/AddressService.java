package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TAddress;
import com.linzhi.gongfu.dto.TArea;
import com.linzhi.gongfu.dto.TCompContacts;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.mapper.AddressMapper;
import com.linzhi.gongfu.mapper.AdministrativeAreaMapper;
import com.linzhi.gongfu.mapper.CompContactsMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.vo.VAddressRequest;
import com.linzhi.gongfu.vo.VCompContactsRequest;
import com.linzhi.gongfu.vo.VDisableAreaRequest;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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
    private final CompContactsRepository compContactsRepository;
    private final CompContactsMapper compContactsMapper;
    private final OperatorRepository operatorRepository;

    /**
     * 三级行政区划查找（包括禁用区域状态）
     * @param companyCode 单位id
     * @return 三级行政区划查找（包括禁用区域状态）列表
     */
    public List<TArea> areas(String companyCode){
        var list = findAllArea().stream()
            .map(administrativeAreaMapper::toDo)
            .toList();
        List<DisabledArea> disabledAreaList = findDisabledAreaByCompanyCode(companyCode);
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
     * @param  companyCode 单位id
     * @return 返回禁用区域列表
     */
    @Cacheable(value = "DisabledArea_compId;1800", key="#companyCode",unless = "#result == null")
    public  List<DisabledArea> findDisabledAreaByCompanyCode(String companyCode){
        return  disabledAreaRepository
            .findAllByDisabledAreaId_DcCompId(companyCode);
    }

    /**
     * 保存
     * @param disableArea 系统区域编码
     * @param companyCode 单位id
     * @return 成功或者错误信息
     */
    @CacheEvict(value = "DisabledArea_compId;1800", key="#companyCode")
    @Transactional
    public DisabledArea saveDisableArea(VDisableAreaRequest disableArea, String companyCode){
        try{
           AdministrativeArea area= administrativeAreaRepository.findById(disableArea.getCode())
               .orElseGet(AdministrativeArea::new);
           String name = findByCode("",disableArea.getCode());
            DisabledArea disabledArea = DisabledArea.builder()
                .disabledAreaId(
                    DisabledAreaId.builder()
                        .code(area.getCode())
                        .dcCompId(companyCode)
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
     * @param companyCode 单位id
     * @param code 系统区域编码列表
     * @return 成功或失败信息
     */
    @CacheEvict(value = "DisabledArea_compId;1800",key = "#companyCode",beforeInvocation=true)
    @Transactional
    public Map<String,Object> deleteDisablesAreaByCode(String companyCode,List<String> code){
        Map<String,Object> map = new HashMap<>();
        try {
            List<DisabledAreaId> ids = new ArrayList<>();
            code.forEach(s -> ids.add(DisabledAreaId.builder()
                    .dcCompId(companyCode)
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
     * @param companyCode  单位id
     * @param areaCode 区域编码
     * @param addresses 地址信息
     * @param state 状态
     * @return 返回地址信息（包括禁用区域）
     */
    public List<TAddress> findAddressesByCompId(String companyCode,String areaCode,String addresses,String state){
        //根据条件查询地址信息
        List<TAddress> tAddresses =findAddresses(companyCode, areaCode, addresses, state).stream()
            .map(addressMapper::toAddress)
            .toList();

        List<DisabledArea> list = findDisabledAreaByCompanyCode(companyCode);
        Map<String,DisabledArea> map  = new HashMap<>();
        list.forEach(disabledArea -> map.put(disabledArea.getDisabledAreaId().getCode(),disabledArea));
        tAddresses.forEach(tAddress -> {
            DisabledArea d=  map.get(tAddress.getAreaCode());
            DisabledArea d1=  map.get(tAddress.getAreaCode().substring(0,6)+"0000");
            DisabledArea d2=  map.get(tAddress.getAreaCode().substring(0,8)+"00");
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
     * @param companyCode 单位id
     * @param areaCode 区域编码
     * @param addresses 地址信息
     * @param state 状态
     * @return 返回地址信息
     */
    @Cacheable(value = "Addresses_compId;1800", unless = "#result == null")
    public List<Address> findAddresses(String companyCode,String areaCode,String addresses,String state){
        //根据条件查询产品信息
        QAddress qAddress = QAddress.address1;
        JPAQuery<Address> query = queryFactory.select(qAddress).from(qAddress);
        query.where(qAddress.addressId.dcCompId.eq(companyCode));
        if(!areaCode.isEmpty()) {
            AdministrativeArea area = administrativeAreaRepository.findById(areaCode)
                .orElseGet(AdministrativeArea::new);
            if(area.getLev().equals("1")||area.getLev().equals("2")){
                query.where(qAddress.areaName.like("%"+area.getName()+"%"));
            }else{
                query.where(qAddress.areaCode.eq(areaCode));
            }
        }
        if(!addresses.isEmpty())
            query.where(qAddress.address.like("%"+addresses+"%"));
        if ( !state.isEmpty())
            query.where(qAddress.state.eq(state.equals("0")?Availability.DISABLED:Availability.ENABLED));
        query.orderBy(qAddress.address.asc());
        return  query.fetch();
    }

    /**
     * 保存地址信息
     * @param vAddress 地址信息
     * @param companyCode 单位id
     * @return 返回成功或者失败信息
     */
    @CacheEvict(value = "Addresses_compId;1800",allEntries=true)
    @Transactional
    public Map<String,Object> saveAddress(VAddressRequest vAddress,String companyCode){
        Map<String,Object> map = new HashMap<>();
        try{
            String code = addressRepository.findMaxCode(companyCode);
            if(code==null)
                code="001";
            String name = findByCode("",vAddress.getAreaCode());
            Address address = Address.builder()
                .addressId(
                    AddressId.builder()
                        .dcCompId(companyCode)
                        .code(code)
                        .build()
                )
                .address(vAddress.getAddress())
                .areaCode(vAddress.getAreaCode())
                .areaName(name)
                .flag(vAddress.getFlag()? Whether.YES:Whether.NO)
                .state(Availability.ENABLED)
                .build();
            if(vAddress.getFlag()) {
                addressRepository.updateAddressById("0",companyCode);
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
     * @param companyCode 单位id
     * @return 返回成功或者失败信息
     */
    @CacheEvict(value = "Addresses_compId;1800",allEntries=true)
    @Transactional
    public Map<String,Object> modifyAddress(String code,VAddressRequest vAddress,String companyCode){
        Map<String,Object> map = new HashMap<>();
        try{
            Address address = addressRepository.findById(
                AddressId.builder()
                    .code(code)
                    .dcCompId(companyCode)
                .build()
            ).orElseGet(Address::new);

            String name = findByCode("",vAddress.getAreaCode());

            if(vAddress.getFlag()&&!(address.getFlag().getState()=='0')) {
                addressRepository.updateAddressById("0",companyCode);
            }

            address.setAddress(vAddress.getAddress());
            address.setAreaName(name);
            address.setFlag(vAddress.getFlag()? Whether.YES:Whether.NO);
            address.setAreaCode(vAddress.getAreaCode());
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
     * 验证地址信息是否已经存在
     * @param areaCode 区域编码
     * @param address 地址信息
     * @param code 地址编码
     * @param companyCode 公司id
     * @return 返回 是或者否
     */
   public Boolean addressVerification(String areaCode,String address,String code,String companyCode){
       //判断是否存在
       var list = addressRepository.findAddressByAreaCodeAndAddressLikeAndAddressId_DcCompId(areaCode,address,companyCode);
       if(code != null)
           list=list.stream()
                     .filter(address1 -> !address1.getAddressId().getCode().equals(code))
                     .toList();
       return list.size() <= 0;
   }

    /**
     * 修改地址状态
     * @param code 地址编码
     * @param state 状态
     * @param companyCode 公司id
     * @return 返回成功或失败信息
     */
    @CacheEvict(value = "Addresses_compId;1800",allEntries=true)
    @Transactional
    public Map<String,Object> modifyAddressState(List<String> code,String state ,String companyCode){
        Map<String,Object> map = new HashMap<>();
        try{
            addressRepository.updateAddressStateById(state,companyCode,code);
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
        Optional<AdministrativeArea> area= administrativeAreaRepository.findById(code);
        if(!area.isEmpty()) {
            if (!name.contains(area.get().getName()))
                name = area.get().getName() + name;
            if (area.get().getLev().equals("1")) {
                return name;
            }
            return findByCode(name, area.get().getParentCode());
        }
        return "";
    }

    /**
     * 获取联系人列表
     * @param operator 操作员编码
     * @param addressCode 地址状态
     * @param companyCode 公司id
     * @return 返回联系人列表
     */
  public List<TCompContacts> findContactByAddrCode(String operator,String companyCode,String addressCode,String state){
       List<CompContacts> list;
       Operator operator1= operatorRepository.findById(OperatorId.builder()
              .operatorCode(operator)
              .companyCode(companyCode)
          .build()).orElseGet(Operator::new);
       if(operator1.getAdmin().equals(Whether.YES)) {
          list = compContactsRepository.findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndStateOrderByContCompName(
              addressCode,
              companyCode,
              state.equals("1") ? Availability.ENABLED : Availability.DISABLED
          );
       }else{
         list = compContactsRepository.findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndCompContactsId_OperatorCodeAndStateOrderByContCompName(
             addressCode,
             companyCode,
             operator,
             state.equals("1")?Availability.ENABLED:Availability.DISABLED
         );
       }

      List<TCompContacts> tCompContacts= list.stream()
          .map(compContactsMapper::toTCompContacts)
          .toList();

      tCompContacts.forEach(tCompContacts1 -> {
          if(tCompContacts1.getOperatorCode().equals(operator))
              tCompContacts1.setReadOnly(false);
      });
      return  tCompContacts;
    }

    /**
     * 保存联系人
     * @param compContacts  联系人信息
     * @param operator 操作员编码
     * @param companyCode 公司编码
     * @return 返回联系人信息
     */
    @Transactional
    public CompContacts saveCompContacts(VCompContactsRequest compContacts,String operator,String companyCode){
         try{
             String maxCode = compContactsRepository.findMaxCode(companyCode,compContacts.getAddressCode(),operator);
             if(maxCode==null){
                 maxCode="001";
             }
             CompContacts compContacts1 = CompContacts.builder()
                 .compContactsId(CompContactsId.builder()
                     .addrCode(compContacts.getAddressCode())
                     .code(operator+maxCode)
                     .dcCompId(companyCode)
                     .operatorCode(operator)
                     .build())
                 .state(Availability.ENABLED)
                 .contCompName(compContacts.getCompanyName())
                 .contName(compContacts.getName())
                 .contPhone(compContacts.getPhone())
                 .build();
             compContactsRepository.save(compContacts1);
             return  compContacts1;
         }catch (Exception e){
             e.printStackTrace();
             return null;
        }
    }

    /**
     * 修改联系人
     * @param compContacts  联系人信息
     * @param code 联系人编码
     * @param operator 操作员编码
     * @param companyCode 公司编码
     * @return 返回联系人信息
     */
    @Transactional
    public CompContacts modifyCompContacts(VCompContactsRequest compContacts,String code,String operator,String companyCode){
        try{
            CompContacts contacts = compContactsRepository.findById(CompContactsId.builder()
                    .operatorCode(operator)
                    .dcCompId(companyCode)
                    .code(code)
                    .addrCode(compContacts.getAddressCode())
                .build()).orElseGet(CompContacts::new);
            if(StringUtils.isNotBlank(compContacts.getCompanyName())){
                contacts.setContCompName(compContacts.getCompanyName());
            }
            if(StringUtils.isNotBlank(compContacts.getName())){
                contacts.setContName(compContacts.getName());
            }
            if(StringUtils.isNotBlank(compContacts.getPhone())){
                contacts.setContPhone(compContacts.getPhone());
            }
            if(StringUtils.isNotBlank(compContacts.getState())){
                contacts.setState(compContacts.getState().equals("0")?Availability.DISABLED:Availability.ENABLED);
            }
            compContactsRepository.save(contacts);
            return  contacts;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 修改联系人启用 禁用状态
     * @param code  联系人编码
     * @param operator 操作员编码
     * @param companyCode 公司编码
     * @param state 停用启用状态
     * @param addressCode 地址编码
     * @return 修改成功或者失败
     */
    @Transactional
    public Boolean modifyCompContactState(List<String> code,String operator,String companyCode,String state,String addressCode){
        try{
            compContactsRepository.updateCompContactsStateById(state,companyCode,code,addressCode,operator);
            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 验证联系人是否存在
     * @param addressCode 地址编码
     * @param operator 操作员
     * @param companyCode 公司编码
     * @param contactsCode 联系人编码
     * @param contactName 联系人姓名
     * @param contactPhone 联系人电话
     * @return 返回是或否
     */
    public  Boolean contactsVerification(String addressCode,String operator,String companyCode,String contactsCode,String contactName,String contactPhone){

        List<CompContacts> list =  compContactsRepository.findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndCompContactsId_OperatorCodeOrderByContCompName(
            addressCode,companyCode,operator);
        if(contactsCode!=null){
            list =list.stream().filter(
                compContacts -> !compContacts.getCompContactsId().getCode().equals(contactsCode)
            ).toList();
        }
        list =list.stream().filter(
            compContacts -> compContacts.getContName().equals(contactName)&&compContacts.getContPhone().equals(contactPhone)
        ).toList();

        return list.size() <= 0;
    }
}
