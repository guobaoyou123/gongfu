package com.linzhi.gongfu.mapper;

import java.time.LocalDateTime;

import com.linzhi.gongfu.entity.OperatorId;
import com.linzhi.gongfu.entity.Session;
import com.linzhi.gongfu.security.token.OperatorSessionToken;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface SessionMapper {
    OperatorId toOperatorId(Session session);

    OperatorId toOperatorId(OperatorSessionToken.Session session);
}
