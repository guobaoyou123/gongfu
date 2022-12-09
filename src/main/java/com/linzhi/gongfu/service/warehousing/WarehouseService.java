package com.linzhi.gongfu.service.warehousing;

import com.linzhi.gongfu.converter.AvailabilityConverter;
import com.linzhi.gongfu.dto.TWareHouse;
import com.linzhi.gongfu.mapper.warehousing.WareHouseMapper;
import com.linzhi.gongfu.repository.warehousing.ProductStockRepository;
import com.linzhi.gongfu.repository.warehousing.WareHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 仓库信息及处理业务服务
 *
 * @author zgh
 * @create_at 2022-12-09
 */
@RequiredArgsConstructor
@Service
public class WarehouseService {

    final private WareHouseRepository wareHouseRepository;
    final private WareHouseMapper wareHouseMapper;
    final private ProductStockRepository productStockRepository;

    /**
     * 查找库房列表
     *
     * @param state       状态 0-禁用 1-启用
     * @param companyCode 公司编码
     * @return 返回库房列表
     */
    @Cacheable(value = "WareHouse_List;1800", unless = "#result == null ", key = "#companyCode+'_'+#state")
    public List<TWareHouse> findWareHouseList(String state, String companyCode) throws NoSuchMethodException {

        return wareHouseRepository.findWareHouseByCompIdAndSate
                (companyCode,
                    new AvailabilityConverter().convertToEntityAttribute(state.toCharArray()[0])
                ).stream().map(wareHouseMapper::toTWareHouse)
            .toList();
    }

    /**
     * 库房详情
     * @param code 库房编码
     * @return 库房详情
     */
    @Cacheable(value = "WareHouse_Detail;1800", unless = "#result == null ", key = "#code")
    public Optional<TWareHouse>  getWareHouseDetail(String code,String companyCode){
        var wareHouse = wareHouseRepository.findById(code)
            .map(wareHouseMapper::toTWareHouse);
        //查询库房是否已经初始化
        int count =  productStockRepository.countProductStocksByProductStockId_CompIdAndProductStockId_WarehouseCode(companyCode,code);
        if(count>0){
            wareHouse.get().setType("1");
        }
        // TODO 需要查询该库是否有出入库单
        return  wareHouseRepository.findById(code)
              .map(wareHouseMapper::toTWareHouse);
    }
}
