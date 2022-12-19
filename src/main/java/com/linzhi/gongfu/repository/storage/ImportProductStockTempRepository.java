package com.linzhi.gongfu.repository.storage;

import com.linzhi.gongfu.entity.ImportProductStockTemp;
import com.linzhi.gongfu.entity.ImportProductStockTempId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 导入临时产品库存的Repository
 *
 * @author zgh
 * @create_at 2022-12-14
 */
public interface ImportProductStockTempRepository
    extends CrudRepository<ImportProductStockTemp, ImportProductStockTempId>, QuerydslPredicateExecutor<ImportProductStockTemp> {

    /**
     * 删除临时产品
     *
     * @param wareHouseCode 合同或者询价单主键
     * @param dcCompId  单位编码
     */
    @Modifying
    @Query("delete from ImportProductStockTemp as c  where c.importProductStockTempId.werahouseCode=?1 and c.importProductStockTempId.dcCompId=?2")
    void deleteProduct(String wareHouseCode, String dcCompId);

    /**
     * 查找导入到临时库存表中的库存列表或者安全库存列表
     * @param compId 单位编码
     * @param operator 操作员编码
     * @param warehouseCode 仓库编码
     * @param type 类型 1-库存 2-安全库存
     * @return 返回产品库存列表或者安全库存列表
     */
   List<ImportProductStockTemp> findImportProductStockTempByImportProductStockTempId_DcCompIdAndImportProductStockTempId_OperatorAndImpAndImportProductStockTempId_WerahouseCodeAndImportProductStockTempId_Type(String compId,String operator,String warehouseCode,String type);
}
