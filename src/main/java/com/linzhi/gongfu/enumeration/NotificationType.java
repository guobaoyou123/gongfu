package com.linzhi.gongfu.enumeration;

/**
 * 用于表示消息通知类型
 * @author zhangguanghua
 * @create_at 2022-07-20
 */
public enum NotificationType {
    ENROLLED_APPLY('0'),  MODIFY_TRADE('1'),ENROLLED_APPLY_HISTORY('2'),;

    private final char type;

    NotificationType(char type) {
        this.type = type;
    }

    public char getType() {
        return type;
    }
}
