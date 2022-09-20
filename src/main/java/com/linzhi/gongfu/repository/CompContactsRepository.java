package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompContacts;
import com.linzhi.gongfu.entity.CompContactsId;
import com.linzhi.gongfu.enumeration.Availability;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
/**
 * 联系人的Repository
 *
 * @author zgh
 * @create_at 2022-01-21
 */
public interface CompContactsRepository extends CrudRepository<CompContacts, CompContactsId>, QuerydslPredicateExecutor<CompContacts> {

    /**
     * 单位联系人列表
     *
     * @param addressCode 地址编码
     * @param dcCompId    单位编码
     * @param operator    操作员编码
     * @return 联系人列表
     */
    List<CompContacts> findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndCompContactsId_OperatorCodeOrderByContCompName(String addressCode, String dcCompId, String operator);

    /**
     * 最大编码
     *
     * @param dcCompId    单位编码
     * @param addressCode 地址编码
     * @param operator    操作员编码
     * @return 最大编码
     */
    @Query(value = "select  right(('000'+cast((cast(max(right(code,3)) as int)+1) as varchar)),3) " +
        " from comp_contact_person   where dc_comp_id=?1 and addr_code=?2 and operator_code=?3",
        nativeQuery = true)
    String findMaxCode(String dcCompId, String addressCode, String operator);

    /**
     * 更新联系人状态
     *
     * @param state       状态
     * @param dcCompId    单位编码
     * @param code        联系人编码
     * @param addressCode 地址编码
     * @param operator    操作员编码
     */
    @Modifying
    @Query(value = "update comp_contact_person set state=?1 where dc_comp_id=?2 and code in ?3 and addr_code=?4 and operator_code=?5", nativeQuery = true)
    void updateCompContactsStateById(String state, String dcCompId, List<String> code, String addressCode, String operator);

    /**
     * 单位联系人列表
     *
     * @param addressCode 地址编码
     * @param compId      单位编码
     * @param state       状态
     * @return 单位联系人列表
     */
    List<CompContacts> findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndStateOrderByContCompName(String addressCode, String compId, Availability state);

    /**
     * 单位联系人列表
     *
     * @param addressCode 地址编码
     * @param compId      单位编码
     * @param operator    操作员编码
     * @param state       状态
     * @return
     */
    List<CompContacts> findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndCompContactsId_OperatorCodeAndStateOrderByContCompName(String addressCode, String compId, String operator, Availability state);

    /**
     * 联系人详情
     *
     * @param addCode 地址编码
     * @param compId  单位编码
     * @param code    联系人编码
     * @return 联系人详情
     */
    Optional<CompContacts> findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndCompContactsId_Code(String addCode, String compId, String code);
}
