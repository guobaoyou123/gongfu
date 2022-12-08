package com.linzhi.gongfu.repository.trade;

import com.linzhi.gongfu.entity.PreferenceSupplier;
import com.linzhi.gongfu.entity.PreferenceSupplierId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PreferenceSupplierRepository extends CrudRepository<PreferenceSupplier, PreferenceSupplierId>, QuerydslPredicateExecutor<PreferenceSupplier> {

    @Modifying
    void deleteByPreferenceSupplierId_CompCodeAndPreferenceSupplierId_BrandCode(String companyCode,String brandCode);

    /**
     * 根据品牌编码列表查找有优选供应商
     * @param companyCode 公司编码
     * @param brands 品牌编码列表
     * @return 优选供应商列表
     */
    List<PreferenceSupplier> findByPreferenceSupplierId_CompCodeAndPreferenceSupplierId_BrandCodeInOrderBySortAsc(String companyCode,List<String> brands);
}
