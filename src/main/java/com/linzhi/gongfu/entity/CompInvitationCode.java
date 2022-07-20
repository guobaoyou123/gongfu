package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.TradeApply;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 生成邀请码表
 *
 * @author zgh
 * @create_at 2022-07-19
 */
@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comp_invitation_code")
public class CompInvitationCode implements Serializable {

    @EmbeddedId
    private CompInvitationCodeId compInvitationCodeId;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
