package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 用于响应前端对于驱动方式的预加载请求
 *
 * @author zgh
 * @create_at 2022-02-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VOutsideSuppliersResponse extends VBaseResponse{

    List<VOutsideSupplier> suppliers;

    @Data
    public static class VOutsideSupplier{

        private String code;

        private String encode;

        private String companyName;

        private String companyShortName;

        private String usci;

        private String state;

    }
}
