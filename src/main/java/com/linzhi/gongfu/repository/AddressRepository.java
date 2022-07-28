package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Address;
import com.linzhi.gongfu.entity.AddressId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AddressRepository extends CrudRepository<Address, AddressId>, QuerydslPredicateExecutor<Address> {

    /**
     * 查询最大编号
     * @param dcCompId 公司编码
     * @return 返回最大编号
     */
    @Query(value="select  right(('000'+cast((cast(max(right(code,3)) as int)+1) as varchar)),3) " +
        " from comp_address  where dc_comp_id=?1",
        nativeQuery = true)
    String findMaxCode(String dcCompId);

    /**
     * 更新地址信息
     * @param flag 是否为本部地址
     * @param dcCompId 公司编码
     */
    @Modifying
    @Query(value="update comp_address set flag=?1 where dc_comp_id=?2 ",nativeQuery = true)
    void updateAddressById(String  flag,String dcCompId);

    /**
     * 更新地址状态
     * @param state 状态
     * @param dcCompId 公司编码
     * @param code 地址编码
     */
    @Modifying
    @Query(value="update comp_address set state=?1 where dc_comp_id=?2 and code in ?3",nativeQuery = true)
    void updateAddressStateById(String state, String dcCompId,List<String> code);

    List<Address> findAddressByAreaCodeAndAddressLikeAndAddressId_DcCompId(String area,String address,String dcCompId);

}
