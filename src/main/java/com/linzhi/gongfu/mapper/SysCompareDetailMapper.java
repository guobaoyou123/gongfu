package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TCompareDetail;
import com.linzhi.gongfu.entity.SysCompareDetail;
import com.linzhi.gongfu.vo.VConnectionsResponse;
import com.linzhi.gongfu.vo.VDriversResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface SysCompareDetailMapper {

    @Mapping(target = "code",source = "sysCompareDetailId.code")
    @Mapping(target = "name",source = "chiName")
    TCompareDetail toCompareDetail(SysCompareDetail compareDetail);

    VDriversResponse.VDrives toPreloadDriver(TCompareDetail tCompareDetail);
    VConnectionsResponse.VConnections toPreloadConnection(TCompareDetail tCompareDetail);
}
