package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Jacksonized
@NoArgsConstructor
public class VTemporaryPlanRequest implements Serializable {

       private String productId ;
       private BigDecimal demand;


}
