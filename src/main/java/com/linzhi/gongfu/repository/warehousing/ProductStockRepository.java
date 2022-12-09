package com.linzhi.gongfu.repository.warehousing;

import com.linzhi.gongfu.entity.ProductStock;
import com.linzhi.gongfu.entity.ProductStockId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 产品库存的Repository
 */
public interface ProductStockRepository extends CrudRepository<ProductStock, ProductStockId>, QuerydslPredicateExecutor<ProductStockId> {

    /**
     * 根据库房编码查询该库初始化产品数量
     * @param companyCode 单位编码
     * @param wareHouseCode 仓库编码
     * @return 数量
     */
      int countProductStocksByProductStockId_CompIdAndProductStockId_WarehouseCode(String companyCode,String wareHouseCode);
}
