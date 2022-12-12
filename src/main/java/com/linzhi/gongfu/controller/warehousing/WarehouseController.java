package com.linzhi.gongfu.controller.warehousing;

import com.linzhi.gongfu.mapper.warehousing.WareHouseMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.warehousing.WarehouseService;
import com.linzhi.gongfu.vo.VBaseResponse;
import com.linzhi.gongfu.vo.warehousing.VWareHouseListResponse;
import com.linzhi.gongfu.vo.warehousing.VWareHouseRequest;
import com.linzhi.gongfu.vo.warehousing.VWareHouseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

/**
 * 用于处理自有库信息
 *
 * @author zgh
 * @create_at 2022-12-09
 */
@RequiredArgsConstructor
@RestController
public class WarehouseController {

    final private WarehouseService warehouseService;
    final private WareHouseMapper wareHouseMapper;

    /**
     * 查询库房列表
     * @param state 状态 0-禁用 1-启用
     * @return 库房列表
     * @throws NoSuchMethodException 异常
     */
    @GetMapping("/warehouses")
    public VWareHouseListResponse wareHouseList(@RequestParam("state") Optional<String> state) throws NoSuchMethodException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var list = warehouseService.findWareHouseList(state.orElse("1"),session.getSession().getCompanyCode())
            .stream().map(wareHouseMapper::toVWareHouse)
            .toList();
        return VWareHouseListResponse.builder()
            .code(200)
            .message("获取数据成功")
            .list(list)
            .build();

    }

    /**
     * 库房详情
     * @param code 库房编码
     * @return 库房详情
     */
    @GetMapping("/warehouse/{code}")
    public VWareHouseResponse wareHouseDetail(@PathVariable String code) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var detail = warehouseService.getWareHouseDetail(code,session.getSession().getCompanyCode())
            .map(wareHouseMapper::toVWareHouseDetail);
        return  VWareHouseResponse.builder()
            .code(200)
            .message("获取数据成功")
            .werahouse(detail.get())
            .build();
    }

    /**
     * 保存库房信息
     * @param wareHouse 库房信息
     * @return 返回成功或者失败信息
     */
    @PostMapping("/warehouse")
    public VBaseResponse saveWareHouse(@RequestBody Optional<VWareHouseRequest> wareHouse) throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        warehouseService.saveWareHouse(wareHouse.orElseThrow(()->new NullPointerException("数据为空")),
            session.getSession().getCompanyCode()
        );
        return  VBaseResponse.builder()
            .code(200)
            .message("保存数据成功")
            .build();
    }

    /**
     * 修改库房信息
     * @param wareHouse 库房信息
     * @return 返回成功或者失败信息
     */
    @PutMapping("/warehouse/{code}")
    public VBaseResponse editWareHouse(@PathVariable("code") String code ,
                                       @RequestBody Optional<VWareHouseRequest> wareHouse
    ) throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        warehouseService.editWareHouse(wareHouse.orElseThrow(()->new NullPointerException("数据为空")),
            session.getSession().getCompanyCode(),code
        );
        return  VBaseResponse.builder()
            .code(200)
            .message("保存数据成功")
            .build();
    }

    /**
     * 禁用、启用库房
     * @param code 库房编码
     * @param state 状态 0-禁用 1-启用
     * @return 成功或者失败信息
     * @throws Exception 异常
     */
    @PostMapping("/warehouse/{code}/{state}")
    public VBaseResponse editWareHouse(@PathVariable("code") String code ,
                                       @PathVariable("state") String state
    ) throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map =  warehouseService.editWareHouseState(
            session.getSession().getCompanyCode(),code,state
        );
        return  VBaseResponse.builder()
            .code(Integer.parseInt(map.get("code")))
            .message(map.get("message"))
            .build();
    }

}
