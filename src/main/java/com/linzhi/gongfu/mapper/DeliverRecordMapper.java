package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.entity.DeliverRecord;
import com.linzhi.gongfu.entity.DeliverTemp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliverRecordMapper {
    @Mapping(target = "deliverRecordId.code", source = "deliverTempId.code")
    @Mapping(target = "deliverRecordId.deliverCode", constant = "1")
    DeliverRecord toDeliverRecord(DeliverTemp deliverTemp);
}
