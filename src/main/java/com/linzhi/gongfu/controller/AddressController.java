package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.mapper.AddressMapper;
import com.linzhi.gongfu.mapper.AdministrativeAreaMapper;
import com.linzhi.gongfu.mapper.DisabledAreaMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.AddressService;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 用于处理地址信息
 *
 * @author zgh
 * @create_at 2022-03-23
 */
@RequiredArgsConstructor
@RestController
public class AddressController {
    private final AddressService addressService;
    private final AdministrativeAreaMapper administrativeAreaMapper;
    private final DisabledAreaMapper disabledAreaMapper;
    private final AddressMapper addressMapper;

    /**
     * 三级行政区划列表
     * @return 返回三级行政区划列表
     */
    @GetMapping("/areas")
    public VAreaResponse areaList(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();

        return VAreaResponse.builder()
            .code(200)
            .message("获取三级行政区划列表成功")
            .areas(addressService.areas(session.getSession().getCompanyCode())
                .stream().map(administrativeAreaMapper::toAreaDo)
                .toList()
            )
            .build();
    }

    /**
     * 查询本公司停用的区域
     * @return 停用区域列表
     */
    @GetMapping("/areas/disabled")
    public VDisableAreaResponse disableAreaList(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var list = addressService.findDisabledAreaByCompId(session.getSession().getCompanyCode());
        return  VDisableAreaResponse.builder()
            .code(200)
            .areas(list.stream()
                .map(disabledAreaMapper::toDo)
                .map(disabledAreaMapper::todisabledArea)
                .toList()
            )
            .message("查询禁用区域列表成功")
            .build();
    }


    /**
     * 添加禁用地址
     * @param disableArea 禁用区域编码
     * @return 返回成功信息
     */
    @PostMapping("/area/disabled")
    public VBaseResponse saveDisableArea(@RequestBody Optional<VDisableAreaRequest> disableArea ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var disableArea1 = addressService.saveDisableArea(
            disableArea.orElseGet(VDisableAreaRequest::new),
            session.getSession().getCompanyCode()
        );
        if(disableArea1==null)
           return  VBaseResponse.builder()
               .code(500)
               .message("添加禁用区域失败")
               .build();
        return  VBaseResponse.builder()
            .code(200)
            .message("添加禁用区域成功")
            .build();
    }

    /**
     * 删除禁用区域
     * @param code 区域系统编码
     * @return 成功或者失败信息
     */
    @DeleteMapping("/area/disabled")
    public  VBaseResponse deleteDisableArea(@RequestParam List<String> code){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var map = addressService.deleteDisablesAreaByCode(session.getSession().getCompanyCode(),code);
        return VBaseResponse.builder()
            .code((Integer) map.get("code"))
            .message((String) map.get("message"))
            .build();
    }

    /**
     * 地址列表
     * @return 地址列表信息
     */
    @GetMapping("/addresses")
    public VAddressResponse addresses(
        @RequestParam("areaCode")Optional<String> areaCode,
        @RequestParam("address")Optional<String> address,
        @RequestParam("state")Optional<String> state
        ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var addresses = addressService.findAddressesByCompId(
           session.getSession().getCompanyCode(),
           areaCode.orElseGet(String::new),
           address.orElseGet(String::new),
           state.orElseGet(String::new)
       );
        return VAddressResponse.builder()
            .code(200)
            .message("查询地址列表成功！")
            .addresses(
                addresses.stream()
                    .map(addressMapper::toPreloadAddress)
                    .toList()
            )
            .build();
    }

    /**
     * 添加地址信息
     * @param address 地址信息
     * @return 返回成功或者失败信息
     */
    @PostMapping("/address")
    public VBaseResponse saveAddress(@RequestBody Optional<VAddressRequest> address){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var map = addressService.saveAddress(
            address.orElseGet(VAddressRequest::new),
            session.getSession().getCompanyCode()
        );
        return VBaseResponse.builder()
            .code((int)map.get("code"))
            .message((String)map.get("message"))
            .build();
    }

    /**
     * 修改地址信息
     * @param code 地址编码
     * @param address 地址信息
     * @return 返回成功或者失败信息
     */
    @PutMapping("/address/{code}")
    public VBaseResponse modifyAddress(@PathVariable("code") String code,@RequestBody Optional<VAddressRequest> address){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var map = addressService.modifyAddress(
            code,
            address.orElseGet(VAddressRequest::new),
            session.getSession().getCompanyCode()
        );
        return VBaseResponse.builder()
            .code((int)map.get("code"))
            .message((String)map.get("message"))
            .build();
    }

    /**
     * 禁用地址
     * @param codes 地址编码
     * @return 返回成功或者失败信息
     */
    @PutMapping("/address/disable")
    public VBaseResponse modifyAddressState(@RequestBody VAddressRequest codes){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var map = addressService.modifyAddressState(
            codes.getCodes(),
            "0",
            session.getSession().getCompanyCode()
        );
        return VBaseResponse.builder()
            .code((int)map.get("code"))
            .message((String)map.get("message"))
            .build();
    }

    /**
     * 启用地址
     * @param codes 地址编码
     * @return 返回成功或者失败信息
     */
    @PutMapping("/address/enable")
    public VBaseResponse modifyAddressStateEnable(@RequestBody VAddressRequest codes){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var map = addressService.modifyAddressState(
            codes.getCodes(),
            "1",
            session.getSession().getCompanyCode()
        );
        return VBaseResponse.builder()
            .code((int)map.get("code"))
            .message((String)map.get("message"))
            .build();
    }


}
