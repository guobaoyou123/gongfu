package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TCompContacts;
import com.linzhi.gongfu.entity.CompContacts;
import com.linzhi.gongfu.vo.VCompContactsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompContactsMapper {

    @Mapping(target = "code", source = "compContactsId.code")
    @Mapping(target = "dcCompId", source = "compContactsId.dcCompId")
    @Mapping(target = "addrCode", source = "compContactsId.addrCode")
    @Mapping(target = "operatorCode", source = "compContactsId.operatorCode")
    @Mapping(target = "state", expression = "java(String.valueOf(compContacts.getState().getState()))")
    @Mapping(target = "readOnly", constant = "true")
    TCompContacts toTCompContacts(CompContacts compContacts);

    @Mapping(target = "companyName", source = "contCompName")
    @Mapping(target = "name", source = "contName")
    @Mapping(target = "phone", source = "contPhone")
    @Mapping(target = "addressCode", source = "addrCode")
    @Mapping(target = "state", source = "state")
    VCompContactsResponse.Contacts toVContacts(TCompContacts tCompContacts);
}
