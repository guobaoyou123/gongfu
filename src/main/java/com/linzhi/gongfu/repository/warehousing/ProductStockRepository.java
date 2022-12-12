package com.linzhi.gongfu.repository.warehousing;

import com.linzhi.gongfu.entity.ProductStock;
import com.linzhi.gongfu.entity.ProductStockId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;

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

      @Query(value = "select sum(phy_stock)-sum(not_out_stock) from product_stock\n" +
          "\n" +
          "where warehouse_code=?1 and comp_id=?2",nativeQuery = true)
      BigDecimal  getVendibleStock(String wareHouseCode,String companyCode);
}
