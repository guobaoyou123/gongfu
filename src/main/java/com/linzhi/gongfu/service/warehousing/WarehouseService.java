package com.linzhi.gongfu.service.warehousing;

import com.linzhi.gongfu.converter.AvailabilityConverter;
import com.linzhi.gongfu.dto.TWareHouse;
import com.linzhi.gongfu.entity.WareHouse;
import com.linzhi.gongfu.entity.WareHouseOperator;
import com.linzhi.gongfu.entity.WareHouseOperatorId;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.mapper.warehousing.WareHouseMapper;
import com.linzhi.gongfu.repository.warehousing.ProductStockRepository;
import com.linzhi.gongfu.repository.warehousing.WareHouseOperatorRepository;
import com.linzhi.gongfu.repository.warehousing.WareHouseRepository;
import com.linzhi.gongfu.service.AddressService;
import com.linzhi.gongfu.vo.warehousing.VWareHouseRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

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
    final private AddressService addressService;
    final private WareHouseOperatorRepository wareHouseOperatorRepository;


    /**
     * 查找库房列表
     *
     * @param state       状态 0-禁用 1-启用
     * @param companyCode 公司编码
     * @return 返回库房列表
     */
    @Cacheable(value = "WareHouse_List;1800", unless = "#result == null ", key = "#companyCode+'_'+#state")
    public List<TWareHouse> findWareHouseList(String state, String companyCode) throws NoSuchMethodException {

        return wareHouseRepository.findWareHouseByCompIdAndState
                (companyCode,
                    new AvailabilityConverter().convertToEntityAttribute(state.toCharArray()[0])
                ).stream().map(wareHouseMapper::toTWareHouse)
            .toList();
    }

    /**
     * 库房详情
     *
     * @param code 库房编码
     * @return 库房详情
     */
    @Cacheable(value = "WareHouse_Detail;1800", unless = "#result == null ", key = "#code")
    public Optional<TWareHouse> getWareHouseDetail(String code, String companyCode) {
        var wareHouse = wareHouseRepository.findById(code)
            .map(wareHouseMapper::toTWareHouse);
        //查询库房是否已经初始化
        int count = productStockRepository.countProductStocksByProductStockId_CompIdAndProductStockId_WarehouseCode(companyCode, code);
        if (count > 0) {
            wareHouse.get().setType("1");
        }
        // TODO 需要查询该库是否有出入库单
        return wareHouseRepository.findById(code)
            .map(wareHouseMapper::toTWareHouse);
    }

    /**
     * 保存库房信息
     *
     * @param wareHouse 库房信息
     * @param companyCode 单位编码
     * @throws Exception 异常
     */
    @CacheEvict(value = "WareHouse_List;1800", key = "#companyCode+'_1'")
    @Transactional
    public void saveWareHouse(VWareHouseRequest wareHouse, String companyCode) throws Exception {
        try {
            //编码 单位号+两位数序号，查找库里最大编号
            var maxCode = wareHouseRepository.findMaxCode(companyCode);
            if (maxCode == null) {
                maxCode = companyCode + "01";
            }
            //查找区域名称
            List<WareHouseOperator> operatorList = new ArrayList<>();
            for (String s : wareHouse.getAuthorizedOperators()) {
                operatorList.add(WareHouseOperator.builder()
                    .wareHouseOperatorId(
                        WareHouseOperatorId.builder()
                            .code(maxCode)
                            .compId(companyCode)
                            .operatorCode(s)
                            .build()
                    )
                    .build());
            }
            String areaName = addressService.findByCode("",wareHouse.getAreaCode());
            var wareHouseDetail = WareHouse.builder()
                .code(maxCode)
                .compId(companyCode)
                .acreage(wareHouse.getAcreage())
                .address(wareHouse.getAddress())
                .areaCode(wareHouse.getAreaCode())
                .areaName(areaName)
                .name(wareHouse.getName())
                .operatorList(operatorList)
                .state(Availability.ENABLED)
                .createdAt(LocalDateTime.now())
                .build();
            wareHouseRepository.save(wareHouseDetail);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    /**
     * 保存库房信息
     *
     * @param wareHouse 库房信息
     * @param companyCode 单位编码
     * @throws Exception 异常
     */
    @Caching(evict = {
        @CacheEvict(value = "WareHouse_List;1800", key = "#companyCode+'_1'"),
        @CacheEvict(value = "WareHouse_Detail",key = "#code")
    })
    @Transactional
    public void editWareHouse(VWareHouseRequest wareHouse, String companyCode,String code) throws Exception {
        try {
            var house = wareHouseRepository.findById(code).orElseThrow(()->new IOException("没有找到数据"));
            //删除授权操作员
            wareHouseOperatorRepository.deleteWareHouseOperatorByWareHouseOperatorId_CodeAndWareHouseOperatorId_CompId(code,companyCode);
            //查找区域名称
            List<WareHouseOperator> operatorList = new ArrayList<>();
            for (String s : wareHouse.getAuthorizedOperators()) {
                operatorList.add(WareHouseOperator.builder()
                    .wareHouseOperatorId(
                        WareHouseOperatorId.builder()
                            .code(code)
                            .compId(companyCode)
                            .operatorCode(s)
                            .build()
                    )
                    .build());
            }
            String areaName = addressService.findByCode("",wareHouse.getAreaCode());
            house.setAreaCode(wareHouse.getAreaCode());
            house.setAreaName(areaName);
            house.setAcreage(house.getAcreage());
            house.setAddress(house.getAddress());
           // house.setOperatorList(operatorList);
            wareHouseRepository.save(house);
            wareHouseOperatorRepository.saveAll(operatorList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    /**
     * 修改库房状态
     * @param companyCode 单位编码
     * @param code 库房编码
     * @param state 状态
     * @throws Exception 异常
     */
     @CacheEvict(value = "WareHouse_List;1800", key = "#companyCode+'_*'")
    @Transactional
    public Map<String,String> editWareHouseState(String companyCode, String code, String state) throws Exception {
        Map<String,String> map = new HashMap<>();
         try {
            if(state.equals("0")){
                //判断可发库存是否等于0
               var amount= productStockRepository.getVendibleStock(code,companyCode);
               if(amount!=null&&amount.floatValue()>0){
                   map.put("code","207");
                   map.put("message","可发库存大于0");
                   return map;
               }
                //TODO:判断是否存在出库单
            }
            wareHouseRepository.updateState(new AvailabilityConverter().convertToEntityAttribute(state.toCharArray()[0]),code);
             map.put("code","200");
             map.put("message","更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }

         return map;
    }

    /**
     * 导出产品库存模板
     *
     * @return 产品库存列表
     */
    public List<LinkedHashMap<String, Object>> exportProductStockTemplate() {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        try {
            LinkedHashMap<String, Object> m = new LinkedHashMap<>();
            m.put("产品代码", "");
            m.put("库存", "");
            list.add(m);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
