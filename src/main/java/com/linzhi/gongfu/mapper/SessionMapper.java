package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.entity.DOperatorId;
import com.linzhi.gongfu.entity.DSession;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface SessionMapper {
    DOperatorId toOperatorId(DSession session);

    DOperatorId toOperatorId(OperatorSessionToken.Session session);
}
