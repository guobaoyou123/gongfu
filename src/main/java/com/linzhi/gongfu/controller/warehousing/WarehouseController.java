package com.linzhi.gongfu.controller.warehousing;

import com.linzhi.gongfu.mapper.warehousing.WareHouseMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.warehousing.WarehouseService;
import com.linzhi.gongfu.vo.warehousing.VWareHouseListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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



}
