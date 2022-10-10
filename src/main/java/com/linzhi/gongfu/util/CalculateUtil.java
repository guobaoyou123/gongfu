package com.linzhi.gongfu.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 合同中计算价格工具类
 */
public abstract class CalculateUtil {

    /**
     * 计算未税单价
     *
     * @param price   单价
     * @param vatRate 税率
     * @return 未税单价
     */
    public static BigDecimal calculateUntaxedUnitPrice(BigDecimal price, BigDecimal vatRate) {
        return price.divide(new BigDecimal("1").add(vatRate), 4, RoundingMode.HALF_UP);
    }

    /**
     * 计算含税单价
     *
     * @param price   单价
     * @param vatRate 税率
     * @return 含税单价
     */
    public static BigDecimal calculateTaxedUnitPrice(BigDecimal price, BigDecimal vatRate) {
        return price.multiply(new BigDecimal("1").add(vatRate))
            .setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * 计算小计
     *
     * @param price  单价
     * @param amount 数量
     * @return 小计
     */
    public static BigDecimal calculateSubtotal(BigDecimal price, BigDecimal amount) {
        return price.multiply(amount).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算折扣后单价
     *
     * @param price    单价
     * @param discount 折扣
     * @return 折扣后单价
     */
    public static BigDecimal calculateDiscountedPrice(BigDecimal price, BigDecimal discount) {
        return price.multiply(new BigDecimal("1").subtract(discount)).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * 计算折扣后小计
     *
     * @param price    单价
     * @param amount   数量
     * @param discount 折扣
     * @return 折扣后小计
     */
    public static BigDecimal calculateDiscountedSubtotal(BigDecimal price, BigDecimal discount, BigDecimal amount) {
        return price.multiply(new BigDecimal("1").subtract(discount)).multiply(amount).setScale(2, RoundingMode.HALF_UP);
    }
}
