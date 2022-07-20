package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class CompInvitationCodeId implements Serializable {

    /**
     * 品牌编码
     */
    @Column(name="dc_comp_id",length = 40,nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String dcCompId;
    /**
     * 被授权单位id
     */
    @Column(name="invitation_code",length = 50,nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String invitationCode;
}
