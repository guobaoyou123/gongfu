package com.linzhi.gongfu.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Jacksonized
@Data
@NoArgsConstructor
public class VPlanDemandRequest implements Serializable {
        private String planCode;
        private String productId;
        private String supplierCode;
        private BigDecimal demand;

}
