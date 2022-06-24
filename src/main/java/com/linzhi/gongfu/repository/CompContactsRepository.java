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

public interface CompContactsRepository extends CrudRepository<CompContacts, CompContactsId>, QuerydslPredicateExecutor<CompContacts> {

    List<CompContacts> findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndCompContactsId_OperatorCodeOrderByContCompName(String addressCode, String dcCompId, String operator) ;

    @Query(value="select  right(('000'+cast((cast(max(right(code,3)) as int)+1) as varchar)),3) " +
        " from comp_contact_person   where dc_comp_id=?1 and addr_code=?2 and operator_code=?3",
        nativeQuery = true)
    String findMaxCode(String dcCompId,String addressCode,String operator);
    @Modifying
    @Query(value="update comp_contact_person set state=?1 where dc_comp_id=?2 and code in ?3 and addr_code=?4 and operator_code=?5",nativeQuery = true)
    void updateCompContactsStateById(String state, String dcCompId,List<String> code ,String addressCode,String operator);

    List<CompContacts> findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndStateOrderByContCompName(String addressCode,String compId,Availability state);

    List<CompContacts> findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndCompContactsId_OperatorCodeAndStateOrderByContCompName(String addressCode,String compId,String operator,Availability state);

    Optional<CompContacts> findCompContactsByCompContactsId_AddrCodeAndCompContactsId_DcCompIdAndCompContactsId_Code(String addCode,String compId,String code);
}
