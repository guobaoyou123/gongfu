package com.linzhi.gongfu.enumeration;

/**
 * 用于表示消息通知类型 0-申请采购 1-修改交易信息 2-申请结果通知  3-呼叫 4-应答
 *
 * @author zhangguanghua
 * @create_at 2022-07-20
 */
public enum NotificationType {
    ENROLLED_APPLY('0'), MODIFY_TRADE('1'), ENROLLED_APPLY_HISTORY('2'),INQUIRY_CALL('3'),INQUIRY_RESPONSE('4')
    ;

    private final char type;

    NotificationType(char type) {
        this.type = type;
    }

    public char getType() {
        return type;
    }
}
