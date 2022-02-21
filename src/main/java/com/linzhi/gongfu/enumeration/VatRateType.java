package com.linzhi.gongfu.enumeration;

/**
 * 用于表示数据库中税率的类型 的枚举
 */
public enum VatRateType {
  GOODS('1'),SERVICE('2');

  private final char type;

  VatRateType(char type){this.type=type;}

  public char getType() {
        return type;
    }
}
