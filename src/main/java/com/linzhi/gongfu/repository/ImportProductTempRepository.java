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


    List<ImportProductTemp> findImportProductTempsByImportProductTempId_DcCompIdAndImportProductTempId_OperatorAndImportProductTempId_InquiryId(String dcCompId,String operator,String id );
    @Modifying
    @Query("delete from ImportProductTemp as c  where c.importProductTempId.inquiryId=?1 and c.importProductTempId.dcCompId=?2 and c.importProductTempId.operator=?3 ")
    void  deleteProduct(String inquiryId,String dcCompId,String operator);
}
