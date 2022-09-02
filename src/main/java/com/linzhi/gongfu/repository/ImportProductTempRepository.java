package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.ImportProductTemp;
import com.linzhi.gongfu.entity.ImportProductTempId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ImportProductTempRepository
    extends CrudRepository<ImportProductTemp, ImportProductTempId>, QuerydslPredicateExecutor<ImportProductTemp> {

    /**
     * 导入临时产品列表
     *
     * @param dcCompId 本单位编码
     * @param operator 操作员编码
     * @param id       合同、询价单主键
     * @return 临时产品列表
     */
    List<ImportProductTemp> findImportProductTempsByImportProductTempId_DcCompIdAndImportProductTempId_OperatorAndImportProductTempId_InquiryId(String dcCompId, String operator, String id);

    /**
     * 删除临时产品
     *
     * @param inquiryId 合同或者询价单主键
     * @param dcCompId  单位编码
     * @param operator  操作员编码
     */
    @Modifying
    @Query("delete from ImportProductTemp as c  where c.importProductTempId.inquiryId=?1 and c.importProductTempId.dcCompId=?2 and c.importProductTempId.operator=?3 ")
    void deleteProduct(String inquiryId, String dcCompId, String operator);
}
